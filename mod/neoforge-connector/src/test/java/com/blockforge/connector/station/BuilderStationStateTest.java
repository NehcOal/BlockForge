package com.blockforge.connector.station;

import com.blockforge.common.station.BuilderStationState;
import com.blockforge.common.station.BuilderStationStatus;
import com.blockforge.common.station.ConstructionCoreState;
import com.blockforge.common.station.MaterialLinkRef;
import com.blockforge.common.station.MaterialLinkType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuilderStationStateTest {
    @Test
    void stationStateNormalizesAndBindsBlueprint() {
        BuilderStationState state = new BuilderStationState(
                "station-1",
                "minecraft:overworld",
                1,
                64,
                2,
                "player-1",
                "",
                "",
                List.of("cache-1"),
                "",
                BuilderStationStatus.IDLE,
                0,
                -2,
                12,
                10L,
                9L
        );

        BuilderStationState bound = state.bindBlueprint("tiny_platform", 20L);

        assertEquals(1, state.maxBlocksPerTick());
        assertEquals(0, state.placedBlocks());
        assertEquals(BuilderStationStatus.READY, bound.status());
        assertEquals("tiny_platform", bound.boundBlueprintId());
        assertEquals(20L, bound.updatedAtGameTime());
    }

    @Test
    void materialLinksAndConstructionCoreUsePureData() {
        MaterialLinkRef link = new MaterialLinkRef(
                "link-1",
                "minecraft:overworld",
                3,
                64,
                3,
                "cache-1",
                MaterialLinkType.MATERIAL_CACHE
        );
        ConstructionCoreState core = new ConstructionCoreState(
                "core-1",
                "minecraft:overworld",
                0,
                64,
                0,
                "project-1",
                List.of("station-1"),
                List.of("anchor-1"),
                List.of("cache-1"),
                "player-1"
        );

        assertTrue(link.sameDimension("minecraft:overworld"));
        assertEquals(List.of("station-1"), core.stationIds());
    }
}
