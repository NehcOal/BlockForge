package com.blockforge.connector.material;

import com.blockforge.common.material.source.MaterialSourceType;

import java.util.List;
import java.util.UUID;

public record MaterialTransaction(
        UUID playerId,
        String blueprintId,
        List<ConsumedMaterialEntry> consumedItems,
        long createdAtGameTime,
        boolean creativeBypass,
        MaterialSourceType sourceType,
        boolean includesNearbyContainers
) {
    public MaterialTransaction(
            UUID playerId,
            String blueprintId,
            List<ConsumedMaterialEntry> consumedItems,
            long createdAtGameTime,
            boolean creativeBypass
    ) {
        this(playerId, blueprintId, consumedItems, createdAtGameTime, creativeBypass, MaterialSourceType.PLAYER_INVENTORY, false);
    }

    public MaterialTransaction {
        consumedItems = List.copyOf(consumedItems);
        sourceType = sourceType == null ? MaterialSourceType.PLAYER_INVENTORY : sourceType;
    }

    public static MaterialTransaction creative(UUID playerId, String blueprintId, long createdAtGameTime) {
        return new MaterialTransaction(playerId, blueprintId, List.of(), createdAtGameTime, true, MaterialSourceType.PLAYER_INVENTORY, false);
    }

    public int totalConsumedItems() {
        return consumedItems.stream().mapToInt(ConsumedMaterialEntry::count).sum();
    }

    public boolean hasConsumedItems() {
        return totalConsumedItems() > 0;
    }
}
