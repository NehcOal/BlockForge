package com.blockforge.common.buildplan;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.blueprint.BlueprintBlock;
import com.blockforge.common.blueprint.BlueprintPaletteEntry;
import com.blockforge.common.rotation.BlueprintRotation;

public final class BuildStepPlanner {
    private BuildStepPlanner() {
    }

    public static BuildStep stepFor(
            Blueprint blueprint,
            BlueprintBlock block,
            int baseX,
            int baseY,
            int baseZ,
            BlueprintRotation rotation,
            boolean mirrorX,
            boolean mirrorZ,
            boolean replaceExisting
    ) {
        BlueprintRotation.RotatedPosition rotated = rotation.rotate(block, blueprint.getSize());
        int plannedX = rotated.x();
        int plannedZ = rotated.z();
        int width = rotation == BlueprintRotation.CLOCKWISE_90 || rotation == BlueprintRotation.COUNTERCLOCKWISE_90
                ? blueprint.getSize().depth()
                : blueprint.getSize().width();
        int depth = rotation == BlueprintRotation.CLOCKWISE_90 || rotation == BlueprintRotation.COUNTERCLOCKWISE_90
                ? blueprint.getSize().width()
                : blueprint.getSize().depth();
        if (mirrorX) {
            plannedX = width - 1 - plannedX;
        }
        if (mirrorZ) {
            plannedZ = depth - 1 - plannedZ;
        }

        BlueprintPaletteEntry entry = blueprint.getPalette().get(block.getState());
        String blockId = entry == null ? "" : entry.name();
        return new BuildStep(
                baseX + plannedX,
                baseY + block.getY(),
                baseZ + plannedZ,
                blockId,
                block.getState(),
                block.getState(),
                replaceExisting,
                BuildStepStatus.PENDING
        );
    }
}
