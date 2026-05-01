package com.blockforge.forge.registry;

import com.blockforge.forge.BlockForgeForge;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ForgeModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(
            ForgeRegistries.BLOCKS,
            BlockForgeForge.MOD_ID
    );

    public static final RegistryObject<Block> BLUEPRINT_TABLE = BLOCKS.register(
            "blueprint_table",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.5F)
                    .sound(SoundType.WOOD))
    );

    public static final RegistryObject<Block> MATERIAL_CACHE = BLOCKS.register(
            "material_cache",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .sound(SoundType.METAL))
    );

    public static final RegistryObject<Block> BUILDER_ANCHOR = BLOCKS.register(
            "builder_anchor",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.0F)
                    .lightLevel(state -> 5)
                    .sound(SoundType.AMETHYST))
    );

    public static final RegistryObject<Block> BUILDER_STATION = BLOCKS.register(
            "builder_station",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.5F)
                    .lightLevel(state -> 3)
                    .sound(SoundType.METAL))
    );

    public static final RegistryObject<Block> MATERIAL_LINK = BLOCKS.register(
            "material_link",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .lightLevel(state -> 2)
                    .sound(SoundType.COPPER))
    );

    public static final RegistryObject<Block> CONSTRUCTION_CORE = BLOCKS.register(
            "construction_core",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(5.0F)
                    .lightLevel(state -> 7)
                    .sound(SoundType.AMETHYST))
    );

    public static final RegistryObject<Block> SETTLEMENT_CORE = BLOCKS.register(
            "settlement_core",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(5.0F)
                    .lightLevel(state -> 6)
                    .sound(SoundType.AMETHYST))
    );

    public static final RegistryObject<Block> CONTRACT_BOARD = BLOCKS.register(
            "contract_board",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.5F)
                    .sound(SoundType.WOOD))
    );

    public static final RegistryObject<Block> REWARD_CRATE = BLOCKS.register(
            "reward_crate",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .sound(SoundType.WOOD))
    );

    public static final RegistryObject<Block> ARCHITECT_DESK = BLOCKS.register(
            "architect_desk",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.5F)
                    .sound(SoundType.WOOD))
    );

    public static final RegistryObject<Block> EVENT_BOARD = BLOCKS.register(
            "event_board",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.5F)
                    .sound(SoundType.WOOD))
    );

    public static final RegistryObject<Block> PROJECT_MAP = BLOCKS.register(
            "project_map",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .sound(SoundType.WOOD))
    );

    public static final RegistryObject<Block> EMERGENCY_BEACON = BLOCKS.register(
            "emergency_beacon",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.0F)
                    .lightLevel(state -> 9)
                    .sound(SoundType.AMETHYST))
    );

    public static final RegistryObject<Block> SUPPLY_DEPOT = BLOCKS.register(
            "supply_depot",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.WOOD))
    );

    private ForgeModBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
