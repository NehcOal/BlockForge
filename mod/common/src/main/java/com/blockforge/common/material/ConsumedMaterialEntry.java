package com.blockforge.common.material;

import com.blockforge.common.material.source.MaterialSourceRef;

public record ConsumedMaterialEntry(
        String itemId,
        int count,
        MaterialSourceRef source,
        String sourceId
) {
    public ConsumedMaterialEntry(String itemId, int count) {
        this(itemId, count, null, null);
    }

    public ConsumedMaterialEntry {
        itemId = itemId == null ? "" : itemId;
        count = Math.max(0, count);
        sourceId = sourceId == null && source != null ? source.id() : sourceId;
    }
}
