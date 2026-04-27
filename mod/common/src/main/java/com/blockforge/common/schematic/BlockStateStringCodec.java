package com.blockforge.common.schematic;

import com.blockforge.common.blueprint.BlueprintPaletteEntry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public final class BlockStateStringCodec {
    private BlockStateStringCodec() {
    }

    public static String encode(BlueprintPaletteEntry entry) {
        if (entry.properties().isEmpty()) {
            return entry.name();
        }
        Map<String, String> sorted = new TreeMap<>(entry.properties());
        StringBuilder builder = new StringBuilder(entry.name()).append("[");
        boolean first = true;
        for (Map.Entry<String, String> property : sorted.entrySet()) {
            if (!first) {
                builder.append(",");
            }
            builder.append(property.getKey()).append("=").append(property.getValue());
            first = false;
        }
        return builder.append("]").toString();
    }

    public static BlueprintPaletteEntry decode(String value) {
        String text = value == null ? "" : value.trim();
        int bracket = text.indexOf('[');
        if (bracket < 0) {
            return new BlueprintPaletteEntry(text, Map.of());
        }
        if (!text.endsWith("]")) {
            throw new IllegalArgumentException("invalid blockstate string: " + value);
        }
        String blockId = text.substring(0, bracket);
        Map<String, String> properties = new LinkedHashMap<>();
        String body = text.substring(bracket + 1, text.length() - 1);
        if (!body.isBlank()) {
            for (String pair : body.split(",")) {
                String[] parts = pair.split("=", 2);
                if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                    throw new IllegalArgumentException("invalid blockstate property: " + pair);
                }
                properties.put(parts[0].trim(), parts[1].trim());
            }
        }
        return new BlueprintPaletteEntry(blockId, properties);
    }
}
