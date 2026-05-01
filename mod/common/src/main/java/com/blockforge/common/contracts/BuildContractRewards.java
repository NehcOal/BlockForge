package com.blockforge.common.contracts;

import java.util.List;

public record BuildContractRewards(
        int reputation,
        int experience,
        List<String> unlockFeatureIds,
        List<ContractRewardItem> items,
        String rewardBlueprintPackId
) {
    public BuildContractRewards {
        reputation = Math.max(0, reputation);
        experience = Math.max(0, experience);
        unlockFeatureIds = unlockFeatureIds == null ? List.of() : List.copyOf(unlockFeatureIds);
        items = items == null ? List.of() : List.copyOf(items);
        rewardBlueprintPackId = rewardBlueprintPackId == null ? "" : rewardBlueprintPackId;
    }
}
