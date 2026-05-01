package com.blockforge.common.gameplay;

import java.util.Locale;

public enum BuilderWandMode {
    PREVIEW,
    BUILD,
    DRY_RUN,
    MATERIALS,
    UNDO,
    ROTATE,
    MIRROR,
    OFFSET,
    ANCHOR,
    HOUSE,
    CLEAR_PREVIEW;

    public BuilderWandMode next() {
        BuilderWandMode[] modes = values();
        return modes[(ordinal() + 1) % modes.length];
    }

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static BuilderWandMode parse(String value) {
        if (value == null || value.isBlank()) {
            return BUILD;
        }
        return BuilderWandMode.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
