package com.blockforge.common.build;

import com.blockforge.common.rotation.BlueprintRotation;
import com.blockforge.common.util.BlockPosition;

import java.util.List;

public record BuildPlan(
        String blueprintId,
        BlockPosition basePosition,
        BlueprintRotation rotation,
        List<PlannedBlock> plannedBlocks,
        int totalBlocks
) {
    public BuildPlan {
        plannedBlocks = plannedBlocks == null ? List.of() : List.copyOf(plannedBlocks);
    }

    public int plannedBlockCount() {
        return plannedBlocks.size();
    }
}
