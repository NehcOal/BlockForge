package com.blockforge.fabric.registry;

import com.blockforge.fabric.BlockForgeFabric;
import com.blockforge.fabric.item.FabricBuilderWandItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public final class FabricModItems {
    public static final Item BUILDER_WAND = new FabricBuilderWandItem(new Item.Settings().maxCount(1));
    public static final Block BLUEPRINT_TABLE = new Block(AbstractBlock.Settings.create().strength(2.5F).sounds(BlockSoundGroup.WOOD));
    public static final Block MATERIAL_CACHE = new Block(AbstractBlock.Settings.create().strength(3.0F).sounds(BlockSoundGroup.METAL));
    public static final Block BUILDER_ANCHOR = new Block(AbstractBlock.Settings.create().strength(4.0F).luminance(state -> 5).sounds(BlockSoundGroup.AMETHYST_BLOCK));
    public static final Item BLUEPRINT_TABLE_ITEM = new BlockItem(BLUEPRINT_TABLE, new Item.Settings());
    public static final Item MATERIAL_CACHE_ITEM = new BlockItem(MATERIAL_CACHE, new Item.Settings());
    public static final Item BUILDER_ANCHOR_ITEM = new BlockItem(BUILDER_ANCHOR, new Item.Settings());

    private FabricModItems() {
    }

    public static void register() {
        registerBlock("blueprint_table", BLUEPRINT_TABLE, BLUEPRINT_TABLE_ITEM);
        registerBlock("material_cache", MATERIAL_CACHE, MATERIAL_CACHE_ITEM);
        registerBlock("builder_anchor", BUILDER_ANCHOR, BUILDER_ANCHOR_ITEM);
        Registry.register(
                Registries.ITEM,
                Identifier.of(BlockForgeFabric.MOD_ID, "builder_wand"),
                BUILDER_WAND
        );
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(BUILDER_WAND));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(BLUEPRINT_TABLE_ITEM);
            entries.add(MATERIAL_CACHE_ITEM);
            entries.add(BUILDER_ANCHOR_ITEM);
        });
    }

    private static void registerBlock(String id, Block block, Item item) {
        Registry.register(Registries.BLOCK, Identifier.of(BlockForgeFabric.MOD_ID, id), block);
        Registry.register(Registries.ITEM, Identifier.of(BlockForgeFabric.MOD_ID, id), item);
    }
}
