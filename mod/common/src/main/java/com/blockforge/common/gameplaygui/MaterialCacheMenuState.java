package com.blockforge.common.gameplaygui;

import java.util.List;

public record MaterialCacheMenuState(
        String cacheId,
        String dimensionId,
        int x,
        int y,
        int z,
        int slotCount,
        int usedSlots,
        int linkedStationCount,
        String materialSourcePriority,
        boolean accessible,
        boolean protectedLocation,
        List<String> warnings
) {
    public MaterialCacheMenuState {
        cacheId = cacheId == null ? "" : cacheId;
        dimensionId = dimensionId == null ? "" : dimensionId;
        slotCount = Math.max(0, slotCount);
        usedSlots = Math.max(0, Math.min(usedSlots, slotCount));
        linkedStationCount = Math.max(0, linkedStationCount);
        materialSourcePriority = materialSourcePriority == null || materialSourcePriority.isBlank()
                ? "CACHE_FIRST"
                : materialSourcePriority;
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }

    public int emptySlots() {
        return Math.max(0, slotCount - usedSlots);
    }

    public boolean full() {
        return slotCount > 0 && usedSlots >= slotCount;
    }
}
