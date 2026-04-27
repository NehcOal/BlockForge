package com.blockforge.common.pack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BlueprintPackManifestParser {
    public BlueprintPackManifest parse(JsonObject root) {
        int schemaVersion = requiredInt(root, "schemaVersion");
        List<BlueprintPackEntry> entries = parseEntries(requiredArray(root, "blueprints"));
        ensureUniqueBlueprintIds(entries);

        return new BlueprintPackManifest(
                schemaVersion,
                requiredString(root, "packId"),
                requiredString(root, "name"),
                requiredString(root, "version"),
                optionalString(root, "description"),
                optionalString(root, "author"),
                optionalString(root, "license"),
                requiredString(root, "minecraftVersion"),
                requiredString(root, "blockforgeVersion"),
                optionalStringArray(root, "tags"),
                entries
        );
    }

    private List<BlueprintPackEntry> parseEntries(List<JsonElement> elements) {
        List<BlueprintPackEntry> entries = new ArrayList<>();
        for (int index = 0; index < elements.size(); index++) {
            JsonObject entry = requiredObject(elements.get(index), "blueprints[" + index + "]");
            entries.add(new BlueprintPackEntry(
                    requiredString(entry, "id"),
                    requiredString(entry, "name"),
                    requiredString(entry, "path"),
                    optionalString(entry, "description"),
                    optionalStringArray(entry, "tags"),
                    optionalString(entry, "previewImage")
            ));
        }
        return entries;
    }

    private void ensureUniqueBlueprintIds(List<BlueprintPackEntry> entries) {
        Set<String> ids = new HashSet<>();
        for (BlueprintPackEntry entry : entries) {
            if (!ids.add(entry.id())) {
                throw new IllegalArgumentException("duplicate blueprint id in pack: " + entry.id());
            }
        }
    }

    private JsonObject requiredObject(JsonElement element, String fieldName) {
        if (element == null || !element.isJsonObject()) {
            throw new IllegalArgumentException("missing object field: " + fieldName);
        }
        return element.getAsJsonObject();
    }

    private List<JsonElement> requiredArray(JsonObject object, String key) {
        if (!object.has(key) || object.get(key) == null || !object.get(key).isJsonArray()) {
            throw new IllegalArgumentException("missing array field: " + key);
        }
        List<JsonElement> elements = new ArrayList<>();
        object.getAsJsonArray(key).forEach(elements::add);
        return elements;
    }

    private List<String> optionalStringArray(JsonObject object, String key) {
        if (!object.has(key) || object.get(key).isJsonNull()) {
            return List.of();
        }
        if (!object.get(key).isJsonArray()) {
            throw new IllegalArgumentException("missing array field: " + key);
        }
        List<String> values = new ArrayList<>();
        for (JsonElement element : object.getAsJsonArray(key)) {
            values.add(requiredString(element, key));
        }
        return values;
    }

    private String requiredString(JsonObject object, String key) {
        if (!object.has(key)) {
            throw new IllegalArgumentException("missing string field: " + key);
        }
        return requiredString(object.get(key), key);
    }

    private String requiredString(JsonElement element, String fieldName) {
        JsonPrimitive primitive = requiredPrimitive(element, fieldName, "string");
        if (!primitive.isString()) {
            throw new IllegalArgumentException("missing string field: " + fieldName);
        }
        String value = primitive.getAsString();
        if (value.isBlank()) {
            throw new IllegalArgumentException("blank string field: " + fieldName);
        }
        return value;
    }

    private String optionalString(JsonObject object, String key) {
        if (!object.has(key) || object.get(key).isJsonNull()) {
            return "";
        }
        return requiredString(object.get(key), key);
    }

    private int requiredInt(JsonObject object, String key) {
        if (!object.has(key)) {
            throw new IllegalArgumentException("missing integer field: " + key);
        }
        JsonPrimitive primitive = requiredPrimitive(object.get(key), key, "integer");
        if (!primitive.isNumber()) {
            throw new IllegalArgumentException("missing integer field: " + key);
        }
        return primitive.getAsInt();
    }

    private JsonPrimitive requiredPrimitive(JsonElement element, String fieldName, String expectedType) {
        if (element == null || !element.isJsonPrimitive()) {
            throw new IllegalArgumentException("missing " + expectedType + " field: " + fieldName);
        }
        return element.getAsJsonPrimitive();
    }
}
