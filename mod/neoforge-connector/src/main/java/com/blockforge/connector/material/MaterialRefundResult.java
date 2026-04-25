package com.blockforge.connector.material;

import java.util.List;

public record MaterialRefundResult(
        int refundedItems,
        int droppedItems,
        List<String> warnings
) {
    public static MaterialRefundResult empty() {
        return new MaterialRefundResult(0, 0, List.of());
    }

    public MaterialRefundResult {
        warnings = List.copyOf(warnings);
    }
}
