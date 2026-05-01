package com.blockforge.connector.gameplaygui;

import com.blockforge.common.gameplaygui.MaterialCacheQuickMovePlan;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaterialCacheQuickMovePlanTest {
    @Test
    void routesCacheSlotsToPlayerInventoryAndPlayerSlotsToCache() {
        MaterialCacheQuickMovePlan plan = new MaterialCacheQuickMovePlan(27, 27, 9);

        assertEquals(63, plan.totalSlots());
        assertTrue(plan.isCacheSlot(0));
        assertTrue(plan.isPlayerInventorySlot(27));
        assertTrue(plan.isHotbarSlot(62));
        assertEquals(MaterialCacheQuickMovePlan.QuickMoveTarget.PLAYER_INVENTORY, plan.targetFor(4));
        assertEquals(MaterialCacheQuickMovePlan.QuickMoveTarget.MATERIAL_CACHE, plan.targetFor(40));
        assertEquals(MaterialCacheQuickMovePlan.QuickMoveTarget.INVALID, plan.targetFor(99));
    }
}
