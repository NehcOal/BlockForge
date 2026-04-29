package com.blockforge.common.gameplay;

public record MaterialCacheInventorySlot(
        int slot,
        String itemId,
        int count,
        int maxCount
) {
    public MaterialCacheInventorySlot {
        slot = Math.max(0, slot);
        itemId = itemId == null ? "" : itemId;
        maxCount = Math.max(1, maxCount);
        count = Math.max(0, Math.min(count, maxCount));
    }

    public boolean empty() {
        return itemId.isBlank() || count <= 0;
    }

    public boolean canMerge(String candidateItemId) {
        return !empty() && itemId.equals(candidateItemId) && count < maxCount;
    }

    public int space() {
        return Math.max(0, maxCount - count);
    }
}
