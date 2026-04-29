package com.blockforge.common.serverrules;

public record ServerBuildRules(
        boolean builderStationEnabled,
        int maxActivePlansPerPlayer,
        int maxBlocksPerTick,
        int maxQueuedJobs,
        boolean requireAnchorForStationJobs,
        boolean allowMaterialCache,
        boolean allowNearbyContainers,
        boolean allowPartialBuild,
        String permissionNodePrefix
) {
    public ServerBuildRules {
        maxActivePlansPerPlayer = Math.max(1, maxActivePlansPerPlayer);
        maxBlocksPerTick = Math.max(1, maxBlocksPerTick);
        maxQueuedJobs = Math.max(1, maxQueuedJobs);
        permissionNodePrefix = permissionNodePrefix == null || permissionNodePrefix.isBlank()
                ? "blockforge"
                : permissionNodePrefix;
    }

    public static ServerBuildRules defaults() {
        return new ServerBuildRules(true, 1, 16, 32, false, true, true, false, "blockforge");
    }
}
