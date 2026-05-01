package com.blockforge.common.serverplay;

import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public record LoaderIntegrationPaths(
        Path configRoot
) {
    public LoaderIntegrationPaths {
        if (configRoot == null) {
            configRoot = Path.of("config", "blockforge");
        }
    }

    public Path auditDirectory() {
        return configRoot.resolve("audit");
    }

    public Path diagnosticsDirectory() {
        return configRoot.resolve("diagnostics");
    }

    public Path auditJsonlPath(Instant timestamp) {
        Instant resolved = timestamp == null ? Instant.EPOCH : timestamp;
        LocalDate date = resolved.atZone(ZoneOffset.UTC).toLocalDate();
        return auditDirectory().resolve("blockforge-audit-" + date + ".jsonl");
    }

    public Path diagnosticsJsonPath(Instant timestamp) {
        Instant resolved = timestamp == null ? Instant.EPOCH : timestamp;
        String safeTimestamp = resolved.toString().replace(":", "").replace(".", "-");
        return diagnosticsDirectory().resolve("blockforge-diagnostics-" + safeTimestamp + ".json");
    }
}
