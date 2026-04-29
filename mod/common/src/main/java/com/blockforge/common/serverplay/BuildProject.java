package com.blockforge.common.serverplay;

import java.util.List;

public record BuildProject(
        String projectId,
        String name,
        String ownerPlayerId,
        List<String> memberPlayerIds,
        List<String> stationIds,
        List<String> materialCacheIds,
        List<String> anchorIds,
        String status
) {
    public BuildProject {
        projectId = projectId == null ? "" : projectId;
        name = name == null ? "" : name;
        ownerPlayerId = ownerPlayerId == null ? "" : ownerPlayerId;
        memberPlayerIds = memberPlayerIds == null ? List.of() : List.copyOf(memberPlayerIds);
        stationIds = stationIds == null ? List.of() : List.copyOf(stationIds);
        materialCacheIds = materialCacheIds == null ? List.of() : List.copyOf(materialCacheIds);
        anchorIds = anchorIds == null ? List.of() : List.copyOf(anchorIds);
        status = status == null ? "draft" : status;
    }

    public boolean includesMember(String playerId) {
        String resolved = playerId == null ? "" : playerId;
        return ownerPlayerId.equals(resolved) || memberPlayerIds.contains(resolved);
    }
}
