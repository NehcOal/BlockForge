package com.blockforge.common.material;

import com.blockforge.common.blueprint.BlueprintBlock;
import com.blockforge.common.blueprint.BlueprintPaletteEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MaterialCounter {
    private final Function<String, String> itemIdResolver;

    public MaterialCounter(Function<String, String> itemIdResolver) {
        this.itemIdResolver = itemIdResolver;
    }

    public MaterialReport withAvailability(
            String blueprintId,
            List<? extends BlueprintBlock> blocks,
            Map<String, ? extends BlueprintPaletteEntry> palette,
            Map<String, Integer> availableItems
    ) {
        Map<String, MutableRequirement> requirements = new HashMap<>();

        for (BlueprintBlock block : blocks) {
            BlueprintPaletteEntry paletteEntry = palette.get(block.getState());
            if (paletteEntry == null || paletteEntry.name() == null || paletteEntry.name().isBlank()) {
                continue;
            }

            String blockId = paletteEntry.name();
            String countedItemId = itemIdResolver.apply(blockId);
            String resolvedItemId = countedItemId == null ? "minecraft:air" : countedItemId;

            MutableRequirement requirement = requirements.computeIfAbsent(
                    resolvedItemId,
                    ignored -> new MutableRequirement(block.getState(), blockId, resolvedItemId)
            );
            requirement.required++;
        }

        List<MaterialRequirement> resolvedRequirements = requirements.values()
                .stream()
                .map(requirement -> requirement.toRequirement(availableItems.getOrDefault(requirement.itemId, 0)))
                .sorted((left, right) -> left.itemId().compareTo(right.itemId()))
                .toList();

        int totalRequired = resolvedRequirements.stream()
                .filter(MaterialRequirement::consumable)
                .mapToInt(MaterialRequirement::required)
                .sum();
        int totalAvailable = resolvedRequirements.stream()
                .filter(MaterialRequirement::consumable)
                .mapToInt(requirement -> Math.min(requirement.available(), requirement.required()))
                .sum();
        boolean enough = resolvedRequirements.stream()
                .filter(MaterialRequirement::consumable)
                .allMatch(requirement -> requirement.missing() == 0);

        return new MaterialReport(
                blueprintId,
                blocks.size(),
                totalRequired,
                totalAvailable,
                enough,
                resolvedRequirements
        );
    }

    private static final class MutableRequirement {
        private final String blockStateKey;
        private final String blockId;
        private final String itemId;
        private int required;

        private MutableRequirement(String blockStateKey, String blockId, String itemId) {
            this.blockStateKey = blockStateKey;
            this.blockId = blockId;
            this.itemId = itemId;
        }

        private MaterialRequirement toRequirement(int available) {
            return new MaterialRequirement(
                    blockStateKey,
                    blockId,
                    itemId,
                    required,
                    available,
                    Math.max(0, required - available)
            );
        }
    }
}
