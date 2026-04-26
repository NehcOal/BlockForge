package com.blockforge.common.blueprint;

import java.util.Map;

public class BlueprintPaletteEntry {
    private final String name;
    private final Map<String, String> properties;

    public BlueprintPaletteEntry(String name, Map<String, String> properties) {
        this.name = name;
        this.properties = properties == null ? Map.of() : Map.copyOf(properties);
    }

    public String name() {
        return name;
    }

    public Map<String, String> properties() {
        return properties;
    }

    public int propertyCount() {
        return properties.size();
    }
}
