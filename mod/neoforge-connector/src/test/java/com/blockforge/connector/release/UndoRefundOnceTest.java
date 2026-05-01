package com.blockforge.connector.release;

import com.blockforge.common.material.ConsumedMaterialEntry;
import com.blockforge.common.material.MaterialTransaction;
import com.blockforge.common.undo.PlacementRecord;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UndoRefundOnceTest {
    @Test
    void placementRecordReportsExactlyConsumedItemsForOneUndoSnapshot() {
        MaterialTransaction transaction = new MaterialTransaction(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "house-starter_cottage",
                List.of(new ConsumedMaterialEntry("minecraft:stone", 8), new ConsumedMaterialEntry("minecraft:oak_planks", 12)),
                100L,
                false
        );
        PlacementRecord record = new PlacementRecord("house-starter_cottage", 100L, 20, transaction);

        assertEquals(20, record.consumedItemCount());
        assertEquals(20, record.materialTransaction().totalConsumedItems());
    }
}
