package com.blockforge.common.buildstation;

import com.blockforge.common.buildplan.BuildIssue;
import com.blockforge.common.buildplan.BuildPlan;
import com.blockforge.common.buildplan.BuildPlanStatus;
import com.blockforge.common.buildplan.BuildPlanStepper;
import com.blockforge.common.buildplan.BuildProgress;
import com.blockforge.common.buildplan.BuildStepStatus;

import java.util.ArrayList;
import java.util.List;

public final class BuilderStationTickExecutor {
    private BuilderStationTickExecutor() {
    }

    public static BuilderStationPlacementBatch tick(
            BuilderStationJob job,
            BuildPlan plan,
            BuilderStationRuntimeConfig config,
            StationTickContext context,
            long gameTime
    ) {
        BuilderStationRuntimeConfig resolvedConfig = config == null ? BuilderStationRuntimeConfig.defaults() : config;
        StationTickContext resolvedContext = context == null ? StationTickContext.ready() : context;
        List<BuildIssue> issues = new ArrayList<>();

        if (!resolvedConfig.enabled()) {
            issues.add(issue("station_disabled", "error", "Builder Station runtime is disabled."));
            return paused(job, plan, issues, gameTime);
        }
        if (!resolvedContext.loadedChunk()) {
            issues.add(issue("chunk_unloaded", "warning", "Target chunk is not loaded; station will not force-load chunks."));
            return paused(job, plan, issues, gameTime);
        }
        if (!resolvedContext.protectionAllowed()) {
            issues.add(issue("protection_denied", "error", "Protection preflight denied this station batch."));
            return failed(job, plan, issues, gameTime);
        }
        if (!resolvedContext.quotaAllowed()) {
            issues.add(issue("quota_denied", "error", "Server quota denied this station batch."));
            return paused(job, plan, issues, gameTime);
        }
        if (!resolvedContext.cooldownReady()) {
            issues.add(issue("cooldown", "info", "Station cooldown is still active."));
            return paused(job, plan, issues, gameTime);
        }
        if (!resolvedContext.materialsAvailable() && !resolvedConfig.allowPartialBuild()) {
            issues.add(issue("materials_missing", "error", "Required materials are not available."));
            return paused(job, plan, issues, gameTime);
        }

        BuildPlan runningPlan = plan.status() == BuildPlanStatus.RUNNING ? plan : plan.withStatus(BuildPlanStatus.RUNNING);
        BuildPlan nextPlan = BuildPlanStepper.markNextBatch(runningPlan, resolvedConfig.maxBlocksPerTick(), BuildStepStatus.PLACED);
        BuildProgress progress = BuildProgress.fromPlan(nextPlan);
        BuilderStationJobStatus nextStatus = nextPlan.status() == BuildPlanStatus.COMPLETED
                ? BuilderStationJobStatus.COMPLETED
                : BuilderStationJobStatus.RUNNING;
        BuilderStationJob nextJob = job.withStatus(nextStatus, gameTime).withProgress(progress.placedBlocks(), gameTime);

        return new BuilderStationPlacementBatch(job.jobId(), nextPlan, progress, nextJob, issues, nextStatus == BuilderStationJobStatus.COMPLETED);
    }

    private static BuilderStationPlacementBatch paused(BuilderStationJob job, BuildPlan plan, List<BuildIssue> issues, long gameTime) {
        BuildProgress progress = BuildProgress.fromPlan(plan);
        return new BuilderStationPlacementBatch(job.jobId(), plan.withStatus(BuildPlanStatus.PAUSED), progress, job.withStatus(BuilderStationJobStatus.PAUSED, gameTime), issues, false);
    }

    private static BuilderStationPlacementBatch failed(BuilderStationJob job, BuildPlan plan, List<BuildIssue> issues, long gameTime) {
        BuildProgress progress = BuildProgress.fromPlan(plan);
        return new BuilderStationPlacementBatch(job.jobId(), plan.withStatus(BuildPlanStatus.FAILED), progress, job.withStatus(BuilderStationJobStatus.FAILED, gameTime), issues, false);
    }

    private static BuildIssue issue(String type, String severity, String message) {
        return new BuildIssue(type, severity, 0, 0, 0, message, "");
    }
}
