package com.blockforge.common.gameplay;

import java.util.UUID;

public record BuilderWandState(
        UUID playerId,
        String selectedBlueprintId,
        int rotationDegrees,
        boolean mirroredX,
        boolean mirroredZ,
        int offsetX,
        int offsetY,
        int offsetZ,
        BuilderWandMode mode,
        String anchorId,
        long lastUsedGameTime
) {
    public BuilderWandState {
        selectedBlueprintId = selectedBlueprintId == null ? "" : selectedBlueprintId;
        mode = mode == null ? BuilderWandMode.BUILD : mode;
        anchorId = anchorId == null ? "" : anchorId;
        rotationDegrees = normalizeRotation(rotationDegrees);
    }

    public static BuilderWandState defaults(UUID playerId) {
        return new BuilderWandState(playerId, "", 0, false, false, 0, 0, 0, BuilderWandMode.BUILD, "", 0L);
    }

    public BuilderWandState withMode(BuilderWandMode nextMode, long gameTime) {
        return new BuilderWandState(playerId, selectedBlueprintId, rotationDegrees, mirroredX, mirroredZ, offsetX, offsetY, offsetZ, nextMode, anchorId, gameTime);
    }

    public BuilderWandState withOffset(int x, int y, int z) {
        return new BuilderWandState(playerId, selectedBlueprintId, rotationDegrees, mirroredX, mirroredZ, x, y, z, mode, anchorId, lastUsedGameTime);
    }

    public BuilderWandState withOffset(int x, int y, int z, long gameTime) {
        return new BuilderWandState(playerId, selectedBlueprintId, rotationDegrees, mirroredX, mirroredZ, x, y, z, mode, anchorId, gameTime);
    }

    public BuilderWandState withMirror(boolean x, boolean z) {
        return new BuilderWandState(playerId, selectedBlueprintId, rotationDegrees, x, z, offsetX, offsetY, offsetZ, mode, anchorId, lastUsedGameTime);
    }

    public BuilderWandState withMirror(boolean x, boolean z, long gameTime) {
        return new BuilderWandState(playerId, selectedBlueprintId, rotationDegrees, x, z, offsetX, offsetY, offsetZ, mode, anchorId, gameTime);
    }

    public BuilderWandState withAnchor(String nextAnchorId) {
        return new BuilderWandState(playerId, selectedBlueprintId, rotationDegrees, mirroredX, mirroredZ, offsetX, offsetY, offsetZ, mode, nextAnchorId, lastUsedGameTime);
    }

    public BuilderWandState withAnchor(String nextAnchorId, long gameTime) {
        return new BuilderWandState(playerId, selectedBlueprintId, rotationDegrees, mirroredX, mirroredZ, offsetX, offsetY, offsetZ, mode, nextAnchorId, gameTime);
    }

    public PlacementOptions toPlacementOptions() {
        return new PlacementOptions(false, true, true, true, true, false, mirroredX, mirroredZ, offsetX, offsetY, offsetZ);
    }

    private static int normalizeRotation(int value) {
        int normalized = value % 360;
        return normalized < 0 ? normalized + 360 : normalized;
    }
}
