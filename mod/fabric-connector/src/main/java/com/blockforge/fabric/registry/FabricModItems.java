package com.blockforge.fabric.registry;

import com.blockforge.fabric.BlockForgeFabric;
import com.blockforge.fabric.item.FabricBuilderWandItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class FabricModItems {
    public static final Item BUILDER_WAND = new FabricBuilderWandItem(new Item.Settings().maxCount(1));

    private FabricModItems() {
    }

    public static void register() {
        Registry.register(
                Registries.ITEM,
                Identifier.of(BlockForgeFabric.MOD_ID, "builder_wand"),
                BUILDER_WAND
        );
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(BUILDER_WAND));
    }
}
