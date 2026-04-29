package com.blockforge.common.buildplan;

import java.util.List;

public record BuildLayer(
        int y,
        List<BuildStep> steps,
        int blockCount
) {
    public BuildLayer {
        steps = steps == null ? List.of() : List.copyOf(steps);
        blockCount = blockCount <= 0 ? steps.size() : blockCount;
    }
}
