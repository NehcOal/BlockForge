package com.blockforge.common.serverplay;

public record AdminBuildSummary(
        String jobId,
        String ownerPlayerId,
        String blueprintId,
        String status,
        int placedBlocks,
        int totalBlocks,
        String dimensionId
) {
    public AdminBuildSummary {
        jobId = jobId == null ? "" : jobId;
        ownerPlayerId = ownerPlayerId == null ? "" : ownerPlayerId;
        blueprintId = blueprintId == null ? "" : blueprintId;
        status = status == null ? "unknown" : status;
        placedBlocks = Math.max(0, placedBlocks);
        totalBlocks = Math.max(0, totalBlocks);
        dimensionId = dimensionId == null ? "" : dimensionId;
    }
}
