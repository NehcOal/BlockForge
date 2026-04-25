package com.blockforge.connector.blueprint;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BlueprintParser {
    public Blueprint parse(JsonObject root) {
        int schemaVersion = requiredInt(root, "schemaVersion");

        if (schemaVersion != 1 && schemaVersion != 2) {
            throw new IllegalArgumentException("unsupported schemaVersion: " + schemaVersion);
        }

        Blueprint.BlueprintSize size = parseSize(requiredObject(root, "size"));
        validateSize(size);
        Map<String, BlueprintPaletteEntry> palette = schemaVersion == 1
                ? parseV1Palette(requiredObject(root, "palette"))
                : parseV2Palette(requiredObject(root, "palette"));
        List<BlueprintBlock> blocks = schemaVersion == 1
                ? parseV1Blocks(requiredArray(root, "blocks"))
                : parseV2Blocks(requiredArray(root, "blocks"));
        validateBlocks(size, palette, blocks);

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
            entries.put(
                    entry.getKey(),
                    new BlueprintPaletteEntry(requiredString(entry.getValue(), "palette." + entry.getKey()), Map.of())
            );
        }

        return entries;
    }

    private Map<String, BlueprintPaletteEntry> parseV2Palette(JsonObject palette) {
        Map<String, BlueprintPaletteEntry> entries = new LinkedHashMap<>();

        for (Map.Entry<String, JsonElement> entry : palette.entrySet()) {
            JsonObject value = requiredObject(entry.getValue(), "palette." + entry.getKey());
            Map<String, String> properties = new LinkedHashMap<>();

            if (value.has("properties") && !value.get("properties").isJsonNull()) {
                JsonObject propertyObject = requiredObject(value.get("properties"), "palette." + entry.getKey() + ".properties");
                for (Map.Entry<String, JsonElement> property : propertyObject.entrySet()) {
                    properties.put(
                            property.getKey(),
                            requiredString(property.getValue(), "palette." + entry.getKey() + ".properties." + property.getKey())
                    );
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

        for (int index = 0; index < elements.size(); index++) {
            JsonObject block = requiredObject(elements.get(index), "blocks[" + index + "]");
            blocks.add(new BlueprintBlock(
                    requiredInt(block, "x"),
                    requiredInt(block, "y"),
                    requiredInt(block, "z"),
                    requiredString(block, stateField)
            ));
        }

        return blocks;
    }

    private void validateSize(Blueprint.BlueprintSize size) {
        if (size.width() <= 0 || size.height() <= 0 || size.depth() <= 0) {
            throw new IllegalArgumentException("size dimensions must be positive");
        }
    }

    private void validateBlocks(
            Blueprint.BlueprintSize size,
            Map<String, BlueprintPaletteEntry> palette,
            List<BlueprintBlock> blocks
    ) {
        Set<String> seenPositions = new HashSet<>();

        for (BlueprintBlock block : blocks) {
            if (!palette.containsKey(block.getState())) {
                throw new IllegalArgumentException("block references missing palette entry: " + block.getState());
            }

            boolean inBounds = block.getX() >= 0
                    && block.getX() < size.width()
                    && block.getY() >= 0
                    && block.getY() < size.height()
                    && block.getZ() >= 0
                    && block.getZ() < size.depth();

            if (!inBounds) {
                throw new IllegalArgumentException("block coordinate outside declared size: "
                        + block.getX()
                        + ":"
                        + block.getY()
                        + ":"
                        + block.getZ());
            }

            String positionKey = block.getX() + ":" + block.getY() + ":" + block.getZ();
            if (!seenPositions.add(positionKey)) {
                throw new IllegalArgumentException("duplicate block coordinate: " + positionKey);
            }
        }
    }

    private JsonObject requiredObject(JsonObject object, String key) {
        if (!object.has(key)) {
            throw new IllegalArgumentException("missing object field: " + key);
        }

        return requiredObject(object.get(key), key);
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

        String value = primitive.getAsString();
        if (!value.matches("-?\\d+")) {
            throw new IllegalArgumentException("invalid integer field: " + key);
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("invalid integer field: " + key);
        }
    }

    private JsonPrimitive requiredPrimitive(JsonElement element, String fieldName, String expectedType) {
        if (element == null || !element.isJsonPrimitive()) {
            throw new IllegalArgumentException("missing " + expectedType + " field: " + fieldName);
        }

        return element.getAsJsonPrimitive();
    }
}
