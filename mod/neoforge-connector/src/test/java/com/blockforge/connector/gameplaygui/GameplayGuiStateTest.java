package com.blockforge.connector.gameplaygui;

import com.blockforge.common.gameplaygui.BuilderStationAction;
import com.blockforge.common.gameplaygui.BuilderStationActionValidator;
import com.blockforge.common.gameplaygui.BuilderStationStatusView;
import com.blockforge.common.gameplaygui.ConstructionCoreStatusView;
import com.blockforge.common.gameplaygui.MaterialCacheMenuState;
import com.blockforge.common.station.BuilderStationState;
import com.blockforge.common.station.BuilderStationStatus;
import com.blockforge.common.station.ConstructionCoreState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameplayGuiStateTest {
    @Test
    void materialCacheMenuStateNormalizesSlotCounts() {
        MaterialCacheMenuState state = new MaterialCacheMenuState(
                "cache-1",
                "minecraft:overworld",
                1,
                64,
                2,
                9,
                20,
                2,
                "",
                true,
                false,
                List.of()
        );

        assertEquals(9, state.usedSlots());
        assertEquals(0, state.emptySlots());
        assertTrue(state.full());
        assertEquals("CACHE_FIRST", state.materialSourcePriority());
    }

    @Test
    void stationStatusViewExposesSafeButtonStates() {
        BuilderStationState station = new BuilderStationState(
                "station-1",
                "minecraft:overworld",
                0,
                64,
                0,
                "owner",
                "tiny_platform",
                "anchor-1",
                List.of("cache-1"),
                "plan-1",
                BuilderStationStatus.PAUSED,
                8,
                4,
                10,
                1L,
                2L
        );

        BuilderStationStatusView view = BuilderStationStatusView.fromState(station, "job-1", 1, 2, 0);

        assertTrue(view.canStart());
        assertTrue(view.canResume());
        assertFalse(view.canPause());
        assertEquals(List.of(), view.warnings());
    }

    @Test
    void stationActionValidatorChecksPermissionsQuotaAndCooldown() {
        BuilderStationStatusView view = new BuilderStationStatusView(
                "station-1",
                "tiny",
                "anchor-1",
                List.of("cache-1"),
                "plan-1",
                "job-1",
                BuilderStationStatus.READY,
                0,
                10,
                0,
                1,
                0,
                true,
                true,
                false,
                false,
                true,
                true,
                List.of()
        );

        assertTrue(BuilderStationActionValidator.validate(view, BuilderStationAction.START, true, true, true).allowed());
        assertFalse(BuilderStationActionValidator.validate(view, BuilderStationAction.START, false, true, true).allowed());
        assertFalse(BuilderStationActionValidator.validate(view, BuilderStationAction.START, true, false, true).allowed());
        assertFalse(BuilderStationActionValidator.validate(view, BuilderStationAction.STEP, true, true, false).allowed());
    }

    @Test
    void constructionCoreStatusSummarizesProjectLinks() {
        ConstructionCoreState core = new ConstructionCoreState(
                "core-1",
                "minecraft:overworld",
                0,
                64,
                0,
                "project-1",
                List.of("station-1", "station-2"),
                List.of("anchor-1"),
                List.of("cache-1", "cache-2"),
                "owner"
        );

        ConstructionCoreStatusView view = ConstructionCoreStatusView.fromState(core, 1);

        assertEquals(2, view.stationCount());
        assertEquals(1, view.anchorCount());
        assertEquals(2, view.cacheCount());
        assertEquals(1, view.activeJobCount());
    }
}
