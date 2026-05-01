package com.blockforge.common.buildstation;

import java.util.List;

public record StationWorldMutationResult(
        String jobId,
        int attemptedBlocks,
        int placedBlocks,
        int skippedBlocks,
        int failedBlocks,
        boolean undoSnapshotRecorded,
        boolean auditRecorded,
        List<String> warnings
) {
    public StationWorldMutationResult {
        jobId = jobId == null ? "" : jobId;
        attemptedBlocks = Math.max(0, attemptedBlocks);
        placedBlocks = Math.max(0, placedBlocks);
        skippedBlocks = Math.max(0, skippedBlocks);
        failedBlocks = Math.max(0, failedBlocks);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }

    public boolean completeSuccess() {
        return attemptedBlocks > 0
                && placedBlocks == attemptedBlocks
                && skippedBlocks == 0
                && failedBlocks == 0
                && undoSnapshotRecorded
                && auditRecorded;
    }

    public boolean shouldPauseJob() {
        return failedBlocks > 0 || skippedBlocks > 0 || !undoSnapshotRecorded || !auditRecorded;
    }
}
