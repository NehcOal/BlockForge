package com.blockforge.connector.registry;

import com.blockforge.connector.BlockForgeConnector;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BlockForgeConnector.MOD_ID);

    public static final DeferredBlock<Block> BLUEPRINT_TABLE = BLOCKS.register(
            "blueprint_table",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.5F)
                    .sound(SoundType.WOOD))
    );

    public static final DeferredBlock<Block> MATERIAL_CACHE = BLOCKS.register(
            "material_cache",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .sound(SoundType.METAL))
    );

    public static final DeferredBlock<Block> BUILDER_ANCHOR = BLOCKS.register(
            "builder_anchor",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.0F)
                    .lightLevel(state -> 5)
                    .sound(SoundType.AMETHYST))
    );

    public static final DeferredBlock<Block> BUILDER_STATION = BLOCKS.register(
            "builder_station",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.5F)
                    .lightLevel(state -> 3)
                    .sound(SoundType.METAL))
    );

    public static final DeferredBlock<Block> MATERIAL_LINK = BLOCKS.register(
            "material_link",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .lightLevel(state -> 2)
                    .sound(SoundType.COPPER))
    );

    public static final DeferredBlock<Block> CONSTRUCTION_CORE = BLOCKS.register(
            "construction_core",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(5.0F)
                    .lightLevel(state -> 7)
                    .sound(SoundType.AMETHYST))
    );

    public static final DeferredBlock<Block> SETTLEMENT_CORE = BLOCKS.register(
            "settlement_core",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(5.0F)
                    .lightLevel(state -> 6)
                    .sound(SoundType.AMETHYST))
    );

    public static final DeferredBlock<Block> CONTRACT_BOARD = BLOCKS.register(
            "contract_board",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.5F)
                    .sound(SoundType.WOOD))
    );

    public static final DeferredBlock<Block> REWARD_CRATE = BLOCKS.register(
            "reward_crate",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .sound(SoundType.WOOD))
    );

    public static final DeferredBlock<Block> ARCHITECT_DESK = BLOCKS.register(
            "architect_desk",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.5F)
                    .sound(SoundType.WOOD))
    );

    public static final DeferredBlock<Block> EVENT_BOARD = BLOCKS.register(
            "event_board",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.5F)
                    .sound(SoundType.WOOD))
    );

    public static final DeferredBlock<Block> PROJECT_MAP = BLOCKS.register(
            "project_map",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .sound(SoundType.WOOD))
    );

    public static final DeferredBlock<Block> EMERGENCY_BEACON = BLOCKS.register(
            "emergency_beacon",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.0F)
                    .lightLevel(state -> 9)
                    .sound(SoundType.AMETHYST))
    );

    public static final DeferredBlock<Block> SUPPLY_DEPOT = BLOCKS.register(
            "supply_depot",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.WOOD))
    );

    private ModBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
