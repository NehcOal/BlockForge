package com.blockforge.connector.material;

import com.blockforge.connector.blueprint.Blueprint;
import com.blockforge.connector.blueprint.BlueprintBlock;
import com.blockforge.connector.blueprint.BlueprintPaletteEntry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaterialCounterTest {
    @Test
    void countsOnlyTheProvidedBlueprintBlocks() {
        BlueprintBlock firstBlock = new BlueprintBlock(0, 0, 0, "stone");
        BlueprintBlock skippedBlock = new BlueprintBlock(1, 0, 0, "stone");
        Blueprint blueprint = new Blueprint(
                2,
                "partial_build",
                "Partial Build",
                "",
                "1.21.1",
                "BlockForge",
                new Blueprint.BlueprintSize(2, 1, 1),
                Map.of("stone", new BlueprintPaletteEntry("minecraft:stone", Map.of())),
                List.of(firstBlock, skippedBlock)
        );

        MaterialReport report = new MaterialCounter(blockId -> "minecraft:stone").withAvailability(
                blueprint,
                List.of(firstBlock),
                Map.of("minecraft:stone", 1)
        );

        assertEquals(1, report.totalBlocks());
        assertEquals(1, report.totalRequiredItems());
        assertEquals(1, report.totalAvailableItems());
    }
}
