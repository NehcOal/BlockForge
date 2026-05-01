package com.blockforge.common.contracts;

import com.blockforge.common.blueprint.Blueprint;

import java.util.ArrayList;
import java.util.List;

public class ContractVerifier {
    private final BuildRequirementChecker requirementChecker;

    public ContractVerifier() {
        this(new BuildRequirementChecker());
    }

    public ContractVerifier(BuildRequirementChecker requirementChecker) {
        this.requirementChecker = requirementChecker;
    }

    public ContractVerificationResult verify(BuildContract contract, Blueprint blueprint) {
        List<String> failed = new ArrayList<>();
        List<String> passed = new ArrayList<>();
        if (contract.requiredBlueprintId() != null && !contract.requiredBlueprintId().isBlank() && blueprint != null && !contract.requiredBlueprintId().equals(blueprint.getId())) {
            failed.add("blueprint id does not match contract");
        } else {
            passed.add("blueprint id accepted");
        }
        failed.addAll(requirementChecker.failedChecks(blueprint, contract.requirements()));

        int completionPercent = estimateCompletion(contract, blueprint, failed);
        if (completionPercent < contract.requirements().requiredCompletionPercent()) {
            failed.add("completion percent below contract threshold");
        } else {
            passed.add("completion percent accepted");
        }
        boolean passedAll = failed.isEmpty();
        int matchedBlocks = blueprint == null ? 0 : blueprint.getBlockCount();
        int missingBlocks = passedAll ? 0 : Math.max(0, contract.requirements().minBlocks() - matchedBlocks);
        return new ContractVerificationResult(
                contract.contractId(),
                passedAll,
                completionPercent,
                matchedBlocks,
                missingBlocks,
                failed.size(),
                passed,
                failed,
                List.of("Alpha heuristic verification; world snapshot verification remains loader-dependent."),
                passedAll ? contract.rewards().reputation() : 0,
                passedAll ? contract.rewards().experience() : 0
        );
    }

    private int estimateCompletion(BuildContract contract, Blueprint blueprint, List<String> failedChecks) {
        if (blueprint == null) {
            return 0;
        }
        int rangeScore = blueprint.getBlockCount() >= contract.requirements().minBlocks() && blueprint.getBlockCount() <= contract.requirements().maxBlocks() ? 50 : 20;
        int checkPenalty = Math.min(50, failedChecks.size() * 10);
        return Math.max(0, Math.min(100, rangeScore + 50 - checkPenalty));
    }
}
