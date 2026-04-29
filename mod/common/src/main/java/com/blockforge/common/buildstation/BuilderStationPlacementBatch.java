package com.blockforge.common.buildstation;

import com.blockforge.common.buildplan.BuildIssue;
import com.blockforge.common.buildplan.BuildPlan;
import com.blockforge.common.buildplan.BuildProgress;

import java.util.List;

public record BuilderStationPlacementBatch(
        String jobId,
        BuildPlan plan,
        BuildProgress progress,
        BuilderStationJob job,
        List<BuildIssue> issues,
        boolean completed
) {
    public BuilderStationPlacementBatch {
        jobId = jobId == null ? "" : jobId;
        issues = issues == null ? List.of() : List.copyOf(issues);
    }
}
