package com.blockforge.common.contracts;

import java.util.Comparator;
import java.util.List;

public class ContractGenerator {
    public List<BuildContract> generateForSettlementLevel(int settlementLevel, long gameTime) {
        ContractDifficulty maxDifficulty = ContractDifficultyScaler.difficultyForSettlementLevel(settlementLevel);
        return ContractTemplates.templates().stream()
                .filter(contract -> contract.difficulty().ordinal() <= maxDifficulty.ordinal())
                .sorted(Comparator.comparing(BuildContract::contractId))
                .map(contract -> withCreatedTime(contract, gameTime))
                .toList();
    }

    private BuildContract withCreatedTime(BuildContract contract, long gameTime) {
        return new BuildContract(
                contract.contractId(),
                contract.title(),
                contract.description(),
                contract.type(),
                contract.difficulty(),
                contract.requiredBlueprintId(),
                contract.allowedBlueprintTags(),
                contract.requirements(),
                contract.rewards(),
                contract.status(),
                contract.acceptedByPlayerId(),
                contract.settlementId(),
                gameTime,
                contract.acceptedAtGameTime(),
                gameTime + 24000L,
                contract.completedAtGameTime()
        );
    }
}
