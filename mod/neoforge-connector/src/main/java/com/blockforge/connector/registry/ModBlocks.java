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

    private ModBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
