package com.blockforge.common.buildplan;

import java.util.ArrayList;
import java.util.List;

public final class BuildPlanStepper {
    private BuildPlanStepper() {
    }

    public static BuildPlan markNextBatch(BuildPlan plan, int maxSteps, BuildStepStatus status) {
        int remaining = Math.max(1, maxSteps);
        List<BuildLayer> nextLayers = new ArrayList<>();
        for (BuildLayer layer : plan.layers()) {
            List<BuildStep> nextSteps = new ArrayList<>();
            for (BuildStep step : layer.steps()) {
                if (remaining > 0 && step.status() == BuildStepStatus.PENDING) {
                    nextSteps.add(step.withStatus(status));
                    remaining--;
                } else {
                    nextSteps.add(step);
                }
            }
            nextLayers.add(new BuildLayer(layer.y(), nextSteps, nextSteps.size()));
        }
        boolean complete = nextLayers.stream().flatMap(layer -> layer.steps().stream()).noneMatch(step -> step.status() == BuildStepStatus.PENDING);
        return plan.withLayers(nextLayers, complete ? BuildPlanStatus.COMPLETED : BuildPlanStatus.RUNNING);
    }
}
