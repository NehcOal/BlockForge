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
    public static final Block BUILDER_STATION = new Block(AbstractBlock.Settings.create().strength(4.5F).luminance(state -> 3).sounds(BlockSoundGroup.METAL));
    public static final Block MATERIAL_LINK = new Block(AbstractBlock.Settings.create().strength(2.0F).luminance(state -> 2).sounds(BlockSoundGroup.COPPER));
    public static final Block CONSTRUCTION_CORE = new Block(AbstractBlock.Settings.create().strength(5.0F).luminance(state -> 7).sounds(BlockSoundGroup.AMETHYST_BLOCK));
    public static final Block SETTLEMENT_CORE = new Block(AbstractBlock.Settings.create().strength(5.0F).luminance(state -> 6).sounds(BlockSoundGroup.AMETHYST_BLOCK));
    public static final Block CONTRACT_BOARD = new Block(AbstractBlock.Settings.create().strength(2.5F).sounds(BlockSoundGroup.WOOD));
    public static final Block REWARD_CRATE = new Block(AbstractBlock.Settings.create().strength(3.0F).sounds(BlockSoundGroup.WOOD));
    public static final Block ARCHITECT_DESK = new Block(AbstractBlock.Settings.create().strength(2.5F).sounds(BlockSoundGroup.WOOD));
    public static final Block EVENT_BOARD = new Block(AbstractBlock.Settings.create().strength(2.5F).sounds(BlockSoundGroup.WOOD));
    public static final Block PROJECT_MAP = new Block(AbstractBlock.Settings.create().strength(2.0F).sounds(BlockSoundGroup.WOOD));
    public static final Block EMERGENCY_BEACON = new Block(AbstractBlock.Settings.create().strength(4.0F).luminance(state -> 9).sounds(BlockSoundGroup.AMETHYST_BLOCK));
    public static final Block SUPPLY_DEPOT = new Block(AbstractBlock.Settings.create().strength(3.5F).sounds(BlockSoundGroup.WOOD));
    public static final Item BLUEPRINT_TABLE_ITEM = new BlockItem(BLUEPRINT_TABLE, new Item.Settings());
    public static final Item MATERIAL_CACHE_ITEM = new BlockItem(MATERIAL_CACHE, new Item.Settings());
    public static final Item BUILDER_ANCHOR_ITEM = new BlockItem(BUILDER_ANCHOR, new Item.Settings());
    public static final Item BUILDER_STATION_ITEM = new BlockItem(BUILDER_STATION, new Item.Settings());
    public static final Item MATERIAL_LINK_ITEM = new BlockItem(MATERIAL_LINK, new Item.Settings());
    public static final Item CONSTRUCTION_CORE_ITEM = new BlockItem(CONSTRUCTION_CORE, new Item.Settings());
    public static final Item SETTLEMENT_CORE_ITEM = new BlockItem(SETTLEMENT_CORE, new Item.Settings());
    public static final Item CONTRACT_BOARD_ITEM = new BlockItem(CONTRACT_BOARD, new Item.Settings());
    public static final Item REWARD_CRATE_ITEM = new BlockItem(REWARD_CRATE, new Item.Settings());
    public static final Item ARCHITECT_DESK_ITEM = new BlockItem(ARCHITECT_DESK, new Item.Settings());
    public static final Item EVENT_BOARD_ITEM = new BlockItem(EVENT_BOARD, new Item.Settings());
    public static final Item PROJECT_MAP_ITEM = new BlockItem(PROJECT_MAP, new Item.Settings());
    public static final Item EMERGENCY_BEACON_ITEM = new BlockItem(EMERGENCY_BEACON, new Item.Settings());
    public static final Item SUPPLY_DEPOT_ITEM = new BlockItem(SUPPLY_DEPOT, new Item.Settings());
    public static final Item ARCHITECT_LEDGER = new Item(new Item.Settings().maxCount(1));
    public static final Item CONTRACT_TOKEN = new Item(new Item.Settings());
    public static final Item ARCHITECT_SEAL = new Item(new Item.Settings());
    public static final Item EVENT_NOTICE = new Item(new Item.Settings());
    public static final Item PROJECT_CHARTER = new Item(new Item.Settings().maxCount(1));
    public static final Item EMERGENCY_REPAIR_KIT = new Item(new Item.Settings());
    public static final Item SETTLEMENT_SEAL = new Item(new Item.Settings());

    private FabricModItems() {
    }

    public static void register() {
        registerBlock("blueprint_table", BLUEPRINT_TABLE, BLUEPRINT_TABLE_ITEM);
        registerBlock("material_cache", MATERIAL_CACHE, MATERIAL_CACHE_ITEM);
        registerBlock("builder_anchor", BUILDER_ANCHOR, BUILDER_ANCHOR_ITEM);
        registerBlock("builder_station", BUILDER_STATION, BUILDER_STATION_ITEM);
        registerBlock("material_link", MATERIAL_LINK, MATERIAL_LINK_ITEM);
        registerBlock("construction_core", CONSTRUCTION_CORE, CONSTRUCTION_CORE_ITEM);
        registerBlock("settlement_core", SETTLEMENT_CORE, SETTLEMENT_CORE_ITEM);
        registerBlock("contract_board", CONTRACT_BOARD, CONTRACT_BOARD_ITEM);
        registerBlock("reward_crate", REWARD_CRATE, REWARD_CRATE_ITEM);
        registerBlock("architect_desk", ARCHITECT_DESK, ARCHITECT_DESK_ITEM);
        registerBlock("event_board", EVENT_BOARD, EVENT_BOARD_ITEM);
        registerBlock("project_map", PROJECT_MAP, PROJECT_MAP_ITEM);
        registerBlock("emergency_beacon", EMERGENCY_BEACON, EMERGENCY_BEACON_ITEM);
        registerBlock("supply_depot", SUPPLY_DEPOT, SUPPLY_DEPOT_ITEM);
        Registry.register(
                Registries.ITEM,
                Identifier.of(BlockForgeFabric.MOD_ID, "builder_wand"),
                BUILDER_WAND
        );
        Registry.register(Registries.ITEM, Identifier.of(BlockForgeFabric.MOD_ID, "architect_ledger"), ARCHITECT_LEDGER);
        Registry.register(Registries.ITEM, Identifier.of(BlockForgeFabric.MOD_ID, "contract_token"), CONTRACT_TOKEN);
        Registry.register(Registries.ITEM, Identifier.of(BlockForgeFabric.MOD_ID, "architect_seal"), ARCHITECT_SEAL);
        Registry.register(Registries.ITEM, Identifier.of(BlockForgeFabric.MOD_ID, "event_notice"), EVENT_NOTICE);
        Registry.register(Registries.ITEM, Identifier.of(BlockForgeFabric.MOD_ID, "project_charter"), PROJECT_CHARTER);
        Registry.register(Registries.ITEM, Identifier.of(BlockForgeFabric.MOD_ID, "emergency_repair_kit"), EMERGENCY_REPAIR_KIT);
        Registry.register(Registries.ITEM, Identifier.of(BlockForgeFabric.MOD_ID, "settlement_seal"), SETTLEMENT_SEAL);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(BUILDER_WAND);
            entries.add(BLUEPRINT_TABLE_ITEM);
            entries.add(MATERIAL_CACHE_ITEM);
            entries.add(BUILDER_ANCHOR_ITEM);
            entries.add(BUILDER_STATION_ITEM);
            entries.add(MATERIAL_LINK_ITEM);
            entries.add(CONSTRUCTION_CORE_ITEM);
            entries.add(SETTLEMENT_CORE_ITEM);
            entries.add(CONTRACT_BOARD_ITEM);
            entries.add(REWARD_CRATE_ITEM);
            entries.add(ARCHITECT_DESK_ITEM);
            entries.add(EVENT_BOARD_ITEM);
            entries.add(PROJECT_MAP_ITEM);
            entries.add(EMERGENCY_BEACON_ITEM);
            entries.add(SUPPLY_DEPOT_ITEM);
            entries.add(ARCHITECT_LEDGER);
            entries.add(CONTRACT_TOKEN);
            entries.add(ARCHITECT_SEAL);
            entries.add(EVENT_NOTICE);
            entries.add(PROJECT_CHARTER);
            entries.add(EMERGENCY_REPAIR_KIT);
            entries.add(SETTLEMENT_SEAL);
        });
    }

    private static void registerBlock(String id, Block block, Item item) {
        Registry.register(Registries.BLOCK, Identifier.of(BlockForgeFabric.MOD_ID, id), block);
        Registry.register(Registries.ITEM, Identifier.of(BlockForgeFabric.MOD_ID, id), item);
    }
}
