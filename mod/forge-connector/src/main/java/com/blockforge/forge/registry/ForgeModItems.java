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

    public static final RegistryObject<Item> BUILDER_STATION = ITEMS.register(
            "builder_station",
            () -> new BlockItem(ForgeModBlocks.BUILDER_STATION.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> MATERIAL_LINK = ITEMS.register(
            "material_link",
            () -> new BlockItem(ForgeModBlocks.MATERIAL_LINK.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> CONSTRUCTION_CORE = ITEMS.register(
            "construction_core",
            () -> new BlockItem(ForgeModBlocks.CONSTRUCTION_CORE.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> SETTLEMENT_CORE = ITEMS.register(
            "settlement_core",
            () -> new BlockItem(ForgeModBlocks.SETTLEMENT_CORE.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> CONTRACT_BOARD = ITEMS.register(
            "contract_board",
            () -> new BlockItem(ForgeModBlocks.CONTRACT_BOARD.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> REWARD_CRATE = ITEMS.register(
            "reward_crate",
            () -> new BlockItem(ForgeModBlocks.REWARD_CRATE.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> ARCHITECT_DESK = ITEMS.register(
            "architect_desk",
            () -> new BlockItem(ForgeModBlocks.ARCHITECT_DESK.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> EVENT_BOARD = ITEMS.register(
            "event_board",
            () -> new BlockItem(ForgeModBlocks.EVENT_BOARD.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> PROJECT_MAP = ITEMS.register(
            "project_map",
            () -> new BlockItem(ForgeModBlocks.PROJECT_MAP.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> EMERGENCY_BEACON = ITEMS.register(
            "emergency_beacon",
            () -> new BlockItem(ForgeModBlocks.EMERGENCY_BEACON.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> SUPPLY_DEPOT = ITEMS.register(
            "supply_depot",
            () -> new BlockItem(ForgeModBlocks.SUPPLY_DEPOT.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> ARCHITECT_LEDGER = ITEMS.register(
            "architect_ledger",
            () -> new Item(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> CONTRACT_TOKEN = ITEMS.register(
            "contract_token",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> ARCHITECT_SEAL = ITEMS.register(
            "architect_seal",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> EVENT_NOTICE = ITEMS.register(
            "event_notice",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> PROJECT_CHARTER = ITEMS.register(
            "project_charter",
            () -> new Item(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> EMERGENCY_REPAIR_KIT = ITEMS.register(
            "emergency_repair_kit",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> SETTLEMENT_SEAL = ITEMS.register(
            "settlement_seal",
            () -> new Item(new Item.Properties())
    );

    private ForgeModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
