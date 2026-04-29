package com.blockforge.common.gameplay;

public record PlacementOptions(
        boolean replaceAirOnly,
        boolean protectBlockEntities,
        boolean consumeMaterials,
        boolean useNearbyContainers,
        boolean useMaterialCaches,
        boolean allowPartialBuild,
        boolean mirrorX,
        boolean mirrorZ,
        int offsetX,
        int offsetY,
        int offsetZ
) {
    public static PlacementOptions defaults() {
        return new PlacementOptions(false, true, true, true, true, false, false, false, 0, 0, 0);
    }

    public int baseX(int clickedX) {
        return clickedX + offsetX;
    }

    public int baseY(int clickedY) {
        return clickedY + offsetY;
    }

    public int baseZ(int clickedZ) {
        return clickedZ + offsetZ;
    }
}
