package com.blockforge.connector.release;

import com.blockforge.common.material.MaterialRefundResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryFullRefundFallbackTest {
    @Test
    void refundResultCanRepresentDroppedFallbackWithoutLosingItems() {
        MaterialRefundResult result = new MaterialRefundResult(12, 4, List.of("Inventory full; dropped 4 item(s)."));

        assertEquals(12, result.refundedItems());
        assertEquals(4, result.droppedItems());
        assertEquals(16, result.refundedItems() + result.droppedItems());
        assertTrue(result.warnings().getFirst().contains("Inventory full"));
    }
}
