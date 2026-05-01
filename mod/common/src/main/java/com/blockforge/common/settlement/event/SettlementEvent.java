package com.blockforge.common.settlement.event;

import java.util.List;

public record SettlementEvent(
        String eventId,
        String settlementId,
        SettlementEventType type,
        SettlementEventSeverity severity,
        SettlementEventStatus status,
        String title,
        String description,
        List<String> relatedContractIds,
        List<String> relatedProjectIds,
        int stabilityImpact,
        int reputationImpact,
        long createdAtGameTime,
        long expiresAtGameTime,
        long resolvedAtGameTime,
        List<String> warnings
) {
    public SettlementEvent {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("eventId is required");
        }
        if (settlementId == null || settlementId.isBlank()) {
            throw new IllegalArgumentException("settlementId is required");
        }
        type = type == null ? SettlementEventType.CUSTOM : type;
        severity = severity == null ? SettlementEventSeverity.NORMAL : severity;
        status = status == null ? SettlementEventStatus.ACTIVE : status;
        title = title == null ? eventId : title;
        description = description == null ? "" : description;
        relatedContractIds = relatedContractIds == null ? List.of() : List.copyOf(relatedContractIds);
        relatedProjectIds = relatedProjectIds == null ? List.of() : List.copyOf(relatedProjectIds);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }

    public SettlementEvent resolve(long gameTime) {
        return withStatus(SettlementEventStatus.RESOLVED, gameTime);
    }

    public SettlementEvent expire(long gameTime) {
        return withStatus(SettlementEventStatus.EXPIRED, gameTime);
    }

    public SettlementEvent ignore(long gameTime) {
        return withStatus(SettlementEventStatus.IGNORED, gameTime);
    }

    private SettlementEvent withStatus(SettlementEventStatus nextStatus, long gameTime) {
        return new SettlementEvent(eventId, settlementId, type, severity, nextStatus, title, description, relatedContractIds, relatedProjectIds, stabilityImpact, reputationImpact, createdAtGameTime, expiresAtGameTime, gameTime, warnings);
    }
}
