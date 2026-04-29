package com.blockforge.common.buildplan;

import java.util.List;
import java.util.Set;

public final class RepairPlanFactory {
    private RepairPlanFactory() {
    }

    public static BuildPlan missingOnly(BuildPlan sourcePlan, Set<String> presentCoordinates, long gameTime) {
        Set<String> safePresent = presentCoordinates == null ? Set.of() : presentCoordinates;
        List<BuildLayer> repairLayers = sourcePlan.layers()
                .stream()
                .map(layer -> {
                    List<BuildStep> missing = layer.steps()
                            .stream()
                            .filter(step -> !safePresent.contains(key(step)))
                            .toList();
                    return new BuildLayer(layer.y(), missing, missing.size());
                })
                .filter(layer -> !layer.steps().isEmpty())
                .toList();
        return new BuildPlan(
                sourcePlan.planId() + ":repair:" + gameTime,
                sourcePlan.playerId(),
                sourcePlan.blueprintId(),
                sourcePlan.displayName() + " Repair",
                sourcePlan.dimensionId(),
                sourcePlan.baseX(),
                sourcePlan.baseY(),
                sourcePlan.baseZ(),
                sourcePlan.rotationDegrees(),
                sourcePlan.mirrorX(),
                sourcePlan.mirrorZ(),
                sourcePlan.offsetX(),
                sourcePlan.offsetY(),
                sourcePlan.offsetZ(),
                0,
                0,
                repairLayers,
                BuildPlanStatus.READY,
                gameTime
        );
    }

    private static String key(BuildStep step) {
        return step.x() + "," + step.y() + "," + step.z();
    }
}
