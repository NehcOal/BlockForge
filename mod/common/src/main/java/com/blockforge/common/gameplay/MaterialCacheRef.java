package com.blockforge.common.gameplay;

public record MaterialCacheRef(
        String id,
        String dimensionId,
        int x,
        int y,
        int z,
        String ownerPlayerId,
        int priority
) {
    public MaterialCacheRef {
        id = id == null ? "" : id;
        dimensionId = dimensionId == null ? "" : dimensionId;
        ownerPlayerId = ownerPlayerId == null ? "" : ownerPlayerId;
    }
}
