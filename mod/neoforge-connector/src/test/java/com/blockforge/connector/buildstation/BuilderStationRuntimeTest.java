package com.blockforge.connector.buildstation;

import com.blockforge.common.buildplan.BuildLayer;
import com.blockforge.common.buildplan.BuildPlan;
import com.blockforge.common.buildplan.BuildPlanStatus;
import com.blockforge.common.buildplan.BuildStep;
import com.blockforge.common.buildplan.BuildStepStatus;
import com.blockforge.common.buildstation.BuilderStationJob;
import com.blockforge.common.buildstation.BuilderStationJobStatus;
import com.blockforge.common.buildstation.BuilderStationPlacementBatch;
import com.blockforge.common.buildstation.BuilderStationRuntimeConfig;
import com.blockforge.common.buildstation.BuilderStationTickExecutor;
import com.blockforge.common.buildstation.StationTickContext;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuilderStationRuntimeTest {
    @Test
    void tickPlacesAtMostConfiguredBatchAndUpdatesProgress() {
        BuildPlan plan = plan(5);
        BuilderStationJob job = BuilderStationJob.queued("job-1", UUID.randomUUID(), plan.planId(), plan.blueprintId(), plan.totalBlocks(), 1L);
        BuilderStationRuntimeConfig config = new BuilderStationRuntimeConfig(true, 2, 4, true, false, true, true, false);

        BuilderStationPlacementBatch first = BuilderStationTickExecutor.tick(job, plan, config, StationTickContext.ready(), 2L);
        BuilderStationPlacementBatch second = BuilderStationTickExecutor.tick(first.job(), first.plan(), config, StationTickContext.ready(), 3L);

        assertEquals(2, first.progress().placedBlocks());
        assertEquals(4, second.progress().placedBlocks());
        assertEquals(BuilderStationJobStatus.RUNNING, second.job().status());
        assertFalse(second.completed());
    }

    @Test
    void tickCompletesWhenAllStepsArePlaced() {
        BuildPlan plan = plan(2);
        BuilderStationJob job = BuilderStationJob.queued("job-1", UUID.randomUUID(), plan.planId(), plan.blueprintId(), plan.totalBlocks(), 1L);

        BuilderStationPlacementBatch batch = BuilderStationTickExecutor.tick(job, plan, BuilderStationRuntimeConfig.defaults(), StationTickContext.ready(), 2L);

        assertTrue(batch.completed());
        assertEquals(BuildPlanStatus.COMPLETED, batch.plan().status());
        assertEquals(BuilderStationJobStatus.COMPLETED, batch.job().status());
        assertEquals(100.0, batch.progress().percent());
    }

    @Test
    void unloadedChunkPausesWithoutPlacing() {
        BuildPlan plan = plan(2);
        BuilderStationJob job = BuilderStationJob.queued("job-1", UUID.randomUUID(), plan.planId(), plan.blueprintId(), plan.totalBlocks(), 1L);

        BuilderStationPlacementBatch batch = BuilderStationTickExecutor.tick(
                job,
                plan,
                BuilderStationRuntimeConfig.defaults(),
                new StationTickContext(false, true, true, true, true),
                2L
        );

        assertEquals(0, batch.progress().placedBlocks());
        assertEquals(BuildPlanStatus.PAUSED, batch.plan().status());
        assertEquals("chunk_unloaded", batch.issues().getFirst().type());
    }

    @Test
    void protectionDeniedFailsWithoutPlacing() {
        BuildPlan plan = plan(2);
        BuilderStationJob job = BuilderStationJob.queued("job-1", UUID.randomUUID(), plan.planId(), plan.blueprintId(), plan.totalBlocks(), 1L);

        BuilderStationPlacementBatch batch = BuilderStationTickExecutor.tick(
                job,
                plan,
                BuilderStationRuntimeConfig.defaults(),
                new StationTickContext(true, false, true, true, true),
                2L
        );

        assertEquals(0, batch.progress().placedBlocks());
        assertEquals(BuildPlanStatus.FAILED, batch.plan().status());
        assertEquals(BuilderStationJobStatus.FAILED, batch.job().status());
    }

    private static BuildPlan plan(int steps) {
        return new BuildPlan(
                "plan-1",
                UUID.randomUUID(),
                "tiny",
                "Tiny",
                "minecraft:overworld",
                0,
                64,
                0,
                0,
                false,
                false,
                0,
                0,
                0,
                0,
                0,
                List.of(new BuildLayer(64, java.util.stream.IntStream.range(0, steps)
                        .mapToObj(index -> new BuildStep(index, 64, 0, "minecraft:stone", "stone", "stone", false, BuildStepStatus.PENDING))
                        .toList(), steps)),
                BuildPlanStatus.READY,
                1L
        );
    }
}
