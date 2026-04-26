package com.blockforge.common.preview;

import com.blockforge.common.rotation.BlueprintRotation;

public record PreviewBounds(int width, int height, int depth, int rotatedWidth, int rotatedDepth) {
    public PreviewBounds {
        if (width <= 0 || height <= 0 || depth <= 0) {
            throw new IllegalArgumentException("Preview dimensions must be positive.");
        }
        if (rotatedWidth <= 0 || rotatedDepth <= 0) {
            throw new IllegalArgumentException("Rotated preview dimensions must be positive.");
        }
    }

    public static PreviewBounds of(int width, int height, int depth, int rotationDegrees) {
        BlueprintRotation rotation = BlueprintRotation.fromDegrees(Integer.toString(rotationDegrees));
        boolean swapsHorizontalAxes = rotation == BlueprintRotation.CLOCKWISE_90
                || rotation == BlueprintRotation.COUNTERCLOCKWISE_90;
        return new PreviewBounds(
                width,
                height,
                depth,
                swapsHorizontalAxes ? depth : width,
                swapsHorizontalAxes ? width : depth
        );
    }
}
