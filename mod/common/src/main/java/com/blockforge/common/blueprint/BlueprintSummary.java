package com.blockforge.common.blueprint;

public record BlueprintSummary(
        String id,
        String name,
        int schemaVersion,
        int width,
        int height,
        int depth,
        int blockCount,
        boolean hasBlockStates
) {
    public String sizeLabel() {
        return width + " x " + height + " x " + depth;
    }
}
