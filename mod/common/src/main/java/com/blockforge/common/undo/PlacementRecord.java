package com.blockforge.common.undo;

import com.blockforge.common.material.MaterialTransaction;

public record PlacementRecord(
        String blueprintId,
        long createdAtGameTime,
        int placedBlocks,
        MaterialTransaction materialTransaction
) {
    public int consumedItemCount() {
        return materialTransaction == null ? 0 : materialTransaction.totalConsumedItems();
    }
}
