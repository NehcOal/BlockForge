package com.blockforge.common.buildplan;

public record BuildProgress(
        String planId,
        int placedBlocks,
        int skippedBlocks,
        int failedBlocks,
        int totalBlocks,
        int currentLayer,
        int totalLayers,
        double percent,
        BuildPlanStatus status
) {
    public BuildProgress {
        planId = planId == null ? "" : planId;
        totalBlocks = Math.max(0, totalBlocks);
        percent = totalBlocks == 0 ? 100.0 : Math.max(0.0, Math.min(100.0, percent));
        status = status == null ? BuildPlanStatus.DRAFT : status;
    }

    public static BuildProgress fromPlan(BuildPlan plan) {
        int placed = 0;
        int skipped = 0;
        int failed = 0;
        int currentLayer = 0;
        for (BuildLayer layer : plan.layers()) {
            boolean layerHasPending = false;
            for (BuildStep step : layer.steps()) {
                if (step.status() == BuildStepStatus.PLACED) {
                    placed++;
                } else if (step.status() == BuildStepStatus.SKIPPED) {
                    skipped++;
                } else if (step.status() == BuildStepStatus.FAILED) {
                    failed++;
                } else {
                    layerHasPending = true;
                }
            }
            if (!layerHasPending) {
                currentLayer++;
            }
        }
        double percent = plan.totalBlocks() == 0 ? 100.0 : ((placed + skipped + failed) * 100.0) / plan.totalBlocks();
        return new BuildProgress(plan.planId(), placed, skipped, failed, plan.totalBlocks(), currentLayer, plan.totalLayers(), percent, plan.status());
    }
}
