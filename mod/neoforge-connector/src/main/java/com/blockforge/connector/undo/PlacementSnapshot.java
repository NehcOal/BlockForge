package com.blockforge.connector.undo;

import com.blockforge.connector.material.MaterialTransaction;

import java.util.List;
import java.util.UUID;

public record PlacementSnapshot(
        UUID playerId,
        String playerName,
        String blueprintId,
        long createdAtGameTime,
        int placedBlocks,
        List<BlockSnapshotEntry> entries,
        MaterialTransaction materialTransaction
) {
    public PlacementSnapshot(
            UUID playerId,
            String playerName,
            String blueprintId,
            long createdAtGameTime,
            int placedBlocks,
            List<BlockSnapshotEntry> entries
    ) {
        this(playerId, playerName, blueprintId, createdAtGameTime, placedBlocks, entries, null);
    }

    public PlacementSnapshot {
        entries = List.copyOf(entries);
    }

    public PlacementSnapshot withMaterialTransaction(MaterialTransaction transaction) {
        return new PlacementSnapshot(
                playerId,
                playerName,
                blueprintId,
                createdAtGameTime,
                placedBlocks,
                entries,
                transaction
        );
    }

    public int consumedItemCount() {
        return materialTransaction == null ? 0 : materialTransaction.totalConsumedItems();
    }
}
