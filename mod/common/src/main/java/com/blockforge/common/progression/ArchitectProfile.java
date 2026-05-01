package com.blockforge.common.progression;

import java.util.List;
import java.util.UUID;

public record ArchitectProfile(
        UUID playerId,
        int reputation,
        int experience,
        int level,
        List<String> unlockedFeatures,
        List<String> completedContractIds
) {
    public ArchitectProfile {
        if (playerId == null) {
            throw new IllegalArgumentException("playerId is required");
        }
        reputation = Math.max(0, reputation);
        experience = Math.max(0, experience);
        level = Math.max(1, level);
        unlockedFeatures = unlockedFeatures == null ? List.of() : List.copyOf(unlockedFeatures);
        completedContractIds = completedContractIds == null ? List.of() : List.copyOf(completedContractIds);
    }

    public static ArchitectProfile create(UUID playerId) {
        return new ArchitectProfile(playerId, 0, 0, 1, List.of(), List.of());
    }
}
