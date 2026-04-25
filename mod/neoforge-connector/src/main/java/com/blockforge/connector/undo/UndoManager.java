package com.blockforge.connector.undo;

import com.blockforge.connector.config.BlockForgeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UndoManager {
    private final Map<UUID, Deque<PlacementSnapshot>> snapshotsByPlayer = new HashMap<>();

    public void record(PlacementSnapshot snapshot) {
        if (snapshot == null || snapshot.placedBlocks() <= 0 || snapshot.entries().isEmpty()) {
            return;
        }

        Deque<PlacementSnapshot> snapshots = snapshotsByPlayer.computeIfAbsent(
                snapshot.playerId(),
                ignored -> new ArrayDeque<>()
        );
        snapshots.addFirst(snapshot);

        while (snapshots.size() > BlockForgeConfig.maxUndoSnapshotsPerPlayer()) {
            snapshots.removeLast();
        }
    }

    public List<PlacementSnapshot> list(UUID playerId) {
        Deque<PlacementSnapshot> snapshots = snapshotsByPlayer.get(playerId);
        if (snapshots == null || snapshots.isEmpty()) {
            return List.of();
        }

        return Collections.unmodifiableList(new ArrayList<>(snapshots));
    }

    public Optional<PlacementSnapshot> popLatest(UUID playerId) {
        Deque<PlacementSnapshot> snapshots = snapshotsByPlayer.get(playerId);
        if (snapshots == null || snapshots.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(snapshots.removeFirst());
    }

    public int clear(UUID playerId) {
        Deque<PlacementSnapshot> snapshots = snapshotsByPlayer.remove(playerId);
        return snapshots == null ? 0 : snapshots.size();
    }

    public UndoResult restore(ServerLevel level, PlacementSnapshot snapshot) {
        int restored = 0;
        List<BlockSnapshotEntry> entries = snapshot.entries();

        for (int index = entries.size() - 1; index >= 0; index--) {
            BlockSnapshotEntry entry = entries.get(index);
            BlockPos pos = entry.pos();
            level.setBlock(pos, entry.previousState(), Block.UPDATE_ALL);

            if (entry.blockEntityTag() != null) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity != null) {
                    blockEntity.loadWithComponents(entry.blockEntityTag().copy(), level.registryAccess());
                    blockEntity.setChanged();
                }
            }

            restored++;
        }

        return new UndoResult(restored, snapshot.blueprintId());
    }

    public record UndoResult(int restoredBlocks, String blueprintId) {
    }
}
