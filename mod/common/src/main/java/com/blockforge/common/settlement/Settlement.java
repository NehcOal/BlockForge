package com.blockforge.common.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record Settlement(
        String settlementId,
        String name,
        UUID ownerPlayerId,
        List<UUID> memberPlayerIds,
        String dimensionId,
        int coreX,
        int coreY,
        int coreZ,
        int level,
        int reputation,
        int completedContracts,
        List<String> activeContractIds,
        List<String> completedContractIds,
        long createdAtGameTime,
        long updatedAtGameTime,
        SettlementStatus status
) {
    public Settlement {
        if (settlementId == null || settlementId.isBlank()) {
            throw new IllegalArgumentException("settlementId is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (ownerPlayerId == null) {
            throw new IllegalArgumentException("ownerPlayerId is required");
        }
        memberPlayerIds = memberPlayerIds == null ? List.of() : List.copyOf(memberPlayerIds);
        activeContractIds = activeContractIds == null ? List.of() : List.copyOf(activeContractIds);
        completedContractIds = completedContractIds == null ? List.of() : List.copyOf(completedContractIds);
        status = status == null ? SettlementStatus.ACTIVE : status;
        level = Math.max(1, level);
        reputation = Math.max(0, reputation);
        completedContracts = Math.max(0, completedContracts);
    }

    public static Settlement create(String settlementId, String name, UUID ownerPlayerId, String dimensionId, int x, int y, int z, long gameTime) {
        return new Settlement(settlementId, name, ownerPlayerId, List.of(ownerPlayerId), dimensionId, x, y, z, 1, 0, 0, List.of(), List.of(), gameTime, gameTime, SettlementStatus.ACTIVE);
    }

    public boolean hasMember(UUID playerId) {
        return ownerPlayerId.equals(playerId) || memberPlayerIds.contains(playerId);
    }

    public Settlement addMember(UUID playerId, long gameTime) {
        if (hasMember(playerId)) {
            return this;
        }
        List<UUID> members = new ArrayList<>(memberPlayerIds);
        members.add(playerId);
        return withMembers(members, gameTime);
    }

    public Settlement removeMember(UUID playerId, long gameTime) {
        if (ownerPlayerId.equals(playerId)) {
            throw new IllegalArgumentException("owner cannot be removed from settlement");
        }
        List<UUID> members = new ArrayList<>(memberPlayerIds);
        members.remove(playerId);
        return withMembers(members, gameTime);
    }

    public Settlement acceptContract(String contractId, long gameTime) {
        if (contractId == null || contractId.isBlank() || activeContractIds.contains(contractId)) {
            return this;
        }
        List<String> active = new ArrayList<>(activeContractIds);
        active.add(contractId);
        return new Settlement(settlementId, name, ownerPlayerId, memberPlayerIds, dimensionId, coreX, coreY, coreZ, level, reputation, completedContracts, active, completedContractIds, createdAtGameTime, gameTime, status);
    }

    public Settlement completeContract(String contractId, int reputationAward, long gameTime) {
        List<String> active = new ArrayList<>(activeContractIds);
        active.remove(contractId);
        List<String> completed = new ArrayList<>(completedContractIds);
        if (completed.contains(contractId)) {
            return new Settlement(settlementId, name, ownerPlayerId, memberPlayerIds, dimensionId, coreX, coreY, coreZ, level, reputation, completedContracts, active, completed, createdAtGameTime, gameTime, status);
        }
        completed.add(contractId);
        return new Settlement(settlementId, name, ownerPlayerId, memberPlayerIds, dimensionId, coreX, coreY, coreZ, level, reputation + Math.max(0, reputationAward), completedContracts + 1, active, completed, createdAtGameTime, gameTime, status);
    }

    public Settlement withLevel(int newLevel, long gameTime) {
        return new Settlement(settlementId, name, ownerPlayerId, memberPlayerIds, dimensionId, coreX, coreY, coreZ, newLevel, reputation, completedContracts, activeContractIds, completedContractIds, createdAtGameTime, gameTime, status);
    }

    private Settlement withMembers(List<UUID> members, long gameTime) {
        return new Settlement(settlementId, name, ownerPlayerId, members, dimensionId, coreX, coreY, coreZ, level, reputation, completedContracts, activeContractIds, completedContractIds, createdAtGameTime, gameTime, status);
    }
}
