package com.blockforge.common.gameplay;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BuilderWandStateStore {
    private final Map<UUID, BuilderWandState> states = new ConcurrentHashMap<>();

    public BuilderWandState getOrCreate(UUID playerId) {
        return states.computeIfAbsent(playerId, BuilderWandState::defaults);
    }

    public BuilderWandState update(UUID playerId, java.util.function.UnaryOperator<BuilderWandState> updater) {
        return states.compute(playerId, (id, existing) -> updater.apply(existing == null ? BuilderWandState.defaults(id) : existing));
    }

    public BuilderWandState setMode(UUID playerId, BuilderWandMode mode, long gameTime) {
        return update(playerId, state -> state.withMode(mode, gameTime));
    }

    public BuilderWandState cycleMode(UUID playerId, long gameTime) {
        return update(playerId, state -> state.withMode(state.mode().next(), gameTime));
    }

    public BuilderWandState cycle(UUID playerId, long gameTime) {
        return cycleMode(playerId, gameTime);
    }
}
