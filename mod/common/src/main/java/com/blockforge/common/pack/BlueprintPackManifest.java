package com.blockforge.common.pack;

import java.util.List;

public record BlueprintPackManifest(
        int schemaVersion,
        String packId,
        String name,
        String version,
        String description,
        String author,
        String license,
        String minecraftVersion,
        String blockforgeVersion,
        List<String> tags,
        List<BlueprintPackEntry> blueprints
) {
    public static final int SUPPORTED_SCHEMA_VERSION = 1;
    public static final int MAX_BLUEPRINTS = 256;

    public BlueprintPackManifest {
        if (schemaVersion != SUPPORTED_SCHEMA_VERSION) {
            throw new IllegalArgumentException("unsupported pack schemaVersion: " + schemaVersion);
        }
        packId = BlueprintPackPaths.requireSafeId(packId, "packId");
        name = requireText(name, "name");
        version = requireText(version, "version");
        description = description == null ? "" : description;
        author = author == null ? "" : author;
        license = license == null ? "" : license;
        minecraftVersion = requireText(minecraftVersion, "minecraftVersion");
        blockforgeVersion = requireText(blockforgeVersion, "blockforgeVersion");
        tags = tags == null ? List.of() : List.copyOf(tags);
        blueprints = blueprints == null ? List.of() : List.copyOf(blueprints);
        if (blueprints.isEmpty()) {
            throw new IllegalArgumentException("pack must contain at least one blueprint");
        }
        if (blueprints.size() > MAX_BLUEPRINTS) {
            throw new IllegalArgumentException("pack exceeds " + MAX_BLUEPRINTS + " blueprints");
        }
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("missing pack field: " + field);
        }
        return value;
    }
}
