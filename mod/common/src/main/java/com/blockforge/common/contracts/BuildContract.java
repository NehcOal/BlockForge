package com.blockforge.common.contracts;

import java.util.List;
import java.util.UUID;

public record BuildContract(
        String contractId,
        String title,
        String description,
        ContractType type,
        ContractDifficulty difficulty,
        String requiredBlueprintId,
        List<String> allowedBlueprintTags,
        BuildContractRequirements requirements,
        BuildContractRewards rewards,
        ContractStatus status,
        UUID acceptedByPlayerId,
        String settlementId,
        long createdAtGameTime,
        long acceptedAtGameTime,
        long expiresAtGameTime,
        long completedAtGameTime
) {
    public BuildContract {
        if (contractId == null || contractId.isBlank()) {
            throw new IllegalArgumentException("contractId is required");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        type = type == null ? ContractType.CUSTOM_BLUEPRINT : type;
        difficulty = difficulty == null ? ContractDifficulty.EASY : difficulty;
        allowedBlueprintTags = allowedBlueprintTags == null ? List.of() : List.copyOf(allowedBlueprintTags);
        if (requirements == null) {
            throw new IllegalArgumentException("requirements are required");
        }
        if (rewards == null) {
            throw new IllegalArgumentException("rewards are required");
        }
        status = status == null ? ContractStatus.AVAILABLE : status;
        requiredBlueprintId = requiredBlueprintId == null ? "" : requiredBlueprintId;
        settlementId = settlementId == null ? "" : settlementId;
    }

    public BuildContract accept(UUID playerId, String settlementId, long gameTime) {
        if (status != ContractStatus.AVAILABLE) {
            throw new IllegalStateException("contract is not available");
        }
        return new BuildContract(contractId, title, description, type, difficulty, requiredBlueprintId, allowedBlueprintTags, requirements, rewards, ContractStatus.ACCEPTED, playerId, settlementId, createdAtGameTime, gameTime, expiresAtGameTime, 0);
    }

    public BuildContract readyToVerify() {
        return new BuildContract(contractId, title, description, type, difficulty, requiredBlueprintId, allowedBlueprintTags, requirements, rewards, ContractStatus.READY_TO_VERIFY, acceptedByPlayerId, settlementId, createdAtGameTime, acceptedAtGameTime, expiresAtGameTime, completedAtGameTime);
    }

    public BuildContract complete(long gameTime) {
        return new BuildContract(contractId, title, description, type, difficulty, requiredBlueprintId, allowedBlueprintTags, requirements, rewards, ContractStatus.COMPLETED, acceptedByPlayerId, settlementId, createdAtGameTime, acceptedAtGameTime, expiresAtGameTime, gameTime);
    }
}
