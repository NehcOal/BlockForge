package com.blockforge.common.station;

import java.util.List;

public record ConstructionCoreState(
        String coreId,
        String dimensionId,
        int x,
        int y,
        int z,
        String projectId,
        List<String> stationIds,
        List<String> anchorIds,
        List<String> materialCacheIds,
        String ownerPlayerId
) {
    public ConstructionCoreState {
        coreId = coreId == null ? "" : coreId;
        dimensionId = dimensionId == null ? "" : dimensionId;
        projectId = projectId == null ? "" : projectId;
        stationIds = stationIds == null ? List.of() : List.copyOf(stationIds);
        anchorIds = anchorIds == null ? List.of() : List.copyOf(anchorIds);
        materialCacheIds = materialCacheIds == null ? List.of() : List.copyOf(materialCacheIds);
        ownerPlayerId = ownerPlayerId == null ? "" : ownerPlayerId;
    }
}
