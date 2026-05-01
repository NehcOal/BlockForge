package com.blockforge.common.serverplay;

import java.util.StringJoiner;

public final class AuditJsonlFormatter {
    private AuditJsonlFormatter() {
    }

    public static String toJsonLine(BuildAuditEntry entry) {
        if (entry == null) {
            return "{}";
        }
        StringJoiner warnings = new StringJoiner(",", "[", "]");
        for (String warning : entry.warnings()) {
            warnings.add("\"" + escape(warning) + "\"");
        }
        return "{"
                + field("auditId", entry.auditId()) + ","
                + field("jobId", entry.jobId()) + ","
                + field("playerId", entry.playerId()) + ","
                + field("playerName", entry.playerName()) + ","
                + field("action", entry.action()) + ","
                + field("blueprintId", entry.blueprintId()) + ","
                + field("dimensionId", entry.dimensionId()) + ","
                + numberField("baseX", entry.baseX()) + ","
                + numberField("baseY", entry.baseY()) + ","
                + numberField("baseZ", entry.baseZ()) + ","
                + numberField("placedBlocks", entry.placedBlocks()) + ","
                + numberField("consumedItems", entry.consumedItems()) + ","
                + field("sourceType", entry.sourceType()) + ","
                + field("status", entry.status()) + ","
                + "\"warnings\":" + warnings + ","
                + numberField("gameTime", entry.gameTime()) + ","
                + field("createdAtIso", entry.createdAtIso())
                + "}";
    }

    private static String field(String key, String value) {
        return "\"" + key + "\":\"" + escape(value) + "\"";
    }

    private static String numberField(String key, long value) {
        return "\"" + key + "\":" + value;
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
