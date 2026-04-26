package com.blockforge.common.build;

import com.blockforge.common.util.BlockPosition;

import java.util.Map;

public record PlannedBlock(
        BlockPosition position,
        String blockId,
        String stateKey,
        Map<String, String> properties
) {
    public PlannedBlock {
        properties = properties == null ? Map.of() : Map.copyOf(properties);
    }
}
