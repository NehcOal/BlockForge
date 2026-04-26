package com.blockforge.common.material.source;

import java.util.UUID;

public record MaterialSourceScanPlan(
        UUID playerId,
        String blueprintId,
        String dimensionId,
        int centerX,
        int centerY,
        int centerZ,
        int radius,
        int maxContainers,
        MaterialSourcePriority priority
) {
    public MaterialSourceScanPlan(
            UUID playerId,
            String blueprintId,
            int centerX,
            int centerY,
            int centerZ,
            int radius,
            int maxContainers,
            MaterialSourcePriority priority
    ) {
        this(playerId, blueprintId, "", centerX, centerY, centerZ, radius, maxContainers, priority);
    }

    public MaterialSourceScanPlan {
        blueprintId = blueprintId == null ? "" : blueprintId;
        dimensionId = dimensionId == null ? "" : dimensionId;
        radius = Math.max(0, radius);
        maxContainers = Math.max(0, maxContainers);
        priority = priority == null ? MaterialSourcePriority.PLAYER_FIRST : priority;
    }

    public static MaterialSourceScanPlan fromConfig(
            UUID playerId,
            String blueprintId,
            int centerX,
            int centerY,
            int centerZ,
            MaterialSourceConfig config
    ) {
        MaterialSourceConfig resolvedConfig = config == null ? MaterialSourceConfig.defaults() : config;
        return new MaterialSourceScanPlan(
                playerId,
                blueprintId,
                "",
                centerX,
                centerY,
                centerZ,
                resolvedConfig.searchRadius(),
                resolvedConfig.maxContainersScanned(),
                resolvedConfig.priority()
        );
    }
}
