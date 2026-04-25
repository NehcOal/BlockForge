package com.blockforge.connector.client.preview;

import com.blockforge.connector.network.payload.SelectedBlueprintPayload;
import net.minecraft.core.BlockPos;

public final class ClientPreviewState {
    private static SelectedBlueprint selectedBlueprint;
    private static PreviewTarget previewTarget;

    private ClientPreviewState() {
    }

    public static void apply(SelectedBlueprintPayload payload) {
        selectedBlueprint = new SelectedBlueprint(
                payload.blueprintId(),
                payload.blueprintName(),
                payload.width(),
                payload.height(),
                payload.depth(),
                payload.rotation()
        );
        previewTarget = null;
    }

    public static void clearSelection() {
        selectedBlueprint = null;
        previewTarget = null;
    }

    public static boolean hasSelection() {
        return selectedBlueprint != null;
    }

    public static SelectedBlueprint selectedBlueprint() {
        return selectedBlueprint;
    }

    public static PreviewTarget previewTarget() {
        return previewTarget;
    }

    public static void setTarget(BlockPos basePos, boolean valid) {
        if (selectedBlueprint == null) {
            previewTarget = null;
            return;
        }

        previewTarget = new PreviewTarget(basePos.immutable(), valid);
    }

    public static void hideTarget() {
        previewTarget = null;
    }

    public record SelectedBlueprint(
            String blueprintId,
            String blueprintName,
            int width,
            int height,
            int depth,
            int rotation
    ) {
        public int rotatedWidth() {
            return rotation == 90 || rotation == 270 ? depth : width;
        }

        public int rotatedDepth() {
            return rotation == 90 || rotation == 270 ? width : depth;
        }
    }

    public record PreviewTarget(BlockPos basePos, boolean valid) {
    }
}
