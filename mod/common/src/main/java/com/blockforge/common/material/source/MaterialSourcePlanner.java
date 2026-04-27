package com.blockforge.common.material.source;

import com.blockforge.common.material.MaterialReport;
import com.blockforge.common.material.MaterialRequirement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class MaterialSourcePlanner {
    private static final MaterialSourceRef DEFAULT_PLAYER_SOURCE =
            MaterialSourceRef.playerInventory("player_inventory", "Player Inventory");

    private MaterialSourcePlanner() {
    }

    public static MaterialSourceReport plan(
            MaterialReport baseMaterialReport,
            MaterialSourceConfig config,
            List<MaterialSourceRef> availableSources
    ) {
        MaterialSourceConfig resolvedConfig = config == null ? MaterialSourceConfig.defaults() : config;
        List<MaterialSourceRef> sources = availableSources == null ? List.of() : List.copyOf(availableSources);
        MaterialSourceRef playerSource = sources.stream()
                .filter(source -> source.type() == MaterialSourceType.PLAYER_INVENTORY)
                .findFirst()
                .orElse(DEFAULT_PLAYER_SOURCE);
        boolean hasContainers = sources.stream().anyMatch(source -> source.type() == MaterialSourceType.NEARBY_CONTAINER);

        List<String> warnings = new ArrayList<>();
        if (hasContainers && !resolvedConfig.enableNearbyContainers()) {
            warnings.add("Nearby container sources were provided but nearby containers are disabled.");
        } else if (hasContainers) {
            warnings.add("Nearby container sources require loader inventory details before they can provide availability.");
        }

        List<MaterialSourceItemEntry> entries = new ArrayList<>();
        for (MaterialRequirement requirement : baseMaterialReport.requirements()) {
            if (!requirement.consumable()) {
                continue;
            }

            int available = resolvedConfig.priority() == MaterialSourcePriority.CONTAINER_ONLY
                    ? 0
                    : requirement.available();
            int reserved = Math.min(requirement.required(), available);
            entries.add(new MaterialSourceItemEntry(
                    requirement.itemId(),
                    requirement.required(),
                    available,
                    reserved,
                    0,
                    playerSource
            ));
        }

        return report(baseMaterialReport.blueprintId(), entries, warnings);
    }

    public static MaterialSourceReport fromAvailableEntries(
            MaterialReport baseMaterialReport,
            MaterialSourceConfig config,
            List<MaterialSourceItemEntry> availableEntries
    ) {
        MaterialSourceConfig resolvedConfig = config == null ? MaterialSourceConfig.defaults() : config;
        List<MaterialSourceItemEntry> sourceEntries = availableEntries == null ? List.of() : List.copyOf(availableEntries);
        Map<String, List<MaterialSourceItemEntry>> byItemId = sourceEntries.stream()
                .collect(Collectors.groupingBy(MaterialSourceItemEntry::itemId));

        List<MaterialSourceItemEntry> entries = new ArrayList<>();
        for (MaterialRequirement requirement : baseMaterialReport.requirements()) {
            if (!requirement.consumable()) {
                continue;
            }

            int remaining = requirement.required();
            boolean addedEntry = false;
            List<MaterialSourceItemEntry> candidates = byItemId.getOrDefault(requirement.itemId(), List.of())
                    .stream()
                    .sorted(sourceComparator(resolvedConfig.priority()))
                    .toList();

            for (MaterialSourceItemEntry candidate : candidates) {
                if (remaining <= 0) {
                    break;
                }
                if (!sourceAllowed(candidate.source(), resolvedConfig)) {
                    continue;
                }

                int reserved = Math.min(remaining, candidate.available());
                entries.add(new MaterialSourceItemEntry(
                        requirement.itemId(),
                        requirement.required(),
                        candidate.available(),
                        reserved,
                        0,
                        candidate.source()
                ));
                addedEntry = true;
                remaining -= reserved;

                if (!resolvedConfig.allowPartialFromContainers()
                        && candidate.source() != null
                        && candidate.source().type() == MaterialSourceType.NEARBY_CONTAINER
                        && remaining > 0) {
                    break;
                }
            }

            if (!addedEntry) {
                entries.add(new MaterialSourceItemEntry(
                        requirement.itemId(),
                        requirement.required(),
                        0,
                        0,
                        0,
                        null
                ));
            }
        }

        return report(baseMaterialReport.blueprintId(), entries, List.of());
    }

    private static MaterialSourceReport report(
            String blueprintId,
            List<MaterialSourceItemEntry> entries,
            List<String> warnings
    ) {
        Map<String, List<MaterialSourceItemEntry>> entriesByItem = entries.stream()
                .collect(Collectors.groupingBy(MaterialSourceItemEntry::itemId));
        int totalRequired = entriesByItem.values()
                .stream()
                .mapToInt(itemEntries -> itemEntries.stream().mapToInt(MaterialSourceItemEntry::required).max().orElse(0))
                .sum();
        int totalAvailable = entriesByItem.values()
                .stream()
                .mapToInt(itemEntries -> itemEntries.stream().mapToInt(MaterialSourceItemEntry::available).sum())
                .sum();
        int totalReserved = entriesByItem.values()
                .stream()
                .mapToInt(itemEntries -> itemEntries.stream().mapToInt(MaterialSourceItemEntry::reserved).sum())
                .sum();
        int totalMissing = Math.max(0, totalRequired - totalReserved);

        return new MaterialSourceReport(
                blueprintId,
                totalRequired,
                totalAvailable,
                totalMissing,
                totalMissing == 0,
                entries,
                warnings
        );
    }

    private static Comparator<MaterialSourceItemEntry> sourceComparator(MaterialSourcePriority priority) {
        return Comparator.comparingInt(entry -> sourceRank(entry.source(), priority));
    }

    private static int sourceRank(MaterialSourceRef source, MaterialSourcePriority priority) {
        MaterialSourceType type = source == null ? MaterialSourceType.PLAYER_INVENTORY : source.type();
        return switch (priority) {
            case CONTAINER_FIRST, CONTAINER_ONLY -> type == MaterialSourceType.NEARBY_CONTAINER ? 0 : 1;
            case PLAYER_FIRST, PLAYER_ONLY -> type == MaterialSourceType.PLAYER_INVENTORY ? 0 : 1;
        };
    }

    private static boolean sourceAllowed(MaterialSourceRef source, MaterialSourceConfig config) {
        MaterialSourceType type = source == null ? MaterialSourceType.PLAYER_INVENTORY : source.type();
        return switch (config.priority()) {
            case PLAYER_ONLY -> type == MaterialSourceType.PLAYER_INVENTORY;
            case CONTAINER_ONLY -> type == MaterialSourceType.NEARBY_CONTAINER && config.enableNearbyContainers();
            case PLAYER_FIRST, CONTAINER_FIRST -> type == MaterialSourceType.PLAYER_INVENTORY
                    || (type == MaterialSourceType.NEARBY_CONTAINER && config.enableNearbyContainers());
        };
    }
}
