package com.blockforge.connector.progression;

import com.blockforge.common.progression.ArchitectProfile;
import com.blockforge.common.progression.ArchitectProgressionService;
import com.blockforge.common.progression.UnlockRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchitectProgressionTest {
    @Test
    void recalculatesLevelAndUnlocksFromReputation() {
        var profile = new ArchitectProfile(UUID.randomUUID(), 260, 100, 1, List.of(), List.of());

        var recalculated = new ArchitectProgressionService().recalculate(profile);

        assertEquals(3, recalculated.level());
        assertTrue(recalculated.unlockedFeatures().contains(UnlockRegistry.HARD_CONTRACTS));
    }
}
