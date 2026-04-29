package com.blockforge.common.buildstation;

public record BuilderStationRuntimeConfig(
        boolean enabled,
        int maxBlocksPerTick,
        int maxQueuedJobs,
        boolean requireLoadedChunk,
        boolean requireMaterialCache,
        boolean allowOwnerInventoryFallback,
        boolean protectBlockEntities,
        boolean allowPartialBuild
) {
    public BuilderStationRuntimeConfig {
        maxBlocksPerTick = Math.max(1, maxBlocksPerTick);
        maxQueuedJobs = Math.max(1, maxQueuedJobs);
    }

    public static BuilderStationRuntimeConfig defaults() {
        return new BuilderStationRuntimeConfig(true, 16, 32, true, false, true, true, false);
    }
}
