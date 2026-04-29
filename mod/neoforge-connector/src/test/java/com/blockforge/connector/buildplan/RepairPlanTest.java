package com.blockforge.connector.buildplan;

import com.blockforge.common.buildplan.BuildLayer;
import com.blockforge.common.buildplan.BuildPlan;
import com.blockforge.common.buildplan.BuildPlanStatus;
import com.blockforge.common.buildplan.BuildStep;
import com.blockforge.common.buildplan.BuildStepStatus;
import com.blockforge.common.buildplan.RepairPlanFactory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RepairPlanTest {
    @Test
    void repairPlanOnlyContainsMissingCoordinates() {
        BuildPlan source = new BuildPlan(
                "plan",
                UUID.randomUUID(),
                "bp",
                "Blueprint",
                "",
                0,
                0,
                0,
                0,
                false,
                false,
                0,
                0,
                0,
                0,
                0,
                List.of(new BuildLayer(0, List.of(
                        new BuildStep(0, 0, 0, "minecraft:stone", "stone", "stone", false, BuildStepStatus.PENDING),
                        new BuildStep(1, 0, 0, "minecraft:stone", "stone", "stone", false, BuildStepStatus.PENDING)
                ), 2)),
                BuildPlanStatus.READY,
                1L
        );

        BuildPlan repair = RepairPlanFactory.missingOnly(source, Set.of("0,0,0"), 2L);

        assertEquals(1, repair.totalBlocks());
        assertEquals(1, repair.layers().getFirst().steps().getFirst().x());
    }
}
