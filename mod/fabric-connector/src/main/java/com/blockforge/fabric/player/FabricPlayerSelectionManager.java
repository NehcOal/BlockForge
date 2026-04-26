package com.blockforge.fabric.player;

import com.blockforge.common.selection.BlueprintRotationSelection;
import com.blockforge.common.selection.PlayerSelection;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FabricPlayerSelectionManager {
    public static final long WAND_COOLDOWN_MILLIS = 2_000L;

    private final Map<UUID, PlayerSelection> selections = new ConcurrentHashMap<>();

    public PlayerSelection select(UUID playerId, String blueprintId) {
        PlayerSelection existing = selections.get(playerId);
        PlayerSelection updated = existing == null
                ? new PlayerSelection(playerId, blueprintId, 0, 0)
                : existing.withBlueprint(blueprintId);
        selections.put(playerId, updated);
        return updated;
    }

    public Optional<PlayerSelection> get(UUID playerId) {
        return Optional.ofNullable(selections.get(playerId));
    }

    public Optional<PlayerSelection> rotate(UUID playerId, int rotationDegrees) {
        BlueprintRotationSelection.validate(rotationDegrees);
        PlayerSelection existing = selections.get(playerId);
        if (existing == null) {
            return Optional.empty();
        }

        PlayerSelection updated = existing.withRotation(rotationDegrees);
        selections.put(playerId, updated);
        return Optional.of(updated);
    }

    public boolean isCoolingDown(PlayerSelection selection, long nowMillis) {
        return selection.lastBuildTimeMillis() > 0
                && nowMillis - selection.lastBuildTimeMillis() < WAND_COOLDOWN_MILLIS;
    }

    public long remainingCooldownMillis(PlayerSelection selection, long nowMillis) {
        long elapsed = Math.max(0, nowMillis - selection.lastBuildTimeMillis());
        return Math.max(0, WAND_COOLDOWN_MILLIS - elapsed);
    }

    public PlayerSelection markBuilt(PlayerSelection selection, long nowMillis) {
        PlayerSelection updated = selection.withLastBuildTimeMillis(nowMillis);
        selections.put(selection.playerId(), updated);
        return updated;
    }
}
