package com.blockforge.common.settlement.emergency;

import java.util.List;

public class EmergencyRepairVerifier {
    public EmergencyRepairResult verify(EmergencyRepairRequest request, int repairedBlocks, int remainingIssues, long gameTime) {
        if (request.expired(gameTime)) {
            return new EmergencyRepairResult(request.repairId(), false, repairedBlocks, remainingIssues, List.of("repair request expired"));
        }
        int totalKnownIssues = request.missingBlocks() + request.wrongBlocks();
        int completionPercent = totalKnownIssues == 0 ? 100 : Math.min(100, repairedBlocks * 100 / totalKnownIssues);
        boolean passed = completionPercent >= request.requiredCompletionPercent() && remainingIssues == 0;
        return new EmergencyRepairResult(request.repairId(), passed, repairedBlocks, remainingIssues, passed ? List.of() : List.of("repair completion below threshold"));
    }
}
