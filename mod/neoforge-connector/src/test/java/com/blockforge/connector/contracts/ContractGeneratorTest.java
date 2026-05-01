package com.blockforge.connector.contracts;

import com.blockforge.common.contracts.ContractDifficulty;
import com.blockforge.common.contracts.ContractGenerator;
import com.blockforge.common.contracts.ContractTemplates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContractGeneratorTest {
    @Test
    void exposesAtLeastTwelveTemplates() {
        assertTrue(ContractTemplates.templates().size() >= 12);
    }

    @Test
    void filtersContractsBySettlementLevel() {
        var contracts = new ContractGenerator().generateForSettlementLevel(1, 240);

        assertTrue(contracts.stream().allMatch(contract -> contract.difficulty().ordinal() <= ContractDifficulty.EASY.ordinal()));
        assertTrue(contracts.stream().allMatch(contract -> contract.createdAtGameTime() == 240));
    }

    @Test
    void highLevelUnlocksMasterContracts() {
        var contracts = new ContractGenerator().generateForSettlementLevel(5, 240);

        assertEquals(ContractTemplates.templates().size(), contracts.size());
    }
}
