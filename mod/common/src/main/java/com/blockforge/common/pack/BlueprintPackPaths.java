package com.blockforge.common.pack;

import java.util.Locale;

public final class BlueprintPackPaths {
    public static final String MANIFEST_PATH = "blockforge-pack.json";
    public static final String BLUEPRINT_PREFIX = "blueprints/";
    private static final String SAFE_ID_PATTERN = "[a-z0-9_-]+";

    private BlueprintPackPaths() {
    }

    public static String requireSafeId(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("missing " + fieldName);
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if (!normalized.matches(SAFE_ID_PATTERN)) {
            throw new IllegalArgumentException(fieldName + " must match " + SAFE_ID_PATTERN);
        }
        return normalized;
    }

    public static String validateBlueprintPath(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("blank blueprint path");
        }
        String normalized = path.trim();
        if (normalized.contains("\\")) {
            throw new IllegalArgumentException("blueprint path must use forward slashes");
        }
        if (normalized.startsWith("/") || normalized.matches("^[A-Za-z]:/.*")) {
            throw new IllegalArgumentException("blueprint path must be relative");
        }
        if (!normalized.startsWith(BLUEPRINT_PREFIX)) {
            throw new IllegalArgumentException("blueprint path must be inside " + BLUEPRINT_PREFIX);
        }
        for (String part : normalized.split("/")) {
            if (part.equals("..")) {
                throw new IllegalArgumentException("blueprint path must not contain traversal");
            }
        }
        if (!normalized.endsWith(".blueprint.json") && !normalized.endsWith(".json")) {
            throw new IllegalArgumentException("blueprint path must point to JSON");
        }
        return normalized;
    }

    public static String registryId(String packId, String blueprintId) {
        return requireSafeId(packId, "packId") + "/" + requireSafeId(blueprintId, "blueprint id");
    }
}
