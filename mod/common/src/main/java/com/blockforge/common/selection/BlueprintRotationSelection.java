package com.blockforge.common.selection;

import com.blockforge.common.rotation.BlueprintRotation;

public record BlueprintRotationSelection(int rotationDegrees) {
    public BlueprintRotationSelection {
        validate(rotationDegrees);
    }

    public BlueprintRotation rotation() {
        return BlueprintRotation.fromDegrees(Integer.toString(rotationDegrees));
    }

    public static void validate(int rotationDegrees) {
        if (rotationDegrees != 0
                && rotationDegrees != 90
                && rotationDegrees != 180
                && rotationDegrees != 270) {
            throw new IllegalArgumentException("Unsupported rotation: " + rotationDegrees);
        }
    }
}
