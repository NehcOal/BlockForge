package com.blockforge.common.material;

import com.blockforge.common.material.source.MaterialSourceRef;
import com.blockforge.common.material.source.MaterialSourceType;

public record ConsumedMaterialEntry(
        String itemId,
        int count,
        MaterialSourceRef source,
        String sourceId,
        MaterialSourceType sourceType
) {
    public ConsumedMaterialEntry(String itemId, int count) {
        this(itemId, count, null, null, MaterialSourceType.PLAYER_INVENTORY);
    }

    public ConsumedMaterialEntry(String itemId, int count, MaterialSourceRef source, String sourceId) {
        this(itemId, count, source, sourceId, source == null ? MaterialSourceType.PLAYER_INVENTORY : source.type());
    }

    public ConsumedMaterialEntry {
        itemId = itemId == null ? "" : itemId;
        count = Math.max(0, count);
        sourceId = sourceId == null && source != null ? source.id() : sourceId;
        sourceType = sourceType == null
                ? (source == null ? MaterialSourceType.PLAYER_INVENTORY : source.type())
                : sourceType;
    }
}
