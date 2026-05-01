package com.blockforge.common.buildstation;

public record StationTickContext(
        boolean loadedChunk,
        boolean protectionAllowed,
        boolean materialsAvailable,
        boolean quotaAllowed,
        boolean cooldownReady
) {
    public static StationTickContext ready() {
        return new StationTickContext(true, true, true, true, true);
    }
}
