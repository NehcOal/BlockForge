package com.blockforge.connector.gameplay;

import com.blockforge.common.gameplay.MaterialCacheInventory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaterialCacheInventoryTest {
    @Test
    void insertExtractAndDropsPreserveItems() {
        MaterialCacheInventory inventory = MaterialCacheInventory.empty(2);

        MaterialCacheInventory.InsertResult inserted = inventory.insert("minecraft:stone", 70);
        assertEquals(70, inserted.inserted());
        assertEquals(0, inserted.remainder());
        assertEquals(2, inserted.inventory().usedSlots());
        assertEquals(70, inserted.inventory().countItem("minecraft:stone"));

        MaterialCacheInventory.ExtractResult extracted = inserted.inventory().extract("minecraft:stone", 65);
        assertEquals(65, extracted.extracted());
        assertEquals(5, extracted.inventory().countItem("minecraft:stone"));
        assertEquals(1, extracted.inventory().dropsOnBreak().size());
    }

    @Test
    void fullCacheReturnsRemainder() {
        MaterialCacheInventory inventory = MaterialCacheInventory.empty(1);

        MaterialCacheInventory.InsertResult inserted = inventory.insert("minecraft:cobblestone", 80);

        assertEquals(64, inserted.inserted());
        assertEquals(16, inserted.remainder());
    }
}
