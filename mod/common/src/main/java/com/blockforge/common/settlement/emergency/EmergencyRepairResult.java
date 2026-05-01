package com.blockforge.common.settlement.emergency;

import java.util.List;

public record EmergencyRepairResult(
        String repairId,
        boolean passed,
        int repairedBlocks,
        int remainingIssues,
        List<String> warnings
) {
    public EmergencyRepairResult {
        if (repairId == null || repairId.isBlank()) {
            throw new IllegalArgumentException("repairId is required");
        }
        repairedBlocks = Math.max(0, repairedBlocks);
        remainingIssues = Math.max(0, remainingIssues);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }
}
