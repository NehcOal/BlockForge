package com.blockforge.connector.buildstation;

import com.blockforge.common.buildstation.StationWorldPlacementContext;
import com.blockforge.common.buildstation.StationWorldPlacementDecision;
import com.blockforge.common.buildstation.StationWorldPlacementGate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StationWorldPlacementGateTest {
    @Test
    void readyContextAllowsPlacement() {
        StationWorldPlacementDecision decision = StationWorldPlacementGate.evaluate(StationWorldPlacementContext.ready());

        assertTrue(decision.allowed());
    }

    @Test
    void missingMaterialPausesWithoutPlacement() {
        StationWorldPlacementDecision decision = StationWorldPlacementGate.evaluate(new StationWorldPlacementContext(
                true,
                true,
                true,
                true,
                true,
                false,
                true,
                false
        ));

        assertFalse(decision.allowed());
        assertTrue(decision.pauseJob());
        assertTrue(decision.issueType().contains("materials"));
    }

    @Test
    void protectionDeniedFailsBeforePlacement() {
        StationWorldPlacementDecision decision = StationWorldPlacementGate.evaluate(new StationWorldPlacementContext(
                true,
                true,
                false,
                true,
                true,
                true,
                true,
                false
        ));

        assertFalse(decision.allowed());
        assertTrue(decision.failJob());
    }
}
