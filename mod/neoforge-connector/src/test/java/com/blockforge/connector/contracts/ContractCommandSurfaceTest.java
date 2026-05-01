package com.blockforge.connector.contracts;

import com.blockforge.common.contracts.ContractCommandSurface;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ContractCommandSurfaceTest {
    @Test
    void exposesSettlementContractAndArchitectCommandModels() {
        assertTrue(ContractCommandSurface.settlementCommands().contains("create"));
        assertTrue(ContractCommandSurface.contractCommands().contains("verify"));
        assertTrue(ContractCommandSurface.architectCommands().contains("reputation"));
        assertTrue(ContractCommandSurface.eventCommands().contains("refresh"));
        assertTrue(ContractCommandSurface.projectCommands().contains("activate"));
        assertTrue(ContractCommandSurface.emergencyCommands().contains("repair"));
    }
}
