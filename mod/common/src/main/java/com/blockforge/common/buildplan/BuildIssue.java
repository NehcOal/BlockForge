package com.blockforge.common.buildplan;

public record BuildIssue(
        String type,
        String severity,
        int x,
        int y,
        int z,
        String message,
        String suggestion
) {
    public BuildIssue {
        type = type == null ? "unknown" : type;
        severity = severity == null ? "warning" : severity;
        message = message == null ? "" : message;
        suggestion = suggestion == null ? "" : suggestion;
    }
}
