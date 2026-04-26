package com.blockforge.forge.material;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.material.MaterialReport;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import java.util.HashMap;
import java.util.Map;

public class ForgePlayerInventoryMaterialChecker {
    public static final boolean REQUIRE_MATERIALS_IN_SURVIVAL = true;
    public static final boolean CREATIVE_MODE_BYPASSES_MATERIALS = true;
    public static final boolean ALLOW_BUILD_IN_ADVENTURE_MODE = false;
    public static final boolean ALLOW_BUILD_IN_SPECTATOR_MODE = false;

    private final ForgeMaterialCounter counter = new ForgeMaterialCounter();

    public AccessResult canBuild(ServerPlayer player) {
        GameType gameType = player.gameMode.getGameModeForPlayer();
        if (gameType == GameType.SPECTATOR && !ALLOW_BUILD_IN_SPECTATOR_MODE) {
            return AccessResult.denied("Spectator mode cannot build BlockForge Forge blueprints.");
        }
        if (gameType == GameType.ADVENTURE && !ALLOW_BUILD_IN_ADVENTURE_MODE) {
            return AccessResult.denied("Adventure mode cannot build BlockForge Forge blueprints.");
        }
        return AccessResult.permitted();
    }

    public MaterialReport report(Blueprint blueprint, ServerPlayer player) {
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

    public boolean isCreativeBypass(ServerPlayer player) {
        return CREATIVE_MODE_BYPASSES_MATERIALS && player.isCreative();
    }

    public Map<String, Integer> inventoryCounts(ServerPlayer player) {
        Map<String, Integer> counts = new HashMap<>();
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            counts.merge(itemId(stack.getItem()), stack.getCount(), Integer::sum);
        }
        return counts;
    }

    private String itemId(Item item) {
        ResourceLocation location = BuiltInRegistries.ITEM.getKey(item);
        return location == null ? "minecraft:air" : location.toString();
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
