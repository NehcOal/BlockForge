package com.blockforge.common.settlement.event;

import java.util.List;

public class SettlementEventRewardService {
    public SettlementEventOutcome resolve(SettlementEvent event) {
        int stabilityDelta = Math.max(1, Math.abs(event.stabilityImpact()));
        return new SettlementEventOutcome(event.eventId(), true, Math.max(0, event.reputationImpact()), stabilityDelta, event.relatedContractIds(), List.of("settlement_event_reward"), List.of());
    }

    public SettlementEventOutcome fail(SettlementEvent event) {
        return new SettlementEventOutcome(event.eventId(), false, 0, Math.min(0, event.stabilityImpact()), List.of(), List.of(), List.of("event failed or expired"));
    }
}
