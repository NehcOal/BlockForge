package com.blockforge.common.blueprint;

public class BlueprintBlock {
    private final int x;
    private final int y;
    private final int z;
    private final String state;

    public BlueprintBlock(int x, int y, int z, String state) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.state = state;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getState() {
        return state;
    }
}
