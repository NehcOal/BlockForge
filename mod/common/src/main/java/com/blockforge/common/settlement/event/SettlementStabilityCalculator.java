package com.blockforge.common.settlement.event;

import java.util.ArrayList;

public class SettlementStabilityCalculator {
    public SettlementStability applyResolvedEvent(SettlementStability stability, SettlementEventOutcome outcome) {
        if (outcome == null || !outcome.resolved()) {
            return stability;
        }
        return adjust(stability, outcome.stabilityDelta(), 0, 0, 0, 0, -5);
    }

    public SettlementStability applyIgnoredEvent(SettlementStability stability, SettlementEvent event) {
        int debt = switch (event.severity()) {
            case LOW -> 5;
            case NORMAL -> 10;
            case HIGH -> 18;
            case CRITICAL -> 25;
        };
        return adjust(stability, -debt / 2, 0, -debt / 3, -debt / 3, 0, debt);
    }

    public SettlementStability applyEventPressure(SettlementStability stability, SettlementEvent event) {
        return switch (event.type()) {
            case RESOURCE_SHORTAGE, STORAGE_OVERFLOW -> adjust(stability, -5, 0, 0, -12, 0, 5);
            case STRUCTURE_DAMAGED, MINE_COLLAPSE, DEFENSE_REQUEST -> adjust(stability, -8, 0, -12, 0, 0, 8);
            case MARKET_DEMAND -> adjust(stability, 0, 8, 0, -4, 0, 0);
            case CELEBRATION_BUILD -> adjust(stability, 4, 0, 0, 0, 10, 0);
            default -> adjust(stability, event.stabilityImpact(), 0, 0, 0, 0, 0);
        };
    }

    private SettlementStability adjust(SettlementStability base, int stabilityDelta, int prosperityDelta, int safetyDelta, int logisticsDelta, int cultureDelta, int debtDelta) {
        var problems = new ArrayList<>(base.activeProblems());
        if (base.stability() + stabilityDelta < 30 && !problems.contains("low_stability")) {
            problems.add("low_stability");
        }
        if (base.logistics() + logisticsDelta < 30 && !problems.contains("logistics_strain")) {
            problems.add("logistics_strain");
        }
        return new SettlementStability(
                base.settlementId(),
                base.stability() + stabilityDelta,
                base.prosperity() + prosperityDelta,
                base.safety() + safetyDelta,
                base.logistics() + logisticsDelta,
                base.culture() + cultureDelta,
                base.maintenanceDebt() + debtDelta,
                problems
        );
    }
}
