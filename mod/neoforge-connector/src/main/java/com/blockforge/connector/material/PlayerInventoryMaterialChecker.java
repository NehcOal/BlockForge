package com.blockforge.connector.material;

import com.blockforge.connector.blueprint.Blueprint;
import com.blockforge.connector.config.BlockForgeConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import java.util.HashMap;
import java.util.Map;

public class PlayerInventoryMaterialChecker {
    private final MaterialCounter counter = new MaterialCounter();

    public AccessResult canBuild(ServerPlayer player) {
        GameType gameType = player.gameMode.getGameModeForPlayer();

        if (gameType == GameType.SPECTATOR && !BlockForgeConfig.allowBuildInSpectatorMode()) {
            return AccessResult.denied("Spectator mode cannot build BlockForge blueprints.");
        }

        if (gameType == GameType.ADVENTURE && !BlockForgeConfig.allowBuildInAdventureMode()) {
            return AccessResult.denied("Adventure mode cannot build BlockForge blueprints.");
        }

        return AccessResult.permitted();
    }

    public MaterialReport report(Blueprint blueprint, ServerPlayer player) {
        if (player == null) {
            return counter.count(blueprint);
        }

        if (isCreativeBypass(player)) {
            return creativeReport(blueprint);
        }

        return counter.withAvailability(blueprint, inventoryCounts(player));
    }

    public boolean isCreativeBypass(ServerPlayer player) {
        return BlockForgeConfig.creativeModeBypassesMaterials() && player.isCreative();
    }

    public Map<String, Integer> inventoryCounts(ServerPlayer player) {
        Map<String, Integer> counts = new HashMap<>();

        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }

            String itemId = itemId(stack.getItem());
            counts.merge(itemId, stack.getCount(), Integer::sum);
        }

        return counts;
    }

    private MaterialReport creativeReport(Blueprint blueprint) {
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
