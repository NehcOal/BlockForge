package com.blockforge.fabric.material;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.material.MaterialCounterCore;
import com.blockforge.common.material.MaterialReport;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Map;

public class FabricMaterialCounter {
    public MaterialReport count(Blueprint blueprint) {
        return withAvailability(blueprint, Map.of());
    }

    public MaterialReport withAvailability(Blueprint blueprint, Map<String, Integer> availableItems) {
        return MaterialCounterCore.withAvailability(blueprint, availableItems, FabricMaterialCounter::itemIdForBlock);
    }

    private static String itemIdForBlock(String blockId) {
        Identifier identifier = Identifier.tryParse(blockId);
        if (identifier == null) {
            return "minecraft:air";
        }

        Block block = Registries.BLOCK.getOrEmpty(identifier).orElse(Blocks.AIR);
        Item item = block.asItem();
        if (block == Blocks.AIR || item == Items.AIR) {
            return "minecraft:air";
        }

        Identifier itemId = Registries.ITEM.getId(item);
        return itemId == null ? "minecraft:air" : itemId.toString();
    }
}
