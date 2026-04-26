package com.blockforge.common.selection;

import com.blockforge.common.rotation.BlueprintRotation;

import java.util.UUID;

public record PlayerSelection(
        UUID playerId,
        String selectedBlueprintId,
        int rotationDegrees,
        long lastBuildTimeMillis
) {
    public PlayerSelection {
        if (playerId == null) {
            throw new IllegalArgumentException("playerId is required");
        }

        if (selectedBlueprintId == null || selectedBlueprintId.isBlank()) {
            throw new IllegalArgumentException("selectedBlueprintId is required");
        }

        BlueprintRotationSelection.validate(rotationDegrees);
    }

    public BlueprintRotation rotation() {
        return BlueprintRotation.fromDegrees(Integer.toString(rotationDegrees));
    }

    public PlayerSelection withBlueprint(String blueprintId) {
        return new PlayerSelection(playerId, blueprintId, rotationDegrees, lastBuildTimeMillis);
    }

    public PlayerSelection withRotation(int degrees) {
        return new PlayerSelection(playerId, selectedBlueprintId, degrees, lastBuildTimeMillis);
    }

    public PlayerSelection withLastBuildTimeMillis(long millis) {
        return new PlayerSelection(playerId, selectedBlueprintId, rotationDegrees, millis);
    }
}
