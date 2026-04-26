package com.blockforge.common.selection;

public record SelectionRequest(String blueprintId, int rotationDegrees) {
    public SelectionRequest {
        if (blueprintId == null || blueprintId.isBlank()) {
            throw new IllegalArgumentException("blueprintId is required");
        }

        BlueprintRotationSelection.validate(rotationDegrees);
    }
}
