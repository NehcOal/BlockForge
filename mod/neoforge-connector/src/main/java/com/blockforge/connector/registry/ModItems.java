package com.blockforge.connector.registry;

import com.blockforge.connector.BlockForgeConnector;
import com.blockforge.connector.item.BuilderWandItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BlockForgeConnector.MOD_ID);

    public static final DeferredItem<BuilderWandItem> BUILDER_WAND = ITEMS.register(
            "builder_wand",
            () -> new BuilderWandItem(new Item.Properties().stacksTo(1))
    );

    public static final DeferredItem<BlockItem> BLUEPRINT_TABLE = ITEMS.registerSimpleBlockItem(
            ModBlocks.BLUEPRINT_TABLE
    );

    public static final DeferredItem<BlockItem> MATERIAL_CACHE = ITEMS.registerSimpleBlockItem(
            ModBlocks.MATERIAL_CACHE
    );

    public static final DeferredItem<BlockItem> BUILDER_ANCHOR = ITEMS.registerSimpleBlockItem(
            ModBlocks.BUILDER_ANCHOR
    );

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
