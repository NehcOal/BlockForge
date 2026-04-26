package com.blockforge.common.rotation;

import com.blockforge.common.blueprint.BlueprintBlock;
import com.blockforge.common.blueprint.BlueprintSize;

import java.util.List;

public enum BlueprintRotation {
    NONE(0),
    CLOCKWISE_90(90),
    CLOCKWISE_180(180),
    COUNTERCLOCKWISE_90(270);

    private static final List<String> HORIZONTAL_FACING = List.of("north", "east", "south", "west");

    private final int degrees;

    BlueprintRotation(int degrees) {
        this.degrees = degrees;
    }

    public int degrees() {
        return degrees;
    }

    public static BlueprintRotation fromDegrees(String value) {
        return switch (value) {
            case "0" -> NONE;
            case "90" -> CLOCKWISE_90;
            case "180" -> CLOCKWISE_180;
            case "270" -> COUNTERCLOCKWISE_90;
            default -> throw new IllegalArgumentException("Unsupported rotation: " + value);
        };
    }

    public RotatedPosition rotate(BlueprintBlock block, BlueprintSize size) {
        return switch (this) {
            case NONE -> new RotatedPosition(block.getX(), block.getZ());
            case CLOCKWISE_90 -> new RotatedPosition(size.depth() - 1 - block.getZ(), block.getX());
            case CLOCKWISE_180 -> new RotatedPosition(size.width() - 1 - block.getX(), size.depth() - 1 - block.getZ());
            case COUNTERCLOCKWISE_90 -> new RotatedPosition(block.getZ(), size.width() - 1 - block.getX());
        };
    }

    public String rotateFacing(String facing) {
        int currentIndex = HORIZONTAL_FACING.indexOf(facing);

        if (currentIndex < 0) {
            return facing;
        }

        int steps = switch (this) {
            case NONE -> 0;
            case CLOCKWISE_90 -> 1;
            case CLOCKWISE_180 -> 2;
            case COUNTERCLOCKWISE_90 -> 3;
        };

        return HORIZONTAL_FACING.get((currentIndex + steps) % HORIZONTAL_FACING.size());
    }

    public record RotatedPosition(int x, int z) {
    }
}
