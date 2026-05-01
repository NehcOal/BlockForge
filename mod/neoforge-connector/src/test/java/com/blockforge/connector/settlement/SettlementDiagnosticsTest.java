package com.blockforge.connector.settlement;

import com.blockforge.common.contracts.ContractTemplates;
import com.blockforge.common.settlement.Settlement;
import com.blockforge.common.settlement.SettlementDiagnostics;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SettlementDiagnosticsTest {
    @Test
    void summarizesSettlementAndContractCounts() {
        Settlement settlement = Settlement.create("s1", "Harbor", UUID.randomUUID(), "minecraft:overworld", 0, 64, 0, 0);

        SettlementDiagnostics diagnostics = SettlementDiagnostics.summarize(List.of(settlement), ContractTemplates.templates().subList(0, 2));

        assertEquals(1, diagnostics.settlements());
        assertEquals(2, diagnostics.activeContracts());
        assertFalse(diagnostics.warnings().isEmpty());
    }
}
