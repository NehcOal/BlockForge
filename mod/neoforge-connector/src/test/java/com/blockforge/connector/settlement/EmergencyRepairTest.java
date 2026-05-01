package com.blockforge.connector.settlement;

import com.blockforge.common.settlement.emergency.EmergencyRepairRequest;
import com.blockforge.common.settlement.emergency.EmergencyRepairVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmergencyRepairTest {
    @Test
    void emergencyRepairPassesWhenIssuesAreResolved() {
        EmergencyRepairRequest request = new EmergencyRepairRequest("repair-1", "settlement-1", "event-1", "bridge", "minecraft:overworld", 0, 64, 0, 10, 0, 80, 1000);

        var result = new EmergencyRepairVerifier().verify(request, 10, 0, 200);

        assertTrue(result.passed());
        assertTrue(result.warnings().isEmpty());
    }

    @Test
    void emergencyRepairTimeoutFailsVerification() {
        EmergencyRepairRequest request = new EmergencyRepairRequest("repair-1", "settlement-1", "event-1", "bridge", "minecraft:overworld", 0, 64, 0, 10, 0, 80, 1000);

        var result = new EmergencyRepairVerifier().verify(request, 10, 0, 1000);

        assertFalse(result.passed());
        assertTrue(result.warnings().contains("repair request expired"));
    }
}
