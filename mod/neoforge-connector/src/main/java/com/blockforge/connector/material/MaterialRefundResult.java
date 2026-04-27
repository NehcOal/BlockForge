package com.blockforge.connector.material;

import java.util.List;

public record MaterialRefundResult(
        int refundedItems,
        int refundedToPlayer,
        int refundedToContainers,
        int droppedItems,
        List<String> warnings
) {
    public MaterialRefundResult(int refundedItems, int droppedItems, List<String> warnings) {
        this(refundedItems, refundedItems, 0, droppedItems, warnings);
    }

    public static MaterialRefundResult empty() {
        return new MaterialRefundResult(0, 0, 0, 0, List.of());
    }

    public MaterialRefundResult {
        refundedItems = Math.max(0, refundedItems);
        refundedToPlayer = Math.max(0, refundedToPlayer);
        refundedToContainers = Math.max(0, refundedToContainers);
        droppedItems = Math.max(0, droppedItems);
        warnings = List.copyOf(warnings);
    }
}
