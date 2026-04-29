package com.blockforge.common.serverplay;

public record BuildCooldown(
        String playerId,
        String action,
        long lastUsedGameTime,
        int cooldownSeconds
) {
    public BuildCooldown {
        playerId = playerId == null ? "" : playerId;
        action = action == null ? "" : action;
        cooldownSeconds = Math.max(0, cooldownSeconds);
    }

    public boolean ready(long currentGameTime) {
        return remainingTicks(currentGameTime) == 0;
    }

    public long remainingTicks(long currentGameTime) {
        long cooldownTicks = cooldownSeconds * 20L;
        long elapsed = Math.max(0, currentGameTime - lastUsedGameTime);
        return Math.max(0, cooldownTicks - elapsed);
    }
}
