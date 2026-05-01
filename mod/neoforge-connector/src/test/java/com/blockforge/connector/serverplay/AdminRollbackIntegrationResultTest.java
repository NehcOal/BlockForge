package com.blockforge.connector.serverplay;

import com.blockforge.common.serverplay.AdminRollbackIntegrationResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminRollbackIntegrationResultTest {
    @Test
    void successfulRollbackRequiresSnapshotBlocksAuditAndStationUpdate() {
        AdminRollbackIntegrationResult result = new AdminRollbackIntegrationResult("job", true, true, true, true, true, List.of());

        assertTrue(result.successful());
        assertFalse(result.partial());
    }

    @Test
    void missingMaterialRefundCanStillBePartialButNotSuccessful() {
        AdminRollbackIntegrationResult result = new AdminRollbackIntegrationResult("job", true, true, false, true, true, List.of("refund unavailable"));

        assertTrue(result.successful());
        assertFalse(result.partial());
    }

    @Test
    void missingSnapshotIsNotPartialRollback() {
        AdminRollbackIntegrationResult result = new AdminRollbackIntegrationResult("job", false, false, false, false, false, List.of("no snapshot"));

        assertFalse(result.successful());
        assertFalse(result.partial());
    }
}
