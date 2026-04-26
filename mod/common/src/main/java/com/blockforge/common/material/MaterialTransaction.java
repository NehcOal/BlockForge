package com.blockforge.common.material;

import java.util.List;
import java.util.UUID;

public record MaterialTransaction(
        UUID playerId,
        String blueprintId,
        List<ConsumedMaterialEntry> consumedItems,
        long createdAtGameTime,
        boolean creativeBypass
) {
    public MaterialTransaction {
        consumedItems = consumedItems == null ? List.of() : List.copyOf(consumedItems);
    }

    public static MaterialTransaction creative(UUID playerId, String blueprintId, long createdAtGameTime) {
        return new MaterialTransaction(playerId, blueprintId, List.of(), createdAtGameTime, true);
    }

    public int totalConsumedItems() {
        return consumedItems.stream().mapToInt(ConsumedMaterialEntry::count).sum();
    }

    public boolean hasConsumedItems() {
        return totalConsumedItems() > 0;
    }
}
