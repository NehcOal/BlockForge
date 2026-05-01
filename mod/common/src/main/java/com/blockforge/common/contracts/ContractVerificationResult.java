package com.blockforge.common.contracts;

import java.util.List;

public record ContractVerificationResult(
        String contractId,
        boolean passed,
        int completionPercent,
        int matchedBlocks,
        int missingBlocks,
        int wrongBlocks,
        List<String> passedChecks,
        List<String> failedChecks,
        List<String> warnings,
        int awardedReputation,
        int awardedExperience
) {
    public ContractVerificationResult {
        completionPercent = Math.max(0, Math.min(100, completionPercent));
        matchedBlocks = Math.max(0, matchedBlocks);
        missingBlocks = Math.max(0, missingBlocks);
        wrongBlocks = Math.max(0, wrongBlocks);
        passedChecks = passedChecks == null ? List.of() : List.copyOf(passedChecks);
        failedChecks = failedChecks == null ? List.of() : List.copyOf(failedChecks);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }
}
