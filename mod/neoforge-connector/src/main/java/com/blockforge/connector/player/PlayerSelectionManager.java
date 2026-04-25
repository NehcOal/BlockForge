package com.blockforge.connector.player;

import com.blockforge.connector.builder.BlueprintRotation;
import com.blockforge.connector.config.BlockForgeConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSelectionManager {
    private final Map<UUID, PlayerBlueprintSelection> selections = new HashMap<>();

    public PlayerBlueprintSelection getOrCreate(UUID playerId) {
        return selections.computeIfAbsent(playerId, ignored -> new PlayerBlueprintSelection());
    }

    public void select(UUID playerId, String blueprintId) {
        getOrCreate(playerId).setSelectedBlueprintId(blueprintId);
    }

    public void rotate(UUID playerId, BlueprintRotation rotation) {
        getOrCreate(playerId).setRotation(rotation);
    }

    public CooldownResult checkCooldown(UUID playerId, long currentGameTime) {
        PlayerBlueprintSelection selection = getOrCreate(playerId);
        long elapsed = currentGameTime - selection.getLastBuildGameTime();
        long cooldownTicks = BlockForgeConfig.wandCooldownTicks();

        if (elapsed < cooldownTicks) {
            return new CooldownResult(false, cooldownTicks - elapsed);
        }

        selection.setLastBuildGameTime(currentGameTime);
        return new CooldownResult(true, 0);
    }

    public record CooldownResult(boolean allowed, long remainingTicks) {
        public double remainingSeconds() {
            return Math.max(0.0, remainingTicks / 20.0);
        }
    }
}
