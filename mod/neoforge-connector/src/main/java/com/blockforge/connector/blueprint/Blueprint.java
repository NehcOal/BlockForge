package com.blockforge.connector.blueprint;

import java.util.List;
import java.util.Map;

public class Blueprint {
    private final int schemaVersion;
    private final String id;
    private final String name;
    private final String description;
    private final String minecraftVersion;
    private final String generator;
    private final BlueprintSize size;
    private final Map<String, BlueprintPaletteEntry> palette;
    private final List<BlueprintBlock> blocks;

    public Blueprint(
            int schemaVersion,
            String id,
            String name,
            String description,
            String minecraftVersion,
            String generator,
            BlueprintSize size,
            Map<String, BlueprintPaletteEntry> palette,
            List<BlueprintBlock> blocks
    ) {
        this.schemaVersion = schemaVersion;
        this.id = id;
        this.name = name;
        this.description = description;
        this.minecraftVersion = minecraftVersion;
        this.generator = generator;
        this.size = size;
        this.palette = palette == null ? Map.of() : Map.copyOf(palette);
        this.blocks = blocks == null ? List.of() : List.copyOf(blocks);
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name == null || name.isBlank() ? id : name;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public String getMinecraftVersion() {
        return minecraftVersion == null ? "" : minecraftVersion;
    }

    public String getGenerator() {
        return generator == null ? "" : generator;
    }

    public BlueprintSize getSize() {
        return size;
    }

    public Map<String, BlueprintPaletteEntry> getPalette() {
        return palette;
    }

    public List<BlueprintBlock> getBlocks() {
        return blocks;
    }

    public int getBlockCount() {
        return blocks.size();
    }

    public int getPalettePropertyCount() {
        return palette.values().stream().mapToInt(BlueprintPaletteEntry::propertyCount).sum();
    }

    public long getBlocksWithPropertiesCount() {
        return blocks.stream()
                .map(BlueprintBlock::getState)
                .map(palette::get)
                .filter(entry -> entry != null && !entry.properties().isEmpty())
                .count();
    }

    public record BlueprintSize(int width, int height, int depth) {
        public String format() {
            return width + " x " + height + " x " + depth;
        }
    }
}
