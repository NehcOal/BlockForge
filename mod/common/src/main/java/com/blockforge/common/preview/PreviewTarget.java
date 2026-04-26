package com.blockforge.common.preview;

public record PreviewTarget(int x, int y, int z, boolean visible, boolean valid, long lastUpdateTick) {
}
