package com.blockforge.common.settlement.event;

import java.util.List;

public record SettlementStability(
        String settlementId,
        int stability,
        int prosperity,
        int safety,
        int logistics,
        int culture,
        int maintenanceDebt,
        List<String> activeProblems
) {
    public SettlementStability {
        if (settlementId == null || settlementId.isBlank()) {
            throw new IllegalArgumentException("settlementId is required");
        }
        stability = clamp(stability);
        prosperity = clamp(prosperity);
        safety = clamp(safety);
        logistics = clamp(logistics);
        culture = clamp(culture);
        maintenanceDebt = clamp(maintenanceDebt);
        activeProblems = activeProblems == null ? List.of() : List.copyOf(activeProblems);
    }

    public static SettlementStability balanced(String settlementId) {
        return new SettlementStability(settlementId, 60, 50, 50, 50, 50, 0, List.of());
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
