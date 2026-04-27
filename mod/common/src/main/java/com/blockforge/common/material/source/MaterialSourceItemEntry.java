package com.blockforge.common.material.source;

public record MaterialSourceItemEntry(
        String itemId,
        int required,
        int available,
        int reserved,
        int consumed,
        MaterialSourceRef source
) {
    public MaterialSourceItemEntry {
        itemId = itemId == null ? "" : itemId;
        required = Math.max(0, required);
        available = Math.max(0, available);
        reserved = Math.max(0, reserved);
        consumed = Math.max(0, consumed);
    }
}
