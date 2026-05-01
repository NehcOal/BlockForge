package com.blockforge.connector.serverplay;

import com.blockforge.common.serverplay.BuildAuditEntry;
import com.blockforge.common.serverplay.BuildAuditLog;
import com.blockforge.common.serverplay.BuildCooldown;
import com.blockforge.common.serverplay.BuildProject;
import com.blockforge.common.serverplay.BuildQuota;
import com.blockforge.common.serverplay.BuildQuotaChecker;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerPlayRulesTest {
    @Test
    void quotaRejectsDailyAndActiveJobOverflow() {
        BuildQuota dailyExceeded = new BuildQuota("player-1", 10, 8, 2, 0, 0L);
        BuildQuota jobsExceeded = new BuildQuota("player-1", 100, 8, 1, 1, 0L);

        assertFalse(BuildQuotaChecker.canStart(dailyExceeded, 4).allowed());
        assertFalse(BuildQuotaChecker.canStart(jobsExceeded, 1).allowed());
        assertTrue(BuildQuotaChecker.canStart(new BuildQuota("player-1", 100, 8, 2, 1, 0L), 4).allowed());
    }

    @Test
    void cooldownReportsRemainingTicks() {
        BuildCooldown cooldown = new BuildCooldown("player-1", "station_start", 100L, 5);

        assertFalse(cooldown.ready(120L));
        assertEquals(80L, cooldown.remainingTicks(120L));
        assertTrue(cooldown.ready(200L));
    }

    @Test
    void auditLogAndProjectMembershipAreQueryable() {
        BuildAuditLog log = new BuildAuditLog();
        log.record(new BuildAuditEntry(
                "audit-1",
                "job-1",
                "player-1",
                "Alex",
                "station_start",
                "tiny_platform",
                "minecraft:overworld",
                0,
                64,
                0,
                10,
                4,
                "builder_station",
                "started",
                List.of("alpha"),
                100L,
                "2026-01-01T00:00:00Z"
        ));
        BuildProject project = new BuildProject(
                "project-1",
                "Spawn",
                "owner",
                List.of("player-1"),
                List.of("station-1"),
                List.of("cache-1"),
                List.of("anchor-1"),
                "active"
        );

        assertEquals(1, log.byPlayer("Alex").size());
        assertTrue(project.includesMember("player-1"));
        assertTrue(project.includesMember("owner"));
    }
}
