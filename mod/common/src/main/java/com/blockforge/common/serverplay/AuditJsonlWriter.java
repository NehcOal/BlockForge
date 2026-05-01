package com.blockforge.common.serverplay;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.ZoneOffset;

public final class AuditJsonlWriter {
    private AuditJsonlWriter() {
    }

    public static Path dailyPath(Path auditDirectory, LocalDate date) {
        return auditDirectory.resolve("blockforge-audit-" + date + ".jsonl");
    }

    public static AuditJsonlWriteResult append(Path auditDirectory, BuildAuditEntry entry) {
        Path path = dailyPath(auditDirectory, LocalDate.now(ZoneOffset.UTC));
        try {
            Files.createDirectories(auditDirectory);
            Files.writeString(
                    path,
                    AuditJsonlFormatter.toJsonLine(entry) + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            return new AuditJsonlWriteResult(true, path, "");
        } catch (IOException | RuntimeException error) {
            return new AuditJsonlWriteResult(false, path, "Audit JSONL write failed: " + error.getMessage());
        }
    }
}
