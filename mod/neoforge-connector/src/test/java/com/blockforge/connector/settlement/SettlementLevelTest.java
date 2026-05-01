package com.blockforge.connector.settlement;

import com.blockforge.common.progression.ArchitectProgressionService;
import com.blockforge.common.progression.UnlockRegistry;
import com.blockforge.common.settlement.SettlementLevel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SettlementLevelTest {
    @Test
    void validatesLevelAndUnlocks() {
        SettlementLevel level = new SettlementLevel(2, 100, 2, 1, 2, List.of(UnlockRegistry.BUILDER_STATION_ACCESS));

        assertEquals(2, level.level());
        assertTrue(level.unlockedFeatures().contains(UnlockRegistry.BUILDER_STATION_ACCESS));
        assertThrows(IllegalArgumentException.class, () -> new SettlementLevel(0, 0, 1, 1, 1, List.of()));
    }

    @Test
    void mapsReputationToArchitectLevel() {
        ArchitectProgressionService service = new ArchitectProgressionService();

        assertEquals(1, service.levelForReputation(99));
        assertEquals(2, service.levelForReputation(100));
        assertEquals(5, service.levelForReputation(1000));
    }
}
