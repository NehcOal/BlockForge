package com.blockforge.common.buildplan;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BuildPlanManager {
    private final Map<UUID, BuildPlan> plans = new ConcurrentHashMap<>();

    public Optional<BuildPlan> get(UUID playerId) {
        return Optional.ofNullable(plans.get(playerId));
    }

    public BuildPlan save(UUID playerId, BuildPlan plan) {
        BuildPlan existing = plans.get(playerId);
        if (existing != null && existing.status() == BuildPlanStatus.RUNNING) {
            throw new IllegalStateException("A running BuildPlan cannot be overwritten. Pause or cancel it first.");
        }
        plans.put(playerId, plan);
        return plan;
    }

    public Optional<BuildPlan> setStatus(UUID playerId, BuildPlanStatus status) {
        return Optional.ofNullable(plans.computeIfPresent(playerId, (id, plan) -> plan.withStatus(status)));
    }

    public Optional<BuildPlan> clear(UUID playerId) {
        BuildPlan existing = plans.get(playerId);
        if (existing != null && existing.status() == BuildPlanStatus.RUNNING) {
            return Optional.empty();
        }
        return Optional.ofNullable(plans.remove(playerId));
    }
}
