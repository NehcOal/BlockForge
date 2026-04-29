package com.blockforge.common.buildplan;

public record BuildPlanOptions(
        boolean layerByLayer,
        boolean allowPartialBuild,
        boolean replaceAirOnly,
        boolean protectBlockEntities,
        boolean consumeMaterials,
        boolean repairMode,
        int maxBlocksPerTick,
        int maxBlocksPerStep
) {
    public BuildPlanOptions {
        maxBlocksPerTick = Math.max(1, maxBlocksPerTick);
        maxBlocksPerStep = Math.max(1, maxBlocksPerStep);
    }

    public static BuildPlanOptions defaults() {
        return new BuildPlanOptions(true, false, true, true, true, false, 16, 64);
    }

    public BuildPlanOptions repair() {
        return new BuildPlanOptions(layerByLayer, allowPartialBuild, replaceAirOnly, protectBlockEntities, consumeMaterials, true, maxBlocksPerTick, maxBlocksPerStep);
    }
}
