package com.blockforge.connector.serverplay;

import com.blockforge.common.serverplay.DiagnosticsExportResult;
import com.blockforge.common.serverplay.DiagnosticsJsonExporter;
import com.blockforge.common.serverplay.DiagnosticsSnapshot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DiagnosticsExportTest {
    @Test
    void exportsDiagnosticsJsonShape(@TempDir Path tempDir) throws Exception {
        DiagnosticsSnapshot snapshot = new DiagnosticsSnapshot(
                "4.2.0-beta.1",
                "NeoForge",
                "1.21.1",
                Map.of("stationMaxBlocksPerTick", "8"),
                3,
                1,
                1,
                1,
                2,
                1,
                4,
                1,
                0,
                3,
                true,
                false,
                List.of("manual regression pending")
        );

        DiagnosticsExportResult result = DiagnosticsJsonExporter.export(tempDir, snapshot, Instant.parse("2026-04-29T00:00:00Z"));

        assertTrue(result.written());
        String json = Files.readString(result.path());
        assertTrue(json.contains("\"version\":\"4.2.0-beta.1\""));
        assertTrue(json.contains("\"activeStations\":2"));
        assertTrue(json.contains("\"warningList\""));
    }
}
