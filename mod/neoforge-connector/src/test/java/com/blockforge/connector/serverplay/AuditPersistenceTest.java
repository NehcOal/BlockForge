package com.blockforge.connector.serverplay;

import com.blockforge.common.serverplay.AuditJsonlWriteResult;
import com.blockforge.common.serverplay.AuditJsonlWriter;
import com.blockforge.common.serverplay.BuildAuditEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditPersistenceTest {
    @Test
    void writesAuditJsonlWithoutThrowing(@TempDir Path tempDir) throws Exception {
        BuildAuditEntry entry = new BuildAuditEntry(
                "audit-1",
                "job-1",
                "player-1",
                "Dev",
                "station complete",
                "tiny",
                "minecraft:overworld",
                1,
                64,
                2,
                4,
                4,
                "builder_station",
                "completed",
                List.of("ok"),
                20L,
                "2026-04-29T00:00:00Z"
        );

        AuditJsonlWriteResult result = AuditJsonlWriter.append(tempDir, entry);

        assertTrue(result.written());
        assertTrue(Files.readString(result.path()).contains("\"action\":\"station complete\""));
    }
}
