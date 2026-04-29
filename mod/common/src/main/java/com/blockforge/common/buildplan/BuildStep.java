package com.blockforge.common.buildplan;

public record BuildStep(
        int x,
        int y,
        int z,
        String blockId,
        String blockStateKey,
        String paletteKey,
        boolean replaceExisting,
        BuildStepStatus status
) {
    public BuildStep {
        blockId = blockId == null ? "" : blockId;
        blockStateKey = blockStateKey == null ? "" : blockStateKey;
        paletteKey = paletteKey == null ? "" : paletteKey;
        status = status == null ? BuildStepStatus.PENDING : status;
    }

    public BuildStep withStatus(BuildStepStatus nextStatus) {
        return new BuildStep(x, y, z, blockId, blockStateKey, paletteKey, replaceExisting, nextStatus);
    }
}
