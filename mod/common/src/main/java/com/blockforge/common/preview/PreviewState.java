package com.blockforge.common.preview;

import com.blockforge.common.selection.BlueprintRotationSelection;

public record PreviewState(
        String blueprintId,
        String blueprintName,
        int width,
        int height,
        int depth,
        int rotationDegrees,
        boolean visible,
        boolean valid
) {
    public PreviewState {
        blueprintId = blueprintId == null ? "" : blueprintId;
        blueprintName = blueprintName == null ? "" : blueprintName;
        BlueprintRotationSelection.validate(rotationDegrees);
        if (width < 0 || height < 0 || depth < 0) {
            throw new IllegalArgumentException("Preview dimensions cannot be negative.");
        }
    }

    public PreviewBounds bounds() {
        return PreviewBounds.of(width, height, depth, rotationDegrees);
    }
}
