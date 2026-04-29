package com.blockforge.common.gameplaygui;

import com.blockforge.common.station.BuilderStationState;
import com.blockforge.common.station.BuilderStationStatus;

import java.util.List;

public record BuilderStationStatusView(
        String stationId,
        String boundBlueprintId,
        String boundAnchorId,
        List<String> boundMaterialCacheIds,
        String activeBuildPlanId,
        String jobId,
        BuilderStationStatus status,
        int placedBlocks,
        int totalBlocks,
        int currentLayer,
        int totalLayers,
        int issuesCount,
        boolean canCreatePlan,
        boolean canStart,
        boolean canPause,
        boolean canResume,
        boolean canStep,
        boolean canCancel,
        List<String> warnings
) {
    public BuilderStationStatusView {
        stationId = stationId == null ? "" : stationId;
        boundBlueprintId = boundBlueprintId == null ? "" : boundBlueprintId;
        boundAnchorId = boundAnchorId == null ? "" : boundAnchorId;
        boundMaterialCacheIds = boundMaterialCacheIds == null ? List.of() : List.copyOf(boundMaterialCacheIds);
        activeBuildPlanId = activeBuildPlanId == null ? "" : activeBuildPlanId;
        jobId = jobId == null ? "" : jobId;
        status = status == null ? BuilderStationStatus.IDLE : status;
        placedBlocks = Math.max(0, placedBlocks);
        totalBlocks = Math.max(0, totalBlocks);
        currentLayer = Math.max(0, currentLayer);
        totalLayers = Math.max(0, totalLayers);
        issuesCount = Math.max(0, issuesCount);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }

    public static BuilderStationStatusView fromState(BuilderStationState state, String jobId, int currentLayer, int totalLayers, int issuesCount) {
        if (state == null) {
            return new BuilderStationStatusView("", "", "", List.of(), "", jobId, BuilderStationStatus.IDLE, 0, 0, currentLayer, totalLayers, issuesCount, false, false, false, false, false, false, List.of("No station state is available."));
        }
        boolean hasBlueprint = !state.boundBlueprintId().isBlank();
        boolean hasAnchor = !state.boundAnchorId().isBlank();
        boolean hasPlan = !state.activeBuildPlanId().isBlank();
        boolean idleOrReady = state.status() == BuilderStationStatus.IDLE || state.status() == BuilderStationStatus.READY || state.status() == BuilderStationStatus.COMPLETED || state.status() == BuilderStationStatus.CANCELLED || state.status() == BuilderStationStatus.FAILED;
        boolean running = state.status() == BuilderStationStatus.RUNNING;
        boolean paused = state.status() == BuilderStationStatus.PAUSED;
        boolean startReady = hasPlan && state.status() == BuilderStationStatus.READY;
        boolean stepReady = hasPlan && (state.status() == BuilderStationStatus.READY || paused);
        return new BuilderStationStatusView(
                state.stationId(),
                state.boundBlueprintId(),
                state.boundAnchorId(),
                state.boundMaterialCacheIds(),
                state.activeBuildPlanId(),
                jobId,
                state.status(),
                state.placedBlocks(),
                state.totalBlocks(),
                currentLayer,
                totalLayers,
                issuesCount,
                hasBlueprint && hasAnchor && idleOrReady,
                startReady,
                running,
                paused,
                stepReady,
                hasPlan && (running || paused || state.status() == BuilderStationStatus.READY),
                warningsFor(state, hasBlueprint, hasAnchor)
        );
    }

    private static List<String> warningsFor(BuilderStationState state, boolean hasBlueprint, boolean hasAnchor) {
        java.util.ArrayList<String> warnings = new java.util.ArrayList<>();
        if (!hasBlueprint) {
            warnings.add("No blueprint bound.");
        }
        if (!hasAnchor) {
            warnings.add("No anchor bound.");
        }
        if (state.boundMaterialCacheIds().isEmpty()) {
            warnings.add("No material cache bound.");
        }
        return List.copyOf(warnings);
    }
}
