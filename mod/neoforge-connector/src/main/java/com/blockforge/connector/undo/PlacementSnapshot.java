package com.blockforge.connector.undo;

import java.util.List;
import java.util.UUID;

public record PlacementSnapshot(
        UUID playerId,
        String playerName,
        String blueprintId,
        long createdAtGameTime,
        int placedBlocks,
        List<BlockSnapshotEntry> entries
) {
    public PlacementSnapshot {
        entries = List.copyOf(entries);
    }
}
