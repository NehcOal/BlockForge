package com.blockforge.common.progression;

import com.blockforge.common.contracts.BuildContract;
import com.blockforge.common.contracts.ContractVerificationResult;

import java.util.ArrayList;

public class RewardService {
    private final ArchitectProgressionService progressionService;

    public RewardService() {
        this(new ArchitectProgressionService());
    }

    public RewardService(ArchitectProgressionService progressionService) {
        this.progressionService = progressionService;
    }

    public ArchitectProfile applyReward(ArchitectProfile profile, BuildContract contract, ContractVerificationResult verification) {
        if (!verification.passed()) {
            return profile;
        }
        ArrayList<String> completed = new ArrayList<>(profile.completedContractIds());
        if (completed.contains(contract.contractId())) {
            return profile;
        }
        completed.add(contract.contractId());
        ArchitectProfile rewarded = new ArchitectProfile(
                profile.playerId(),
                profile.reputation() + verification.awardedReputation(),
                profile.experience() + verification.awardedExperience(),
                profile.level(),
                profile.unlockedFeatures(),
                completed
        );
        return progressionService.recalculate(rewarded);
    }
}
