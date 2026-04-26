package com.blockforge.fabric.client.preview;

import com.blockforge.common.preview.PreviewBounds;
import com.blockforge.common.preview.PreviewState;
import com.blockforge.common.preview.PreviewTarget;
import com.blockforge.fabric.network.FabricBlueprintGuiNetworking;
import net.minecraft.util.math.BlockPos;

public final class FabricClientPreviewState {
    private static PreviewState selection;
    private static PreviewTarget target;

    private FabricClientPreviewState() {
    }

    public static void apply(FabricBlueprintGuiNetworking.PreviewSelectionPayload payload) {
        selection = new PreviewState(
                payload.blueprintId(),
                payload.blueprintName(),
                payload.width(),
                payload.height(),
                payload.depth(),
                payload.rotationDegrees(),
                false,
                true
        );
        target = null;
    }

    public static void clear() {
        selection = null;
        target = null;
    }

    public static PreviewState selection() {
        return selection;
    }

    public static PreviewTarget target() {
        return target;
    }

    public static void setTarget(BlockPos basePos, boolean valid, long tick) {
        if (selection == null) {
            target = null;
            return;
        }

        target = new PreviewTarget(basePos.getX(), basePos.getY(), basePos.getZ(), true, valid, tick);
        selection = new PreviewState(
                selection.blueprintId(),
                selection.blueprintName(),
                selection.width(),
                selection.height(),
                selection.depth(),
                selection.rotationDegrees(),
                true,
                valid
        );
    }

    public static void hideTarget() {
        target = null;
        if (selection != null) {
            selection = new PreviewState(
                    selection.blueprintId(),
                    selection.blueprintName(),
                    selection.width(),
                    selection.height(),
                    selection.depth(),
                    selection.rotationDegrees(),
                    false,
                    false
            );
        }
    }

    public static PreviewBounds bounds() {
        return selection == null ? null : selection.bounds();
    }
}
