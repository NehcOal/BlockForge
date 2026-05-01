package com.blockforge.connector.release;

import com.blockforge.common.buildstation.StationWorldPlacementContext;
import com.blockforge.common.buildstation.StationWorldPlacementDecision;
import com.blockforge.common.buildstation.StationWorldPlacementGate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BuildFailureMaterialRollbackTest {
    @Test
    void quotaDeniedPausesBeforeConsumingReservedMaterials() {
        StationWorldPlacementDecision decision = StationWorldPlacementGate.evaluate(
                new StationWorldPlacementContext(true, true, true, false, true, true, true, false)
        );

        assertFalse(decision.allowed());
        assertEquals("quota_denied", decision.issueType());
    }
}
