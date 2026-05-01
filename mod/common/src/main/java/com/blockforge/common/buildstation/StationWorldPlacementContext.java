package com.blockforge.common.buildstation;

public record StationWorldPlacementContext(
        boolean loadedChunk,
        boolean sameDimension,
        boolean protectionAllowed,
        boolean quotaAllowed,
        boolean cooldownReady,
        boolean materialsReserved,
        boolean replacePolicyAllowed,
        boolean blockEntityProtected
) {
    public static StationWorldPlacementContext ready() {
        return new StationWorldPlacementContext(true, true, true, true, true, true, true, false);
    }
}
