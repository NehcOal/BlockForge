package com.blockforge.common.schematic;

public final class SchematicIdUtil {
    private SchematicIdUtil() {
    }

    public static String idForFile(String fileName) {
        String base = fileName;
        if (base.endsWith(".schem")) {
            base = base.substring(0, base.length() - ".schem".length());
        }
        base = base.toLowerCase().replaceAll("[^a-z0-9_-]+", "_").replaceAll("^_+|_+$", "");
        if (base.isBlank()) {
            base = "schematic";
        }
        return "schem/" + base;
    }
}
