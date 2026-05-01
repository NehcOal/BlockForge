package com.blockforge.connector.serverplay;

import com.blockforge.common.serverplay.LoaderIntegrationPaths;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoaderIntegrationPathsTest {
    @Test
    void createsStableAuditAndDiagnosticsPaths() {
        LoaderIntegrationPaths paths = new LoaderIntegrationPaths(Path.of("config", "blockforge"));
        Instant timestamp = Instant.parse("2026-04-29T08:00:00Z");

        assertTrue(paths.auditJsonlPath(timestamp).toString().endsWith("config\\blockforge\\audit\\blockforge-audit-2026-04-29.jsonl")
                || paths.auditJsonlPath(timestamp).toString().endsWith("config/blockforge/audit/blockforge-audit-2026-04-29.jsonl"));
        assertTrue(paths.diagnosticsJsonPath(timestamp).getFileName().toString().startsWith("blockforge-diagnostics-2026-04-29T080000Z"));
    }
}
