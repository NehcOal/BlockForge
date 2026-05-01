package com.blockforge.connector.contracts;

import com.blockforge.common.contracts.ContractPersistenceFormat;
import com.blockforge.common.contracts.ContractTemplates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ContractPersistenceFormatTest {
    @Test
    void writesStableSummaryLine() {
        String line = ContractPersistenceFormat.summaryLine(ContractTemplates.templates().get(0));

        assertTrue(line.contains("starter_cottage"));
        assertTrue(line.contains("AVAILABLE"));
    }
}
