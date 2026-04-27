package com.blockforge.common.security.protection;

import java.util.List;
import java.util.UUID;

public record ProtectionCheckRequest(
        UUID playerId,
        String playerName,
        String dimensionId,
        ProtectionAction action,
        BuildArea area,
        List<BlockForgeProtectionRegion> regions
) {
    public ProtectionCheckRequest {
        playerName = playerName == null ? "" : playerName;
        dimensionId = dimensionId == null ? "" : dimensionId;
        regions = regions == null ? List.of() : List.copyOf(regions);
    }
}
