package com.blockforge.connector.buildstation;

import com.blockforge.common.buildstation.BuilderStationJob;
import com.blockforge.common.buildstation.BuilderStationJobStatus;
import com.blockforge.common.buildstation.BuilderStationQueue;
import com.blockforge.common.serverrules.ServerBuildRuleEvaluator;
import com.blockforge.common.serverrules.ServerBuildRules;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuilderStationQueueTest {
    @Test
    void queueTracksJobsAndProgress() {
        BuilderStationQueue queue = new BuilderStationQueue();
        UUID playerId = UUID.randomUUID();
        BuilderStationJob job = queue.enqueue(BuilderStationJob.queued("job-1", playerId, "plan-1", "tiny", 10, 1L), 2);

        assertEquals(1, queue.list().size());
        assertEquals(BuilderStationJobStatus.QUEUED, job.status());

        BuilderStationJob running = queue.update("job-1", current -> current.withStatus(BuilderStationJobStatus.RUNNING, 2L)).orElseThrow();
        assertEquals(BuilderStationJobStatus.RUNNING, running.status());

        BuilderStationJob complete = queue.update("job-1", current -> current
                .withStatus(BuilderStationJobStatus.COMPLETED, 3L)
                .withProgress(10, 3L)
        ).orElseThrow();
        assertEquals(BuilderStationJobStatus.COMPLETED, complete.status());
        assertEquals(100.0, complete.percent());
    }

    @Test
    void queueLimitIsEnforced() {
        BuilderStationQueue queue = new BuilderStationQueue();
        queue.enqueue(BuilderStationJob.queued("job-1", UUID.randomUUID(), "plan-1", "tiny", 1, 1L), 1);

        assertThrows(IllegalStateException.class, () -> queue.enqueue(BuilderStationJob.queued("job-2", UUID.randomUUID(), "plan-2", "tiny", 1, 2L), 1));
    }

    @Test
    void serverRulesCanRequireAnchor() {
        ServerBuildRules rules = new ServerBuildRules(true, 1, 16, 32, true, true, true, false, "blockforge");

        assertTrue(ServerBuildRuleEvaluator.canQueueBuilderStationJob(rules, 0, 0, false).reason().contains("Builder Anchor"));
    }
}
