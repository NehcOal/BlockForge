package com.blockforge.common.settlement;

import java.util.List;

public record SettlementLevel(
        int level,
        int requiredReputation,
        int maxActiveContracts,
        int maxStations,
        int maxMaterialCaches,
        List<String> unlockedFeatures
) {
    public SettlementLevel {
        if (level < 1) {
            throw new IllegalArgumentException("level must be >= 1");
        }
        if (requiredReputation < 0) {
            throw new IllegalArgumentException("requiredReputation must be >= 0");
        }
        unlockedFeatures = unlockedFeatures == null ? List.of() : List.copyOf(unlockedFeatures);
    }
}
