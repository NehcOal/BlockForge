package com.blockforge.common.settlement.event;

import java.util.List;

public record SettlementEventDiagnostics(
        int activeEvents,
        int activeProjects,
        int emergencyRepairs,
        int averageStability,
        List<String> warnings
) {
    public SettlementEventDiagnostics {
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }
}
