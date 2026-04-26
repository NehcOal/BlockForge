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
import java.util.concurrent.ConcurrentHashMap;

public class ForgeUndoManager {
    private static final int RESTORE_FLAGS = Block.UPDATE_CLIENTS
            | Block.UPDATE_SUPPRESS_DROPS
            | Block.UPDATE_KNOWN_SHAPE;

    private final Map<UUID, PlacementSnapshot> latestByPlayer = new ConcurrentHashMap<>();

    public void record(PlacementSnapshot snapshot) {
        if (snapshot == null || snapshot.entries().isEmpty()) {
            return;
        }

        latestByPlayer.put(snapshot.playerId(), snapshot);
    }

    public Optional<PlacementSnapshot> popLatest(UUID playerId) {
        return Optional.ofNullable(latestByPlayer.remove(playerId));
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
