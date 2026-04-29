package com.blockforge.common.station;

import com.blockforge.common.buildplan.BuildIssue;
import com.blockforge.common.buildplan.BuildProgress;

import java.util.List;

public record BuilderStationJob(
        String jobId,
        String stationId,
        String buildPlanId,
        String blueprintId,
        BuilderStationStatus status,
        BuildProgress progress,
        List<BuildIssue> issues,
        long startedAtGameTime,
        long finishedAtGameTime
) {
    public BuilderStationJob {
        jobId = jobId == null ? "" : jobId;
        stationId = stationId == null ? "" : stationId;
        buildPlanId = buildPlanId == null ? "" : buildPlanId;
        blueprintId = blueprintId == null ? "" : blueprintId;
        status = status == null ? BuilderStationStatus.IDLE : status;
        issues = issues == null ? List.of() : List.copyOf(issues);
        finishedAtGameTime = Math.max(0, finishedAtGameTime);
    }

    public BuilderStationJob withStatus(BuilderStationStatus nextStatus, long gameTime) {
        return new BuilderStationJob(
                jobId,
                stationId,
                buildPlanId,
                blueprintId,
                nextStatus,
                progress,
                issues,
                startedAtGameTime,
                gameTime
        );
    }
}
