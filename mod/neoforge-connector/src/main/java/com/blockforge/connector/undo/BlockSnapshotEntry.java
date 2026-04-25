package com.blockforge.connector.undo;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public record BlockSnapshotEntry(
        BlockPos pos,
        BlockState previousState,
        CompoundTag blockEntityTag
) {
    public BlockSnapshotEntry {
        pos = pos.immutable();
        blockEntityTag = blockEntityTag == null ? null : blockEntityTag.copy();
    }
}
