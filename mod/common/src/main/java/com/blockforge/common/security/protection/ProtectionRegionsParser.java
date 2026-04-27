package com.blockforge.common.security.protection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ProtectionRegionsParser {
    private ProtectionRegionsParser() {
    }

    public static ProtectionRegionsConfig parse(String json) {
        List<String> warnings = new ArrayList<>();
        JsonElement rootElement = JsonParser.parseString(json == null ? "{}" : json);
        if (!rootElement.isJsonObject()) {
            throw new IllegalArgumentException("protection-regions.json root must be an object.");
        }

        JsonObject root = rootElement.getAsJsonObject();
        int schemaVersion = intValue(root, "schemaVersion", 1);
        if (schemaVersion != 1) {
            throw new IllegalArgumentException("Unsupported protection regions schemaVersion: " + schemaVersion);
        }

        JsonElement regionsElement = root.get("regions");
        if (regionsElement == null || !regionsElement.isJsonArray()) {
            return new ProtectionRegionsConfig(schemaVersion, List.of(), warnings);
        }

        JsonArray regionsArray = regionsElement.getAsJsonArray();
        List<BlockForgeProtectionRegion> regions = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        for (JsonElement element : regionsArray) {
            if (!element.isJsonObject()) {
                warnings.add("Skipped non-object protection region.");
                continue;
            }
            try {
                BlockForgeProtectionRegion region = parseRegion(element.getAsJsonObject());
                if (!ids.add(region.id())) {
                    warnings.add("Skipped duplicate protection region id: " + region.id());
                    continue;
                }
                regions.add(region);
            } catch (RuntimeException error) {
                warnings.add("Skipped invalid protection region: " + error.getMessage());
            }
        }

        return new ProtectionRegionsConfig(schemaVersion, regions, warnings);
    }

    private static BlockForgeProtectionRegion parseRegion(JsonObject object) {
        String id = stringValue(object, "id", "");
        String dimensionId = stringValue(object, "dimensionId", "minecraft:overworld");
        BlockForgeRegionMode mode = BlockForgeRegionMode.valueOf(stringValue(object, "mode", "DENY").toUpperCase());
        return new BlockForgeProtectionRegion(
                id,
                dimensionId,
                intValue(object, "minX", 0),
                intValue(object, "minY", -64),
                intValue(object, "minZ", 0),
                intValue(object, "maxX", 0),
                intValue(object, "maxY", 320),
                intValue(object, "maxZ", 0),
                mode,
                stringList(object.get("allowedPlayers")),
                stringList(object.get("deniedPlayers")),
                stringList(object.get("allowedPermissions")),
                stringList(object.get("tags")),
                stringValue(object, "description", "")
        );
    }

    private static String stringValue(JsonObject object, String key, String fallback) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : fallback;
    }

    private static int intValue(JsonObject object, String key, int fallback) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonPrimitive() ? element.getAsInt() : fallback;
    }

    private static List<String> stringList(JsonElement element) {
        if (element == null || !element.isJsonArray()) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        for (JsonElement item : element.getAsJsonArray()) {
            if (item.isJsonPrimitive()) {
                values.add(item.getAsString());
            }
        }
        return values;
    }
}
