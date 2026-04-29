package com.blockforge.common.buildplan;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.blueprint.BlueprintBlock;
import com.blockforge.common.rotation.BlueprintRotation;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class BuildPlanFactory {
    private BuildPlanFactory() {
    }

    public static BuildPlan create(
            Blueprint blueprint,
            UUID playerId,
            String dimensionId,
            int baseX,
            int baseY,
            int baseZ,
            int rotationDegrees,
            boolean mirrorX,
            boolean mirrorZ,
            int offsetX,
            int offsetY,
            int offsetZ,
            BuildPlanOptions options,
            long gameTime
    ) {
        BuildPlanOptions resolvedOptions = options == null ? BuildPlanOptions.defaults() : options;
        BlueprintRotation rotation = rotationFromDegrees(rotationDegrees);
        int resolvedBaseX = baseX + offsetX;
        int resolvedBaseY = baseY + offsetY;
        int resolvedBaseZ = baseZ + offsetZ;
        List<BuildStep> steps = blueprint.getBlocks()
                .stream()
                .sorted(Comparator.comparingInt(BlueprintBlock::getY)
                        .thenComparingInt(BlueprintBlock::getX)
                        .thenComparingInt(BlueprintBlock::getZ)
                        .thenComparing(BlueprintBlock::getState))
                .map(block -> BuildStepPlanner.stepFor(
                        blueprint,
                        block,
                        resolvedBaseX,
                        resolvedBaseY,
                        resolvedBaseZ,
                        rotation,
                        mirrorX,
                        mirrorZ,
                        !resolvedOptions.replaceAirOnly()
                ))
                .toList();
        List<BuildLayer> layers = resolvedOptions.layerByLayer()
                ? BuildLayerPlanner.planLayers(steps)
                : List.of(new BuildLayer(resolvedBaseY, steps, steps.size()));
        String planId = stablePlanId(playerId, blueprint.getId(), resolvedBaseX, resolvedBaseY, resolvedBaseZ, rotationDegrees, mirrorX, mirrorZ);
        BuildPlanStatus status = BuildPlanValidator.validateLayers(layers, 0, 319).isEmpty()
                ? BuildPlanStatus.READY
                : BuildPlanStatus.DRAFT;
        return new BuildPlan(
                planId,
                playerId,
                blueprint.getId(),
                blueprint.getName(),
                dimensionId,
                resolvedBaseX,
                resolvedBaseY,
                resolvedBaseZ,
                rotationDegrees,
                mirrorX,
                mirrorZ,
                offsetX,
                offsetY,
                offsetZ,
                steps.size(),
                layers.size(),
                layers,
                status,
                gameTime
        );
    }

    public static BlueprintRotation rotationFromDegrees(int rotationDegrees) {
        int normalized = ((rotationDegrees % 360) + 360) % 360;
        return switch (normalized) {
            case 90 -> BlueprintRotation.CLOCKWISE_90;
            case 180 -> BlueprintRotation.CLOCKWISE_180;
            case 270 -> BlueprintRotation.COUNTERCLOCKWISE_90;
            default -> BlueprintRotation.NONE;
        };
    }

    private static String stablePlanId(UUID playerId, String blueprintId, int x, int y, int z, int rotationDegrees, boolean mirrorX, boolean mirrorZ) {
        return (playerId == null ? "server" : playerId)
                + ":"
                + blueprintId
                + "@"
                + x
                + ","
                + y
                + ","
                + z
                + ":r"
                + rotationDegrees
                + ":mx"
                + mirrorX
                + ":mz"
                + mirrorZ;
    }
}
