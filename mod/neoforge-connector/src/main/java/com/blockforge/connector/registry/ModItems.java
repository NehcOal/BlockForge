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

    public static final DeferredItem<BlockItem> BUILDER_STATION = ITEMS.registerSimpleBlockItem(
            ModBlocks.BUILDER_STATION
    );

    public static final DeferredItem<BlockItem> MATERIAL_LINK = ITEMS.registerSimpleBlockItem(
            ModBlocks.MATERIAL_LINK
    );

    public static final DeferredItem<BlockItem> CONSTRUCTION_CORE = ITEMS.registerSimpleBlockItem(
            ModBlocks.CONSTRUCTION_CORE
    );

    public static final DeferredItem<BlockItem> SETTLEMENT_CORE = ITEMS.registerSimpleBlockItem(
            ModBlocks.SETTLEMENT_CORE
    );

    public static final DeferredItem<BlockItem> CONTRACT_BOARD = ITEMS.registerSimpleBlockItem(
            ModBlocks.CONTRACT_BOARD
    );

    public static final DeferredItem<BlockItem> REWARD_CRATE = ITEMS.registerSimpleBlockItem(
            ModBlocks.REWARD_CRATE
    );

    public static final DeferredItem<BlockItem> ARCHITECT_DESK = ITEMS.registerSimpleBlockItem(
            ModBlocks.ARCHITECT_DESK
    );

    public static final DeferredItem<BlockItem> EVENT_BOARD = ITEMS.registerSimpleBlockItem(
            ModBlocks.EVENT_BOARD
    );

    public static final DeferredItem<BlockItem> PROJECT_MAP = ITEMS.registerSimpleBlockItem(
            ModBlocks.PROJECT_MAP
    );

    public static final DeferredItem<BlockItem> EMERGENCY_BEACON = ITEMS.registerSimpleBlockItem(
            ModBlocks.EMERGENCY_BEACON
    );

    public static final DeferredItem<BlockItem> SUPPLY_DEPOT = ITEMS.registerSimpleBlockItem(
            ModBlocks.SUPPLY_DEPOT
    );

    public static final DeferredItem<Item> ARCHITECT_LEDGER = ITEMS.register(
            "architect_ledger",
            () -> new Item(new Item.Properties().stacksTo(1))
    );

    public static final DeferredItem<Item> CONTRACT_TOKEN = ITEMS.register(
            "contract_token",
            () -> new Item(new Item.Properties())
    );

    public static final DeferredItem<Item> ARCHITECT_SEAL = ITEMS.register(
            "architect_seal",
            () -> new Item(new Item.Properties())
    );

    public static final DeferredItem<Item> EVENT_NOTICE = ITEMS.register(
            "event_notice",
            () -> new Item(new Item.Properties())
    );

    public static final DeferredItem<Item> PROJECT_CHARTER = ITEMS.register(
            "project_charter",
            () -> new Item(new Item.Properties().stacksTo(1))
    );

    public static final DeferredItem<Item> EMERGENCY_REPAIR_KIT = ITEMS.register(
            "emergency_repair_kit",
            () -> new Item(new Item.Properties())
    );

    public static final DeferredItem<Item> SETTLEMENT_SEAL = ITEMS.register(
            "settlement_seal",
            () -> new Item(new Item.Properties())
    );

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
