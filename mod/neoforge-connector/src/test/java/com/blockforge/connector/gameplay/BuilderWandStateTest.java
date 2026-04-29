package com.blockforge.connector.gameplay;

import com.blockforge.common.gameplay.BuilderWandMode;
import com.blockforge.common.gameplay.BuilderWandState;
import com.blockforge.common.gameplay.BuilderWandStateStore;
import com.blockforge.common.gameplay.PlacementOptions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuilderWandStateTest {
    @Test
    void defaultStateBuildsWithMaterialCachesEnabled() {
        BuilderWandState state = BuilderWandState.defaults(UUID.randomUUID());
        PlacementOptions options = state.toPlacementOptions();

        assertEquals(BuilderWandMode.BUILD, state.mode());
        assertTrue(options.useMaterialCaches());
        assertEquals(10, options.baseX(10));
        assertEquals(64, options.baseY(64));
        assertEquals(-4, options.baseZ(-4));
    }

    @Test
    void offsetMirrorAndAnchorArePureState() {
        BuilderWandState state = BuilderWandState.defaults(UUID.randomUUID())
                .withOffset(2, -1, 3, 10L)
                .withMirror(true, false, 11L)
                .withAnchor("0,64,0", 12L);

        assertEquals(7, state.toPlacementOptions().baseX(5));
        assertEquals(63, state.toPlacementOptions().baseY(64));
        assertEquals(4, state.toPlacementOptions().baseZ(1));
        assertTrue(state.mirroredX());
        assertFalse(state.mirroredZ());
        assertEquals("0,64,0", state.anchorId());
        assertEquals(12L, state.lastUsedGameTime());
    }

    @Test
    void storeCyclesModesAndNormalizesMissingState() {
        UUID playerId = UUID.randomUUID();
        BuilderWandStateStore store = new BuilderWandStateStore();

        BuilderWandState next = store.cycle(playerId, 20L);

        assertEquals(BuilderWandMode.DRY_RUN, next.mode());
        assertEquals(20L, next.lastUsedGameTime());
        assertEquals(BuilderWandMode.MATERIALS, store.setMode(playerId, BuilderWandMode.MATERIALS, 21L).mode());
    }
}
