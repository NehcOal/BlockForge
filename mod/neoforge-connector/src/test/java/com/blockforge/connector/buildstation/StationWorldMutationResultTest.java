package com.blockforge.connector.buildstation;

import com.blockforge.common.buildstation.StationWorldMutationResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StationWorldMutationResultTest {
    @Test
    void completeMutationRequiresUndoAndAudit() {
        StationWorldMutationResult result = new StationWorldMutationResult("job", 4, 4, 0, 0, true, true, List.of());

        assertTrue(result.completeSuccess());
        assertFalse(result.shouldPauseJob());
    }

    @Test
    void missingAuditPausesJobEvenWhenBlocksWerePlaced() {
        StationWorldMutationResult result = new StationWorldMutationResult("job", 4, 4, 0, 0, true, false, List.of("audit failed"));

        assertFalse(result.completeSuccess());
        assertTrue(result.shouldPauseJob());
    }
}
