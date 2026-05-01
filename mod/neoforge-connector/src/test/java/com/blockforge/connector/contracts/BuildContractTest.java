package com.blockforge.connector.contracts;

import com.blockforge.common.contracts.BuildContract;
import com.blockforge.common.contracts.ContractStatus;
import com.blockforge.common.contracts.ContractTemplates;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuildContractTest {
    @Test
    void acceptsAvailableContract() {
        UUID player = UUID.randomUUID();
        BuildContract contract = ContractTemplates.templates().get(0).accept(player, "settlement-1", 100);

        assertEquals(ContractStatus.ACCEPTED, contract.status());
        assertEquals(player, contract.acceptedByPlayerId());
        assertEquals("settlement-1", contract.settlementId());
    }

    @Test
    void rejectsAcceptingAcceptedContractAgain() {
        BuildContract accepted = ContractTemplates.templates().get(0).accept(UUID.randomUUID(), "settlement-1", 100);

        assertThrows(IllegalStateException.class, () -> accepted.accept(UUID.randomUUID(), "settlement-1", 120));
    }
}
