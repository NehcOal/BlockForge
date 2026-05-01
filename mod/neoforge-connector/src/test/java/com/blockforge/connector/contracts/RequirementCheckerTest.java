package com.blockforge.connector.contracts;

import com.blockforge.common.contracts.BuildRequirementChecker;
import com.blockforge.common.contracts.ContractTemplates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RequirementCheckerTest {
    @Test
    void reportsMissingRequiredBlocks() {
        var contract = ContractTemplates.templates().stream()
                .filter(template -> template.contractId().equals("storage_shed"))
                .findFirst()
                .orElseThrow();

        var failed = new BuildRequirementChecker().failedChecks(ContractVerifierTest.cottageBlueprint(), contract.requirements());

        assertTrue(failed.stream().anyMatch(check -> check.contains("minecraft:chest")));
    }
}
