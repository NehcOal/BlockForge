package com.blockforge.common.material;

import java.util.List;

public record MaterialRefundResult(
        int refundedItems,
        int droppedItems,
        List<String> warnings
) {
    public MaterialRefundResult {
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }

    public static MaterialRefundResult none() {
        return new MaterialRefundResult(0, 0, List.of());
    }
}
