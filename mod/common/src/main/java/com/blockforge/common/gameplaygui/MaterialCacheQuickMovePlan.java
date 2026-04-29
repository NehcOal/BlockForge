package com.blockforge.common.gameplaygui;

public record MaterialCacheQuickMovePlan(
        int cacheSlotCount,
        int playerInventorySlotCount,
        int hotbarSlotCount
) {
    public MaterialCacheQuickMovePlan {
        cacheSlotCount = Math.max(0, cacheSlotCount);
        playerInventorySlotCount = Math.max(0, playerInventorySlotCount);
        hotbarSlotCount = Math.max(0, hotbarSlotCount);
    }

    public int totalSlots() {
        return cacheSlotCount + playerInventorySlotCount + hotbarSlotCount;
    }

    public boolean isCacheSlot(int slotIndex) {
        return slotIndex >= 0 && slotIndex < cacheSlotCount;
    }

    public boolean isPlayerInventorySlot(int slotIndex) {
        return slotIndex >= cacheSlotCount && slotIndex < cacheSlotCount + playerInventorySlotCount;
    }

    public boolean isHotbarSlot(int slotIndex) {
        return slotIndex >= cacheSlotCount + playerInventorySlotCount && slotIndex < totalSlots();
    }

    public QuickMoveTarget targetFor(int slotIndex) {
        if (isCacheSlot(slotIndex)) {
            return QuickMoveTarget.PLAYER_INVENTORY;
        }
        if (isPlayerInventorySlot(slotIndex) || isHotbarSlot(slotIndex)) {
            return QuickMoveTarget.MATERIAL_CACHE;
        }
        return QuickMoveTarget.INVALID;
    }

    public enum QuickMoveTarget {
        MATERIAL_CACHE,
        PLAYER_INVENTORY,
        INVALID
    }
}
