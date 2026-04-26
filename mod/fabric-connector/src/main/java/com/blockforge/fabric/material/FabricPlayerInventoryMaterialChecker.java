package com.blockforge.fabric.material;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.material.MaterialReport;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.util.HashMap;
import java.util.Map;

public class FabricPlayerInventoryMaterialChecker {
    public static final boolean REQUIRE_MATERIALS_IN_SURVIVAL = true;
    public static final boolean CREATIVE_MODE_BYPASSES_MATERIALS = true;
    public static final boolean ALLOW_BUILD_IN_ADVENTURE_MODE = false;
    public static final boolean ALLOW_BUILD_IN_SPECTATOR_MODE = false;

    private final FabricMaterialCounter counter = new FabricMaterialCounter();

    public AccessResult canBuild(ServerPlayerEntity player) {
        GameMode gameMode = player.interactionManager.getGameMode();
        if (gameMode == GameMode.SPECTATOR && !ALLOW_BUILD_IN_SPECTATOR_MODE) {
            return AccessResult.denied("Spectator mode cannot build BlockForge Fabric blueprints.");
        }
        if (gameMode == GameMode.ADVENTURE && !ALLOW_BUILD_IN_ADVENTURE_MODE) {
            return AccessResult.denied("Adventure mode cannot build BlockForge Fabric blueprints.");
        }
        return AccessResult.permitted();
    }

    public MaterialReport report(Blueprint blueprint, ServerPlayerEntity player) {
        if (player == null) {
            return counter.count(blueprint);
        }
        if (isCreativeBypass(player)) {
            MaterialReport baseReport = counter.count(blueprint);
            return new MaterialReport(
                    baseReport.blueprintId(),
                    baseReport.totalBlocks(),
                    baseReport.totalRequiredItems(),
                    baseReport.totalRequiredItems(),
                    true,
                    baseReport.requirements()
                            .stream()
                            .map(requirement -> requirement.withAvailability(requirement.required()))
                            .toList()
            );
        }
        return counter.withAvailability(blueprint, inventoryCounts(player));
    }

    public boolean isCreativeBypass(ServerPlayerEntity player) {
        return CREATIVE_MODE_BYPASSES_MATERIALS && player.isCreative();
    }

    public Map<String, Integer> inventoryCounts(ServerPlayerEntity player) {
        Map<String, Integer> counts = new HashMap<>();
        for (int slot = 0; slot < player.getInventory().size(); slot++) {
            ItemStack stack = player.getInventory().getStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            counts.merge(itemId(stack.getItem()), stack.getCount(), Integer::sum);
        }
        return counts;
    }

    private String itemId(Item item) {
        return Registries.ITEM.getId(item).toString();
    }

    public record AccessResult(boolean allowed, String message) {
        public static AccessResult permitted() {
            return new AccessResult(true, "");
        }

        public static AccessResult denied(String message) {
            return new AccessResult(false, message);
        }
    }
}
