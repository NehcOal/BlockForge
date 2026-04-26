package com.blockforge.connector.material.source;

import com.blockforge.common.material.source.MaterialSourceConfig;
import com.blockforge.common.material.source.MaterialSourceItemEntry;
import com.blockforge.common.material.source.MaterialSourcePlanner;
import com.blockforge.common.material.source.MaterialSourceRef;
import com.blockforge.common.material.source.MaterialSourceReport;
import com.blockforge.common.material.source.MaterialSourceType;
import com.blockforge.connector.material.MaterialReport;
import com.blockforge.connector.material.MaterialRequirement;
import com.blockforge.connector.material.PlayerInventoryMaterialChecker;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NeoForgeMaterialSourceAdapter {
    private final PlayerInventoryMaterialChecker inventoryChecker = new PlayerInventoryMaterialChecker();

    public MaterialSourceReport report(
            MaterialReport baseReport,
            ServerPlayer player,
            List<NeoForgeContainerMaterialSource> containers,
            MaterialSourceConfig config
    ) {
        List<MaterialSourceItemEntry> entries = new ArrayList<>();
        Map<String, Integer> playerCounts = player == null ? Map.of() : inventoryChecker.inventoryCounts(player);
        MaterialSourceRef playerSource = MaterialSourceRef.playerInventory(
                player == null ? "player_inventory" : player.getUUID().toString(),
                player == null ? "Player Inventory" : player.getGameProfile().getName()
        );

        for (MaterialRequirement requirement : baseReport.requirements()) {
            if (!requirement.consumable()) {
                continue;
            }

            int playerAvailable = playerCounts.getOrDefault(requirement.itemId(), 0);
            entries.add(new MaterialSourceItemEntry(
                    requirement.itemId(),
                    requirement.required(),
                    playerAvailable,
                    0,
                    0,
                    playerSource
            ));

            if (containers != null) {
                for (NeoForgeContainerMaterialSource container : containers) {
                    int available = container.countItem(requirement.itemId());
                    if (available <= 0) {
                        continue;
                    }
                    entries.add(new MaterialSourceItemEntry(
                            requirement.itemId(),
                            requirement.required(),
                            available,
                            0,
                            0,
                            container.ref()
                    ));
                }
            }
        }

        return MaterialSourcePlanner.fromAvailableEntries(toCommonReport(baseReport), config, entries);
    }

    public int reservedFrom(MaterialSourceReport report, MaterialSourceType type) {
        if (report == null) {
            return 0;
        }

        return report.entries()
                .stream()
                .filter(entry -> entry.source() != null && entry.source().type() == type)
                .mapToInt(MaterialSourceItemEntry::reserved)
                .sum();
    }

    private com.blockforge.common.material.MaterialReport toCommonReport(MaterialReport report) {
        List<com.blockforge.common.material.MaterialRequirement> requirements = report.requirements()
                .stream()
                .map(requirement -> new com.blockforge.common.material.MaterialRequirement(
                        requirement.blockStateKey(),
                        requirement.blockId(),
                        requirement.itemId(),
                        requirement.required(),
                        requirement.available(),
                        requirement.missing()
                ))
                .toList();

        return new com.blockforge.common.material.MaterialReport(
                report.blueprintId(),
                report.totalBlocks(),
                report.totalRequiredItems(),
                report.totalAvailableItems(),
                report.enoughMaterials(),
                requirements
        );
    }
}
