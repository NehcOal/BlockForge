package com.blockforge.connector.builder;

import com.blockforge.connector.blueprint.Blueprint;
import com.blockforge.connector.blueprint.BlueprintBlock;

public enum BlueprintRotation {
    NONE(com.blockforge.common.rotation.BlueprintRotation.NONE),
    CLOCKWISE_90(com.blockforge.common.rotation.BlueprintRotation.CLOCKWISE_90),
    CLOCKWISE_180(com.blockforge.common.rotation.BlueprintRotation.CLOCKWISE_180),
    COUNTERCLOCKWISE_90(com.blockforge.common.rotation.BlueprintRotation.COUNTERCLOCKWISE_90);

    private final com.blockforge.common.rotation.BlueprintRotation commonRotation;

    BlueprintRotation(com.blockforge.common.rotation.BlueprintRotation commonRotation) {
        this.commonRotation = commonRotation;
    }

    public int degrees() {
        return commonRotation.degrees();
    }

    public com.blockforge.common.rotation.BlueprintRotation toCommon() {
        return commonRotation;
    }

    public static BlueprintRotation fromDegrees(String value) {
        return switch (com.blockforge.common.rotation.BlueprintRotation.fromDegrees(value)) {
            case NONE -> NONE;
            case CLOCKWISE_90 -> CLOCKWISE_90;
            case CLOCKWISE_180 -> CLOCKWISE_180;
            case COUNTERCLOCKWISE_90 -> COUNTERCLOCKWISE_90;
        };
    }

    public RotatedPosition rotate(BlueprintBlock block, Blueprint.BlueprintSize size) {
        com.blockforge.common.rotation.BlueprintRotation.RotatedPosition rotated =
                commonRotation.rotate(
                        block,
                        new com.blockforge.common.blueprint.BlueprintSize(
                                size.width(),
                                size.height(),
                                size.depth()
                        )
                );

        return new RotatedPosition(rotated.x(), rotated.z());
    }

    public String rotateFacing(String facing) {
        return commonRotation.rotateFacing(facing);
    }

    public record RotatedPosition(int x, int z) {
    }
}
