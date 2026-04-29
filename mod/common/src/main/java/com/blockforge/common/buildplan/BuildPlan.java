package com.blockforge.common.buildplan;

import java.util.List;
import java.util.UUID;

public record BuildPlan(
        String planId,
        UUID playerId,
        String blueprintId,
        String displayName,
        String dimensionId,
        int baseX,
        int baseY,
        int baseZ,
        int rotationDegrees,
        boolean mirrorX,
        boolean mirrorZ,
        int offsetX,
        int offsetY,
        int offsetZ,
        int totalBlocks,
        int totalLayers,
        List<BuildLayer> layers,
        BuildPlanStatus status,
        long createdAtGameTime
) {
    public BuildPlan {
        planId = planId == null ? "" : planId;
        blueprintId = blueprintId == null ? "" : blueprintId;
        displayName = displayName == null || displayName.isBlank() ? blueprintId : displayName;
        dimensionId = dimensionId == null ? "" : dimensionId;
        layers = layers == null ? List.of() : List.copyOf(layers);
        totalBlocks = totalBlocks <= 0 ? layers.stream().mapToInt(BuildLayer::blockCount).sum() : totalBlocks;
        totalLayers = totalLayers <= 0 ? layers.size() : totalLayers;
        status = status == null ? BuildPlanStatus.DRAFT : status;
    }

    public BuildPlan withStatus(BuildPlanStatus nextStatus) {
        return new BuildPlan(planId, playerId, blueprintId, displayName, dimensionId, baseX, baseY, baseZ, rotationDegrees, mirrorX, mirrorZ, offsetX, offsetY, offsetZ, totalBlocks, totalLayers, layers, nextStatus, createdAtGameTime);
    }

    public BuildPlan withLayers(List<BuildLayer> nextLayers, BuildPlanStatus nextStatus) {
        return new BuildPlan(planId, playerId, blueprintId, displayName, dimensionId, baseX, baseY, baseZ, rotationDegrees, mirrorX, mirrorZ, offsetX, offsetY, offsetZ, 0, 0, nextLayers, nextStatus, createdAtGameTime);
    }
}
