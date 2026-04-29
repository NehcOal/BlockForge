package com.blockforge.common.station;

import java.util.List;

public record BuilderStationState(
        String stationId,
        String dimensionId,
        int x,
        int y,
        int z,
        String ownerPlayerId,
        String boundBlueprintId,
        String boundAnchorId,
        List<String> boundMaterialCacheIds,
        String activeBuildPlanId,
        BuilderStationStatus status,
        int maxBlocksPerTick,
        int placedBlocks,
        int totalBlocks,
        long createdAtGameTime,
        long updatedAtGameTime
) {
    public BuilderStationState {
        stationId = stationId == null ? "" : stationId;
        dimensionId = dimensionId == null ? "" : dimensionId;
        ownerPlayerId = ownerPlayerId == null ? "" : ownerPlayerId;
        boundBlueprintId = boundBlueprintId == null ? "" : boundBlueprintId;
        boundAnchorId = boundAnchorId == null ? "" : boundAnchorId;
        boundMaterialCacheIds = boundMaterialCacheIds == null ? List.of() : List.copyOf(boundMaterialCacheIds);
        activeBuildPlanId = activeBuildPlanId == null ? "" : activeBuildPlanId;
        status = status == null ? BuilderStationStatus.IDLE : status;
        maxBlocksPerTick = Math.max(1, maxBlocksPerTick);
        placedBlocks = Math.max(0, placedBlocks);
        totalBlocks = Math.max(0, totalBlocks);
        updatedAtGameTime = Math.max(createdAtGameTime, updatedAtGameTime);
    }

    public BuilderStationState bindBlueprint(String blueprintId, long gameTime) {
        return new BuilderStationState(
                stationId,
                dimensionId,
                x,
                y,
                z,
                ownerPlayerId,
                blueprintId,
                boundAnchorId,
                boundMaterialCacheIds,
                activeBuildPlanId,
                BuilderStationStatus.READY,
                maxBlocksPerTick,
                placedBlocks,
                totalBlocks,
                createdAtGameTime,
                gameTime
        );
    }

    public BuilderStationState withStatus(BuilderStationStatus nextStatus, long gameTime) {
        return new BuilderStationState(
                stationId,
                dimensionId,
                x,
                y,
                z,
                ownerPlayerId,
                boundBlueprintId,
                boundAnchorId,
                boundMaterialCacheIds,
                activeBuildPlanId,
                nextStatus,
                maxBlocksPerTick,
                placedBlocks,
                totalBlocks,
                createdAtGameTime,
                gameTime
        );
    }
}
