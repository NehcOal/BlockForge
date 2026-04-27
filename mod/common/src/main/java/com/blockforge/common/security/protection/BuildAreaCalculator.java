package com.blockforge.common.security.protection;

import com.blockforge.common.blueprint.BlueprintSize;
import com.blockforge.common.preview.PreviewBounds;
import com.blockforge.common.util.BlockPosition;

public final class BuildAreaCalculator {
    private BuildAreaCalculator() {
    }

    public static BuildArea fromBlueprint(
            String dimensionId,
            BlockPosition basePosition,
            BlueprintSize size,
            int rotationDegrees,
            int blockCount
    ) {
        PreviewBounds bounds = PreviewBounds.of(size.width(), size.height(), size.depth(), rotationDegrees);
        return new BuildArea(
                dimensionId,
                basePosition.x(),
                basePosition.y(),
                basePosition.z(),
                basePosition.x() + bounds.rotatedWidth() - 1,
                basePosition.y() + bounds.height() - 1,
                basePosition.z() + bounds.rotatedDepth() - 1,
                blockCount
        );
    }
}
