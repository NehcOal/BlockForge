package com.blockforge.common.security.protection;

import java.util.List;

public record BlockForgeProtectionRegion(
        String id,
        String dimensionId,
        int minX,
        int minY,
        int minZ,
        int maxX,
        int maxY,
        int maxZ,
        BlockForgeRegionMode mode,
        List<String> allowedPlayers,
        List<String> deniedPlayers,
        List<String> allowedPermissions,
        List<String> tags,
        String description
) {
    public BlockForgeProtectionRegion {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Protection region id cannot be blank.");
        }
        dimensionId = dimensionId == null ? "" : dimensionId;
        int normalizedMinX = Math.min(minX, maxX);
        int normalizedMinY = Math.min(minY, maxY);
        int normalizedMinZ = Math.min(minZ, maxZ);
        int normalizedMaxX = Math.max(minX, maxX);
        int normalizedMaxY = Math.max(minY, maxY);
        int normalizedMaxZ = Math.max(minZ, maxZ);
        minX = normalizedMinX;
        minY = normalizedMinY;
        minZ = normalizedMinZ;
        maxX = normalizedMaxX;
        maxY = normalizedMaxY;
        maxZ = normalizedMaxZ;
        mode = mode == null ? BlockForgeRegionMode.DENY : mode;
        allowedPlayers = allowedPlayers == null ? List.of() : List.copyOf(allowedPlayers);
        deniedPlayers = deniedPlayers == null ? List.of() : List.copyOf(deniedPlayers);
        allowedPermissions = allowedPermissions == null ? List.of() : List.copyOf(allowedPermissions);
        tags = tags == null ? List.of() : List.copyOf(tags);
        description = description == null ? "" : description;
    }
}
