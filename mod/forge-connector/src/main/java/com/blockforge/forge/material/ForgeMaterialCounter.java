package com.blockforge.forge.material;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.material.MaterialCounterCore;
import com.blockforge.common.material.MaterialReport;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public class ForgeMaterialCounter {
    public MaterialReport count(Blueprint blueprint) {
        return withAvailability(blueprint, Map.of());
    }

    public MaterialReport withAvailability(Blueprint blueprint, Map<String, Integer> availableItems) {
        return MaterialCounterCore.withAvailability(blueprint, availableItems, ForgeMaterialCounter::itemIdForBlock);
    }

    private static String itemIdForBlock(String blockId) {
        ResourceLocation location = ResourceLocation.tryParse(blockId);
        if (location == null) {
            return "minecraft:air";
        }

        Block block = BuiltInRegistries.BLOCK.getOptional(location).orElse(Blocks.AIR);
        Item item = block.asItem();
        if (block == Blocks.AIR || item == Items.AIR) {
            return "minecraft:air";
        }

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        return itemId == null ? "minecraft:air" : itemId.toString();
    }
}
