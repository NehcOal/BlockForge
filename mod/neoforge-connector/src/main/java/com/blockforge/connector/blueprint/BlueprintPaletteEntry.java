package com.blockforge.connector.blueprint;

import java.util.Map;

public record BlueprintPaletteEntry(String name, Map<String, String> properties) {
    public BlueprintPaletteEntry {
        properties = properties == null ? Map.of() : Map.copyOf(properties);
    }

    public int propertyCount() {
        return properties.size();
    }
}
