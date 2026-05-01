package com.blockforge.common.contracts;

public final class ContractDifficultyScaler {
    private ContractDifficultyScaler() {
    }

    public static int reputationMultiplier(ContractDifficulty difficulty) {
        return switch (difficulty) {
            case EASY -> 1;
            case NORMAL -> 2;
            case HARD -> 3;
            case MASTER -> 5;
        };
    }

    public static ContractDifficulty difficultyForSettlementLevel(int settlementLevel) {
        if (settlementLevel >= 5) {
            return ContractDifficulty.MASTER;
        }
        if (settlementLevel >= 3) {
            return ContractDifficulty.HARD;
        }
        if (settlementLevel >= 2) {
            return ContractDifficulty.NORMAL;
        }
        return ContractDifficulty.EASY;
    }
}
