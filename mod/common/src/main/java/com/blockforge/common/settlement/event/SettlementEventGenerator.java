package com.blockforge.common.settlement.event;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SettlementEventGenerator {
    private final SettlementEventPressureModel pressureModel;

    public SettlementEventGenerator() {
        this(new SettlementEventPressureModel());
    }

    public SettlementEventGenerator(SettlementEventPressureModel pressureModel) {
        this.pressureModel = pressureModel;
    }

    public List<SettlementEvent> generate(String settlementId, int settlementLevel, SettlementStability stability, List<SettlementEvent> activeEvents, int maxActiveEvents, long gameTime) {
        int activeCount = activeEvents == null ? 0 : activeEvents.stream().filter(event -> event.status() == SettlementEventStatus.ACTIVE).toList().size();
        int remaining = Math.max(0, maxActiveEvents - activeCount);
        if (remaining == 0) {
            return List.of();
        }
        boolean preferPositive = pressureModel.prefersPositiveEvents(stability);
        int negativePressure = pressureModel.negativePressure(stability);
        Set<String> unavailableEventIds = activeEvents == null ? Set.of() : activeEvents.stream()
                .filter(event -> event.status() == SettlementEventStatus.ACTIVE || recentlyFinished(event, gameTime))
                .map(SettlementEvent::eventId)
                .collect(Collectors.toSet());
        List<SettlementEvent> candidates = SettlementEventTemplates.templates(settlementId, gameTime).stream()
                .filter(event -> settlementLevel >= requiredLevel(event))
                .filter(event -> negativePressure >= 20 || preferPositive || event.severity() != SettlementEventSeverity.CRITICAL)
                .filter(event -> !unavailableEventIds.contains(event.eventId()))
                .sorted(preferPositive ? Comparator.comparing(SettlementEvent::stabilityImpact).reversed() : Comparator.comparing(SettlementEvent::stabilityImpact))
                .toList();
        candidates = rotateByGameTime(candidates, gameTime);

        List<SettlementEvent> selected = new ArrayList<>();
        boolean selectedCritical = false;
        for (SettlementEvent event : candidates) {
            if (event.severity() == SettlementEventSeverity.CRITICAL) {
                if (selectedCritical) {
                    continue;
                }
                selectedCritical = true;
            }
            selected.add(event);
            if (selected.size() >= remaining) {
                break;
            }
        }
        return selected;
    }

    private int requiredLevel(SettlementEvent event) {
        return switch (event.severity()) {
            case LOW -> 1;
            case NORMAL -> 1;
            case HIGH -> 2;
            case CRITICAL -> 3;
        };
    }

    private boolean recentlyFinished(SettlementEvent event, long gameTime) {
        if (event.status() == SettlementEventStatus.ACTIVE || event.resolvedAtGameTime() <= 0) {
            return false;
        }
        return gameTime - event.resolvedAtGameTime() < SettlementEventConfigDefaults.EVENT_REFRESH_INTERVAL_TICKS;
    }

    private List<SettlementEvent> rotateByGameTime(List<SettlementEvent> events, long gameTime) {
        if (events.size() <= 1) {
            return events;
        }
        int offset = (int) ((gameTime / SettlementEventConfigDefaults.EVENT_REFRESH_INTERVAL_TICKS) % events.size());
        if (offset == 0) {
            return events;
        }
        List<SettlementEvent> rotated = new ArrayList<>(events.size());
        rotated.addAll(events.subList(offset, events.size()));
        rotated.addAll(events.subList(0, offset));
        return rotated;
    }
}
