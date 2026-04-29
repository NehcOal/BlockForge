package com.blockforge.common.serverplay;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.StringJoiner;

public final class DiagnosticsJsonExporter {
    private DiagnosticsJsonExporter() {
    }

    public static String toJson(DiagnosticsSnapshot snapshot) {
        StringJoiner config = new StringJoiner(",", "{", "}");
        for (Map.Entry<String, String> entry : snapshot.configSummary().entrySet()) {
            config.add(field(entry.getKey(), entry.getValue()));
        }
        StringJoiner warnings = new StringJoiner(",", "[", "]");
        for (String warning : snapshot.warningList()) {
            warnings.add("\"" + escape(warning) + "\"");
        }
        return "{"
                + field("version", snapshot.version()) + ","
                + field("loader", snapshot.loader()) + ","
                + field("minecraftVersion", snapshot.minecraftVersion()) + ","
                + "\"configSummary\":" + config + ","
                + numberField("loadedBlueprints", snapshot.loadedBlueprints()) + ","
                + numberField("loadedPacks", snapshot.loadedPacks()) + ","
                + numberField("loadedSchematics", snapshot.loadedSchematics()) + ","
                + numberField("loadedLitematics", snapshot.loadedLitematics()) + ","
                + numberField("activeStations", snapshot.activeStations()) + ","
                + numberField("activeJobs", snapshot.activeJobs()) + ","
                + numberField("auditEntries", snapshot.auditEntries()) + ","
                + numberField("quotaDenials", snapshot.quotaDenials()) + ","
                + numberField("cooldownDenials", snapshot.cooldownDenials()) + ","
                + numberField("materialNetworkSources", snapshot.materialNetworkSources()) + ","
                + booleanField("protectionEnabled", snapshot.protectionEnabled()) + ","
                + booleanField("nearbyContainersEnabled", snapshot.nearbyContainersEnabled()) + ","
                + "\"warningList\":" + warnings
                + "}";
    }

    public static DiagnosticsExportResult export(Path diagnosticsDirectory, DiagnosticsSnapshot snapshot, Instant timestamp) {
        String safeTimestamp = timestamp.toString().replace(":", "").replace(".", "-");
        Path path = diagnosticsDirectory.resolve("blockforge-diagnostics-" + safeTimestamp + ".json");
        try {
            Files.createDirectories(diagnosticsDirectory);
            Files.writeString(path, toJson(snapshot), StandardCharsets.UTF_8);
            return new DiagnosticsExportResult(true, path, "");
        } catch (IOException | RuntimeException error) {
            return new DiagnosticsExportResult(false, path, "Diagnostics export failed: " + error.getMessage());
        }
    }

    private static String field(String key, String value) {
        return "\"" + escape(key) + "\":\"" + escape(value) + "\"";
    }

    private static String numberField(String key, long value) {
        return "\"" + key + "\":" + value;
    }

    private static String booleanField(String key, boolean value) {
        return "\"" + key + "\":" + value;
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }
}
