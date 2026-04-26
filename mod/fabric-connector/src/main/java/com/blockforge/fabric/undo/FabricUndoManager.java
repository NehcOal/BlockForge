package com.blockforge.fabric.undo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FabricUndoManager {
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

    public UndoResult restore(ServerWorld world, ServerPlayerEntity player, PlacementSnapshot snapshot) {
        int restored = 0;
        List<BlockSnapshotEntry> entries = snapshot.entries();

        for (int index = entries.size() - 1; index >= 0; index--) {
            BlockSnapshotEntry entry = entries.get(index);
            world.setBlockState(entry.position(), entry.previousState(), Block.NOTIFY_ALL);
            restored++;
        }

        return new UndoResult(snapshot.blueprintId(), player.getUuid(), restored);
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
            position = position.toImmutable();
        }
    }

    public record UndoResult(String blueprintId, UUID playerId, int restoredBlocks) {
    }
}
