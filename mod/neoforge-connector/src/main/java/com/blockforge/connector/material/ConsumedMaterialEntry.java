package com.blockforge.connector.material;

import com.blockforge.common.material.source.MaterialSourceRef;

public record ConsumedMaterialEntry(
        String itemId,
        int count,
        MaterialSourceRef source,
        String sourceId
) {
    public ConsumedMaterialEntry(String itemId, int count) {
        this(itemId, count, null, "");
    }

    public ConsumedMaterialEntry {
        itemId = itemId == null ? "" : itemId;
        count = Math.max(0, count);
        sourceId = sourceId == null ? "" : sourceId;
    }
}
