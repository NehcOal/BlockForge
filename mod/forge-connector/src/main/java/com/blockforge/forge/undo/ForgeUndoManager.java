package com.blockforge.forge.undo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentHashMap;

public class ForgeUndoManager {
    private static final int MAX_HISTORY_PER_PLAYER = 20;
    private static final int RESTORE_FLAGS = Block.UPDATE_CLIENTS
            | Block.UPDATE_SUPPRESS_DROPS
            | Block.UPDATE_KNOWN_SHAPE;

    private final Map<UUID, ConcurrentLinkedDeque<PlacementSnapshot>> historyByPlayer = new ConcurrentHashMap<>();

    public void record(PlacementSnapshot snapshot) {
        if (snapshot == null || snapshot.entries().isEmpty()) {
            return;
        }

        ConcurrentLinkedDeque<PlacementSnapshot> history = historyByPlayer.computeIfAbsent(
                snapshot.playerId(),
                ignored -> new ConcurrentLinkedDeque<>()
        );
        history.addLast(snapshot);
        while (history.size() > MAX_HISTORY_PER_PLAYER) {
            history.pollFirst();
        }
    }

    public Optional<PlacementSnapshot> popLatest(UUID playerId) {
        ConcurrentLinkedDeque<PlacementSnapshot> history = historyByPlayer.get(playerId);
        if (history == null) {
            return Optional.empty();
        }

        PlacementSnapshot snapshot = history.pollLast();
        if (history.isEmpty()) {
            historyByPlayer.remove(playerId, history);
        }
        return Optional.ofNullable(snapshot);
    }

    public UndoResult restore(ServerLevel level, ServerPlayer player, PlacementSnapshot snapshot) {
        int restored = 0;
        List<BlockSnapshotEntry> entries = snapshot.entries();

        for (int index = entries.size() - 1; index >= 0; index--) {
            BlockSnapshotEntry entry = entries.get(index);
            level.setBlock(entry.position(), entry.previousState(), RESTORE_FLAGS);
            restored++;
        }

        return new UndoResult(snapshot.blueprintId(), player.getUUID(), restored);
    }

    public record PlacementSnapshot(
            UUID playerId,
            String playerName,
            String blueprintId,
            long createdAtGameTime,
            int placedBlocks,
            List<BlockSnapshotEntry> entries
    ) {
        public PlacementSnapshot {
            entries = entries == null ? List.of() : List.copyOf(entries);
        }
    }

    public record BlockSnapshotEntry(BlockPos position, BlockState previousState) {
        public BlockSnapshotEntry {
            position = position.immutable();
        }
    }

    public record UndoResult(String blueprintId, UUID playerId, int restoredBlocks) {
    }
}
