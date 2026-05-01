package com.blockforge.common.settlement.event;

import java.util.List;

public record SettlementEventOutcome(
        String eventId,
        boolean resolved,
        int awardedReputation,
        int stabilityDelta,
        List<String> generatedContractIds,
        List<String> generatedRewardIds,
        List<String> warnings
) {
    public SettlementEventOutcome {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("eventId is required");
        }
        generatedContractIds = generatedContractIds == null ? List.of() : List.copyOf(generatedContractIds);
        generatedRewardIds = generatedRewardIds == null ? List.of() : List.copyOf(generatedRewardIds);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }
}
