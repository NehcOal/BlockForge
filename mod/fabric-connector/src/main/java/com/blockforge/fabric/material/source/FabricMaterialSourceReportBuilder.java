package com.blockforge.fabric.material.source;

import com.blockforge.common.material.MaterialReport;
import com.blockforge.common.material.MaterialRequirement;
import com.blockforge.common.material.source.MaterialSourceConfig;
import com.blockforge.common.material.source.MaterialSourceItemEntry;
import com.blockforge.common.material.source.MaterialSourcePlanner;
import com.blockforge.common.material.source.MaterialSourceRef;
import com.blockforge.common.material.source.MaterialSourceReport;
import com.blockforge.common.material.source.MaterialSourceType;
import com.blockforge.fabric.material.FabricPlayerInventoryMaterialChecker;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FabricMaterialSourceReportBuilder {
    private final FabricPlayerInventoryMaterialChecker checker = new FabricPlayerInventoryMaterialChecker();

    public MaterialSourceReport report(
            MaterialReport baseReport,
            ServerPlayerEntity player,
            List<FabricContainerMaterialSource> containers,
            MaterialSourceConfig config
    ) {
        Map<String, Integer> playerCounts = player == null ? Map.of() : checker.inventoryCounts(player);
        MaterialSourceRef playerSource = MaterialSourceRef.playerInventory(
                player == null ? "player_inventory" : player.getUuidAsString(),
                player == null ? "Player Inventory" : player.getName().getString()
        );
        List<MaterialSourceItemEntry> entries = new ArrayList<>();

        for (MaterialRequirement requirement : baseReport.requirements()) {
            if (!requirement.consumable()) {
                continue;
            }

            entries.add(new MaterialSourceItemEntry(
                    requirement.itemId(),
                    requirement.required(),
                    playerCounts.getOrDefault(requirement.itemId(), 0),
                    0,
                    0,
                    playerSource
            ));

            if (containers == null) {
                continue;
            }
            for (FabricContainerMaterialSource container : containers) {
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

        return MaterialSourcePlanner.fromAvailableEntries(baseReport, config, entries);
    }

    public int reservedFrom(MaterialSourceReport report, MaterialSourceType type) {
        return report == null ? 0 : report.entries()
                .stream()
                .filter(entry -> entry.source() != null && entry.source().type() == type)
                .mapToInt(MaterialSourceItemEntry::reserved)
                .sum();
    }
}
