package com.blockforge.common.blueprint;

public record BlueprintSize(int width, int height, int depth) {
    public String format() {
        return width + " x " + height + " x " + depth;
    }
}
