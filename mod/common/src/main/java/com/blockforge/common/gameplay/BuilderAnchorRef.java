package com.blockforge.common.gameplay;

public record BuilderAnchorRef(
        String id,
        String dimensionId,
        int x,
        int y,
        int z,
        int rotationDegrees,
        String ownerPlayerId,
        String name
) {
    public BuilderAnchorRef {
        id = id == null ? "" : id;
        dimensionId = dimensionId == null ? "" : dimensionId;
        ownerPlayerId = ownerPlayerId == null ? "" : ownerPlayerId;
        name = name == null || name.isBlank() ? id : name;
        rotationDegrees = ((rotationDegrees % 360) + 360) % 360;
    }
}
