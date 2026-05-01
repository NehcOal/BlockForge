package com.blockforge.common.gameplaygui;

import com.blockforge.common.station.ConstructionCoreState;

public record ConstructionCoreStatusView(
        String coreId,
        String projectId,
        String ownerPlayerId,
        int stationCount,
        int anchorCount,
        int cacheCount,
        int activeJobCount
) {
    public ConstructionCoreStatusView {
        coreId = coreId == null ? "" : coreId;
        projectId = projectId == null ? "" : projectId;
        ownerPlayerId = ownerPlayerId == null ? "" : ownerPlayerId;
        stationCount = Math.max(0, stationCount);
        anchorCount = Math.max(0, anchorCount);
        cacheCount = Math.max(0, cacheCount);
        activeJobCount = Math.max(0, activeJobCount);
    }

    public static ConstructionCoreStatusView fromState(ConstructionCoreState state, int activeJobCount) {
        if (state == null) {
            return new ConstructionCoreStatusView("", "", "", 0, 0, 0, activeJobCount);
        }
        return new ConstructionCoreStatusView(
                state.coreId(),
                state.projectId(),
                state.ownerPlayerId(),
                state.stationIds().size(),
                state.anchorIds().size(),
                state.materialCacheIds().size(),
                activeJobCount
        );
    }
}
