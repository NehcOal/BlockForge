package com.blockforge.connector.buildplan;

import com.blockforge.common.buildplan.BuildLayer;
import com.blockforge.common.buildplan.BuildPlan;
import com.blockforge.common.buildplan.BuildPlanStatus;
import com.blockforge.common.buildplan.BuildProgress;
import com.blockforge.common.buildplan.BuildStep;
import com.blockforge.common.buildplan.BuildStepStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuildProgressTest {
    @Test
    void computesPercentFromStepStatuses() {
        BuildPlan plan = new BuildPlan(
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
                        step(BuildStepStatus.PLACED),
                        step(BuildStepStatus.SKIPPED),
                        step(BuildStepStatus.PENDING),
                        step(BuildStepStatus.FAILED)
                ), 4)),
                BuildPlanStatus.RUNNING,
                1L
        );

        BuildProgress progress = BuildProgress.fromPlan(plan);

        assertEquals(1, progress.placedBlocks());
        assertEquals(1, progress.skippedBlocks());
        assertEquals(1, progress.failedBlocks());
        assertEquals(75.0, progress.percent());
    }

    private static BuildStep step(BuildStepStatus status) {
        return new BuildStep(0, 0, 0, "minecraft:stone", "stone", "stone", false, status);
    }
}
