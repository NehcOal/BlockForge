package com.blockforge.connector.buildplan;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.blueprint.BlueprintBlock;
import com.blockforge.common.blueprint.BlueprintPaletteEntry;
import com.blockforge.common.blueprint.BlueprintSize;
import com.blockforge.common.buildplan.BuildPlan;
import com.blockforge.common.buildplan.BuildPlanFactory;
import com.blockforge.common.buildplan.BuildPlanOptions;
import com.blockforge.common.buildplan.BuildPlanStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuildPlanFactoryTest {
    @Test
    void createsStableLayeredPlanWithOffset() {
        UUID playerId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        BuildPlan first = BuildPlanFactory.create(blueprint(), playerId, "minecraft:overworld", 10, 64, 20, 0, false, false, 1, 2, 3, BuildPlanOptions.defaults(), 100L);
        BuildPlan second = BuildPlanFactory.create(blueprint(), playerId, "minecraft:overworld", 10, 64, 20, 0, false, false, 1, 2, 3, BuildPlanOptions.defaults(), 100L);

        assertEquals(first.planId(), second.planId());
        assertEquals(BuildPlanStatus.READY, first.status());
        assertEquals(3, first.totalBlocks());
        assertEquals(2, first.totalLayers());
        assertEquals(11, first.layers().getFirst().steps().getFirst().x());
        assertEquals(66, first.layers().getFirst().steps().getFirst().y());
        assertEquals(23, first.layers().getFirst().steps().getFirst().z());
    }

    @Test
    void mirrorsAndRotatesCoordinates() {
        BuildPlan plan = BuildPlanFactory.create(blueprint(), UUID.randomUUID(), "", 0, 0, 0, 90, true, false, 0, 0, 0, BuildPlanOptions.defaults(), 1L);

        assertEquals(0, plan.layers().getFirst().steps().getFirst().x());
        assertEquals(0, plan.layers().getFirst().steps().getFirst().z());
    }

    private static Blueprint blueprint() {
        return new Blueprint(
                2,
                "tiny_plan",
                "Tiny Plan",
                "",
                "1.21.1",
                "test",
                new BlueprintSize(2, 2, 2),
                Map.of("stone", new BlueprintPaletteEntry("minecraft:stone", Map.of())),
                List.of(
                        new BlueprintBlock(0, 0, 0, "stone"),
                        new BlueprintBlock(1, 0, 0, "stone"),
                        new BlueprintBlock(0, 1, 1, "stone")
                )
        );
    }
}
