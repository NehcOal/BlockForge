package com.blockforge.common.contracts;

public record ContractRewardItem(String itemId, int count) {
    public ContractRewardItem {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId is required");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
    }
}
