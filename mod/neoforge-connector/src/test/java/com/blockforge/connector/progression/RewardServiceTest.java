package com.blockforge.connector.progression;

import com.blockforge.common.contracts.ContractTemplates;
import com.blockforge.common.contracts.ContractVerifier;
import com.blockforge.common.progression.ArchitectProfile;
import com.blockforge.common.progression.RewardService;
import com.blockforge.connector.contracts.ContractVerifierTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RewardServiceTest {
    @Test
    void completedContractGrantsReputationAndExperience() {
        var contract = ContractTemplates.templates().stream()
                .filter(template -> template.contractId().equals("starter_cottage"))
                .findFirst()
                .orElseThrow();
        var verification = new ContractVerifier().verify(contract, ContractVerifierTest.cottageBlueprint());
        var profile = ArchitectProfile.create(UUID.randomUUID());

        var rewarded = new RewardService().applyReward(profile, contract, verification);

        assertTrue(rewarded.reputation() > profile.reputation());
        assertTrue(rewarded.experience() > profile.experience());
        assertTrue(rewarded.completedContractIds().contains(contract.contractId()));
    }

    @Test
    void completedContractDoesNotGrantRewardAgain() {
        var contract = ContractTemplates.templates().stream()
                .filter(template -> template.contractId().equals("starter_cottage"))
                .findFirst()
                .orElseThrow();
        var verification = new ContractVerifier().verify(contract, ContractVerifierTest.cottageBlueprint());
        var service = new RewardService();
        var profile = service.applyReward(ArchitectProfile.create(UUID.randomUUID()), contract, verification);

        var rewardedAgain = service.applyReward(profile, contract, verification);

        assertEquals(profile.reputation(), rewardedAgain.reputation());
        assertEquals(profile.experience(), rewardedAgain.experience());
        assertEquals(profile.completedContractIds(), rewardedAgain.completedContractIds());
    }
}
