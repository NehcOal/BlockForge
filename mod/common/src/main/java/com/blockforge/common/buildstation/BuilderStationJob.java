package com.blockforge.common.buildstation;

import java.util.UUID;

public record BuilderStationJob(
        String jobId,
        UUID playerId,
        String planId,
        String blueprintId,
        BuilderStationJobStatus status,
        long queuedAtGameTime,
        long startedAtGameTime,
        long updatedAtGameTime,
        int totalBlocks,
        int completedBlocks,
        String failureReason
) {
    public BuilderStationJob {
        jobId = jobId == null ? "" : jobId;
        planId = planId == null ? "" : planId;
        blueprintId = blueprintId == null ? "" : blueprintId;
        status = status == null ? BuilderStationJobStatus.QUEUED : status;
        totalBlocks = Math.max(0, totalBlocks);
        completedBlocks = Math.max(0, Math.min(completedBlocks, totalBlocks));
        failureReason = failureReason == null ? "" : failureReason;
    }

    public static BuilderStationJob queued(String jobId, UUID playerId, String planId, String blueprintId, int totalBlocks, long gameTime) {
        return new BuilderStationJob(jobId, playerId, planId, blueprintId, BuilderStationJobStatus.QUEUED, gameTime, 0L, gameTime, totalBlocks, 0, "");
    }

    public BuilderStationJob withStatus(BuilderStationJobStatus nextStatus, long gameTime) {
        long startTime = startedAtGameTime == 0L && nextStatus == BuilderStationJobStatus.RUNNING ? gameTime : startedAtGameTime;
        return new BuilderStationJob(jobId, playerId, planId, blueprintId, nextStatus, queuedAtGameTime, startTime, gameTime, totalBlocks, completedBlocks, failureReason);
    }

    public BuilderStationJob withProgress(int nextCompletedBlocks, long gameTime) {
        BuilderStationJobStatus nextStatus = nextCompletedBlocks >= totalBlocks ? BuilderStationJobStatus.COMPLETED : status;
        return new BuilderStationJob(jobId, playerId, planId, blueprintId, nextStatus, queuedAtGameTime, startedAtGameTime, gameTime, totalBlocks, nextCompletedBlocks, failureReason);
    }

    public double percent() {
        return totalBlocks == 0 ? 100.0 : (completedBlocks * 100.0) / totalBlocks;
    }
}
