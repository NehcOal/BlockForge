package com.blockforge.common.gui;

import java.util.List;

public record BlueprintSummary(
        String id,
        String name,
        int schemaVersion,
        int width,
        int height,
        int depth,
        int blockCount,
        boolean hasBlockStates,
        String sourceType,
        String sourceId,
        int warningCount,
        List<String> tags
) {
    public BlueprintSummary {
        sourceType = sourceType == null || sourceType.isBlank() ? "loose" : sourceType;
        sourceId = sourceId == null ? "" : sourceId;
        tags = tags == null ? List.of() : List.copyOf(tags);
    }

    public String sizeLabel() {
        return width + " x " + height + " x " + depth;
    }
}
