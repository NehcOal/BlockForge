package com.blockforge.common.security.protection;

public record BuildArea(
        String dimensionId,
        int minX,
        int minY,
        int minZ,
        int maxX,
        int maxY,
        int maxZ,
        int blockCount
) {
    public BuildArea {
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
        blockCount = Math.max(0, blockCount);
    }
}
