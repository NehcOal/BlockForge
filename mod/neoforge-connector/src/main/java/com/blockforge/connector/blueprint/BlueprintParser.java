package com.blockforge.connector.blueprint;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlueprintParser {
    public Blueprint parse(JsonObject root) {
        int schemaVersion = requiredInt(root, "schemaVersion");

        if (schemaVersion != 1 && schemaVersion != 2) {
            throw new IllegalArgumentException("unsupported schemaVersion: " + schemaVersion);
        }

        Blueprint.BlueprintSize size = parseSize(requiredObject(root, "size"));
        Map<String, BlueprintPaletteEntry> palette = schemaVersion == 1
                ? parseV1Palette(requiredObject(root, "palette"))
                : parseV2Palette(requiredObject(root, "palette"));
        List<BlueprintBlock> blocks = schemaVersion == 1
                ? parseV1Blocks(requiredArray(root, "blocks"))
                : parseV2Blocks(requiredArray(root, "blocks"));

        return new Blueprint(
                schemaVersion,
                requiredString(root, "id"),
                optionalString(root, "name"),
                optionalString(root, "description"),
                optionalString(root, "minecraftVersion"),
                optionalString(root, "generator"),
                size,
                palette,
                blocks
        );
    }

    private Blueprint.BlueprintSize parseSize(JsonObject size) {
        return new Blueprint.BlueprintSize(
                requiredInt(size, "width"),
                requiredInt(size, "height"),
                requiredInt(size, "depth")
        );
    }

    private Map<String, BlueprintPaletteEntry> parseV1Palette(JsonObject palette) {
        Map<String, BlueprintPaletteEntry> entries = new LinkedHashMap<>();

        for (Map.Entry<String, JsonElement> entry : palette.entrySet()) {
            entries.put(entry.getKey(), new BlueprintPaletteEntry(entry.getValue().getAsString(), Map.of()));
        }

        return entries;
    }

    private Map<String, BlueprintPaletteEntry> parseV2Palette(JsonObject palette) {
        Map<String, BlueprintPaletteEntry> entries = new LinkedHashMap<>();

        for (Map.Entry<String, JsonElement> entry : palette.entrySet()) {
            JsonObject value = entry.getValue().getAsJsonObject();
            Map<String, String> properties = new LinkedHashMap<>();

            if (value.has("properties") && value.get("properties").isJsonObject()) {
                for (Map.Entry<String, JsonElement> property : value.getAsJsonObject("properties").entrySet()) {
                    properties.put(property.getKey(), property.getValue().getAsString());
                }
            }

            entries.put(
                    entry.getKey(),
                    new BlueprintPaletteEntry(requiredString(value, "name"), properties)
            );
        }

        return entries;
    }

    private List<BlueprintBlock> parseV1Blocks(List<JsonElement> elements) {
        return parseBlocks(elements, "block");
    }

    private List<BlueprintBlock> parseV2Blocks(List<JsonElement> elements) {
        return parseBlocks(elements, "state");
    }

    private List<BlueprintBlock> parseBlocks(List<JsonElement> elements, String stateField) {
        List<BlueprintBlock> blocks = new ArrayList<>();

        for (JsonElement element : elements) {
            JsonObject block = element.getAsJsonObject();
            blocks.add(new BlueprintBlock(
                    requiredInt(block, "x"),
                    requiredInt(block, "y"),
                    requiredInt(block, "z"),
                    requiredString(block, stateField)
            ));
        }

        return blocks;
    }

    private JsonObject requiredObject(JsonObject object, String key) {
        if (!object.has(key) || !object.get(key).isJsonObject()) {
            throw new IllegalArgumentException("missing object field: " + key);
        }

        return object.getAsJsonObject(key);
    }

    private List<JsonElement> requiredArray(JsonObject object, String key) {
        if (!object.has(key) || !object.get(key).isJsonArray()) {
            throw new IllegalArgumentException("missing array field: " + key);
        }

        List<JsonElement> elements = new ArrayList<>();
        object.getAsJsonArray(key).forEach(elements::add);
        return elements;
    }

    private String requiredString(JsonObject object, String key) {
        if (!object.has(key) || !object.get(key).isJsonPrimitive()) {
            throw new IllegalArgumentException("missing string field: " + key);
        }

        String value = object.get(key).getAsString();
        if (value.isBlank()) {
            throw new IllegalArgumentException("blank string field: " + key);
        }

        return value;
    }

    private String optionalString(JsonObject object, String key) {
        if (!object.has(key) || object.get(key).isJsonNull()) {
            return "";
        }

        return object.get(key).getAsString();
    }

    private int requiredInt(JsonObject object, String key) {
        if (!object.has(key) || !object.get(key).isJsonPrimitive()) {
            throw new IllegalArgumentException("missing integer field: " + key);
        }

        return object.get(key).getAsInt();
    }
}
