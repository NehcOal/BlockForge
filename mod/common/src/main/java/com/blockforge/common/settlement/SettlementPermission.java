package com.blockforge.common.settlement;

import java.util.UUID;

public record SettlementPermission(
        UUID playerId,
        String settlementId,
        boolean canAcceptContracts,
        boolean canSubmitContracts,
        boolean canUseStations,
        boolean canManageMembers,
        boolean canClaimRewards
) {
    public static SettlementPermission owner(UUID playerId, String settlementId) {
        return new SettlementPermission(playerId, settlementId, true, true, true, true, true);
    }

    public static SettlementPermission member(UUID playerId, String settlementId) {
        return new SettlementPermission(playerId, settlementId, true, true, true, false, true);
    }
}
