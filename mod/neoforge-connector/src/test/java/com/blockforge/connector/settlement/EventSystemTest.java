package com.blockforge.connector.settlement;

import com.blockforge.common.settlement.event.SettlementEventConfigDefaults;
import com.blockforge.common.settlement.event.SettlementEventGenerator;
import com.blockforge.common.settlement.event.SettlementEventOutcome;
import com.blockforge.common.settlement.event.SettlementEventPersistenceFormat;
import com.blockforge.common.settlement.event.SettlementEventPressureModel;
import com.blockforge.common.settlement.event.SettlementEventRewardService;
import com.blockforge.common.settlement.event.SettlementEventScheduler;
import com.blockforge.common.settlement.event.SettlementEventSeverity;
import com.blockforge.common.settlement.event.SettlementEventTemplates;
import com.blockforge.common.settlement.event.SettlementStability;
import com.blockforge.common.settlement.event.SettlementStabilityCalculator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventSystemTest {
    @Test
    void eventGenerationRespectsMaxActiveEvents() {
        SettlementStability stability = SettlementStability.balanced("settlement-1");
        var events = new SettlementEventGenerator().generate("settlement-1", 3, stability, List.of(), 3, 100);

        assertEquals(3, events.size());
        assertTrue(events.stream().allMatch(event -> event.settlementId().equals("settlement-1")));
    }

    @Test
    void eventGenerationCapsCriticalEventsPerRefresh() {
        SettlementStability low = new SettlementStability("settlement-1", 15, 40, 10, 10, 40, 80, List.of());
        var events = new SettlementEventGenerator().generate("settlement-1", 3, low, List.of(), 3, 100);

        long criticalEvents = events.stream()
                .filter(event -> event.severity() == SettlementEventSeverity.CRITICAL)
                .count();

        assertTrue(criticalEvents <= 1);
    }

    @Test
    void eventGenerationSkipsRecentlyFinishedEvents() {
        var recent = SettlementEventTemplates.templates("settlement-1", 100).get(0).resolve(100);
        var events = new SettlementEventGenerator().generate("settlement-1", 3, SettlementStability.balanced("settlement-1"), List.of(recent), 3, 200);

        assertFalse(events.stream().anyMatch(event -> event.eventId().equals(recent.eventId())));
    }

    @Test
    void eventGenerationRotatesByGameTime() {
        SettlementStability stability = SettlementStability.balanced("settlement-1");

        String firstCycle = new SettlementEventGenerator().generate("settlement-1", 3, stability, List.of(), 1, 0).get(0).eventId();
        String secondCycle = new SettlementEventGenerator().generate("settlement-1", 3, stability, List.of(), 1, 24000).get(0).eventId();

        assertFalse(firstCycle.equals(secondCycle));
    }

    @Test
    void lowStabilityIncreasesNegativeEventPressure() {
        SettlementStability low = new SettlementStability("settlement-1", 20, 50, 20, 25, 50, 40, List.of());
        SettlementEventPressureModel pressure = new SettlementEventPressureModel();

        assertTrue(pressure.negativePressure(low) > 40);
        assertFalse(pressure.prefersPositiveEvents(low));
    }

    @Test
    void highStabilityUnlocksPositiveEvents() {
        SettlementStability high = new SettlementStability("settlement-1", 80, 75, 70, 70, 70, 0, List.of());
        SettlementEventPressureModel pressure = new SettlementEventPressureModel();

        assertTrue(pressure.prefersPositiveEvents(high));
    }

    @Test
    void resolvingEventGivesStabilityAndReputation() {
        var event = SettlementEventTemplates.templates("settlement-1", 10).get(0);
        SettlementEventOutcome outcome = new SettlementEventRewardService().resolve(event);
        SettlementStability next = new SettlementStabilityCalculator().applyResolvedEvent(SettlementStability.balanced("settlement-1"), outcome);

        assertTrue(outcome.resolved());
        assertTrue(outcome.awardedReputation() > 0);
        assertTrue(next.stability() > SettlementStability.balanced("settlement-1").stability());
    }

    @Test
    void ignoredEventAddsMaintenanceDebt() {
        var event = SettlementEventTemplates.templates("settlement-1", 10).stream()
                .filter(candidate -> candidate.severity() == SettlementEventSeverity.CRITICAL)
                .findFirst()
                .orElseThrow();
        SettlementStability next = new SettlementStabilityCalculator().applyIgnoredEvent(SettlementStability.balanced("settlement-1"), event);

        assertTrue(next.maintenanceDebt() > 0);
        assertTrue(next.stability() < SettlementStability.balanced("settlement-1").stability());
    }

    @Test
    void schedulerUsesGameTimeIntervals() {
        SettlementEventScheduler scheduler = new SettlementEventScheduler();

        assertFalse(scheduler.shouldRefresh(100, 200, 24000));
        assertTrue(scheduler.shouldRefresh(100, 24100, 24000));
    }

    @Test
    void eventConfigDefaultsMatchAlphaRules() {
        assertTrue(SettlementEventConfigDefaults.ENABLE_SETTLEMENT_EVENTS);
        assertEquals(3, SettlementEventConfigDefaults.MAX_ACTIVE_EVENTS_PER_SETTLEMENT);
        assertEquals(24000L, SettlementEventConfigDefaults.EVENT_REFRESH_INTERVAL_TICKS);
        assertEquals(72000L, SettlementEventConfigDefaults.EMERGENCY_REPAIR_TIMEOUT_TICKS);
    }

    @Test
    void persistencePathsPointAtBlockForgeConfig() {
        assertTrue(SettlementEventPersistenceFormat.EVENTS_FILE.contains("config/blockforge/settlements"));
        assertTrue(SettlementEventPersistenceFormat.REPAIRS_FILE.contains("config/blockforge/emergency"));
    }
}
