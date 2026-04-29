package com.blockforge.connector.serverplay;

import com.blockforge.common.serverplay.AdminRollbackPlanner;
import com.blockforge.common.serverplay.AuditJsonlFormatter;
import com.blockforge.common.serverplay.BuildAuditEntry;
import com.blockforge.common.serverplay.BuildCooldown;
import com.blockforge.common.serverplay.CooldownPolicy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditRollbackPolicyTest {
    @Test
    void auditEntryFormatsAsEscapedJsonl() {
        BuildAuditEntry entry = new BuildAuditEntry(
                "audit-1",
                "job-1",
                "player-1",
                "Alex \"Builder\"",
                "station_complete",
                "tiny_platform",
                "minecraft:overworld",
                0,
                64,
                0,
                12,
                4,
                "builder_station",
                "completed",
                List.of("cache\nfallback"),
                200L,
                "2026-01-01T00:00:00Z"
        );

        String line = AuditJsonlFormatter.toJsonLine(entry);

        assertTrue(line.contains("\"auditId\":\"audit-1\""));
        assertTrue(line.contains("Alex \\\"Builder\\\""));
        assertTrue(line.contains("cache\\nfallback"));
    }

    @Test
    void rollbackRequiresPermissionAndSnapshot() {
        assertFalse(AdminRollbackPlanner.decide(false, true, true, false).allowed());
        assertFalse(AdminRollbackPlanner.decide(true, false, true, false).allowed());
        assertTrue(AdminRollbackPlanner.decide(true, true, false, false).allowed());
        assertFalse(AdminRollbackPlanner.decide(true, true, false, true).allowed());
    }

    @Test
    void cooldownPolicyReturnsFriendlyRemainingTime() {
        BuildCooldown cooldown = new BuildCooldown("player-1", "station_step", 100L, 5);

        assertFalse(CooldownPolicy.check(cooldown, 120L).allowed());
        assertTrue(CooldownPolicy.check(cooldown, 200L).allowed());
    }
}
