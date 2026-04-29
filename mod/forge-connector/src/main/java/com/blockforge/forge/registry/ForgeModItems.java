package com.blockforge.forge.registry;

import com.blockforge.forge.BlockForgeForge;
import com.blockforge.forge.item.ForgeBuilderWandItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ForgeModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
            ForgeRegistries.ITEMS,
            BlockForgeForge.MOD_ID
    );

    public static final RegistryObject<Item> BUILDER_WAND = ITEMS.register(
            "builder_wand",
            () -> new ForgeBuilderWandItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> BLUEPRINT_TABLE = ITEMS.register(
            "blueprint_table",
            () -> new BlockItem(ForgeModBlocks.BLUEPRINT_TABLE.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> MATERIAL_CACHE = ITEMS.register(
            "material_cache",
            () -> new BlockItem(ForgeModBlocks.MATERIAL_CACHE.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> BUILDER_ANCHOR = ITEMS.register(
            "builder_anchor",
            () -> new BlockItem(ForgeModBlocks.BUILDER_ANCHOR.get(), new Item.Properties())
    );

    private ForgeModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
