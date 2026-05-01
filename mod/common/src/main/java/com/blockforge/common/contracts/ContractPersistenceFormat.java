package com.blockforge.common.contracts;

public final class ContractPersistenceFormat {
    private ContractPersistenceFormat() {
    }

    public static String summaryLine(BuildContract contract) {
        return contract.contractId() + "|" + contract.status() + "|" + contract.type() + "|" + contract.difficulty();
    }
}
