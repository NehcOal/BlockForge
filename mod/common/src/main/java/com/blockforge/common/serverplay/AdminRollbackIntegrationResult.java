package com.blockforge.common.serverplay;

import java.util.List;

public record AdminRollbackIntegrationResult(
        String jobId,
        boolean snapshotFound,
        boolean blocksRestored,
        boolean materialsRefunded,
        boolean auditRecorded,
        boolean stationUpdated,
        List<String> warnings
) {
    public AdminRollbackIntegrationResult {
        jobId = jobId == null ? "" : jobId;
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }

    public boolean successful() {
        return snapshotFound && blocksRestored && auditRecorded && stationUpdated;
    }

    public boolean partial() {
        return snapshotFound && !successful();
    }
}
