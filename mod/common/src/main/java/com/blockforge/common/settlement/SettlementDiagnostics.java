package com.blockforge.common.settlement;

import com.blockforge.common.contracts.BuildContract;

import java.util.List;

public record SettlementDiagnostics(
        int settlements,
        int activeSettlements,
        int activeContracts,
        int completedContracts,
        List<String> warnings
) {
    public static SettlementDiagnostics summarize(List<Settlement> settlements, List<BuildContract> contracts) {
        List<Settlement> safeSettlements = settlements == null ? List.of() : settlements;
        List<BuildContract> safeContracts = contracts == null ? List.of() : contracts;
        int active = (int) safeSettlements.stream().filter(settlement -> settlement.status() == SettlementStatus.ACTIVE).count();
        int completed = safeSettlements.stream().mapToInt(Settlement::completedContracts).sum();
        return new SettlementDiagnostics(
                safeSettlements.size(),
                active,
                safeContracts.size(),
                completed,
                List.of("Minecraft manual regression pending", "Dedicated server smoke test pending")
        );
    }
}
