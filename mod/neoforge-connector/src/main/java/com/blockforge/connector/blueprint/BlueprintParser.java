package com.blockforge.connector.blueprint;

import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlueprintParser {
    private final com.blockforge.common.blueprint.BlueprintParser parser =
            new com.blockforge.common.blueprint.BlueprintParser();

    public Blueprint parse(JsonObject root) {
        com.blockforge.common.blueprint.Blueprint parsed = parser.parse(root);
        Map<String, BlueprintPaletteEntry> palette = new LinkedHashMap<>();

        for (Map.Entry<String, com.blockforge.common.blueprint.BlueprintPaletteEntry> entry : parsed.getPalette().entrySet()) {
            palette.put(
                    entry.getKey(),
                    new BlueprintPaletteEntry(entry.getValue().name(), entry.getValue().properties())
            );
        }

        List<BlueprintBlock> blocks = parsed.getBlocks()
                .stream()
                .map(block -> new BlueprintBlock(block.getX(), block.getY(), block.getZ(), block.getState()))
                .toList();

        return new Blueprint(
                parsed.getSchemaVersion(),
                parsed.getId(),
                parsed.getName(),
                parsed.getDescription(),
                parsed.getMinecraftVersion(),
                parsed.getGenerator(),
                new Blueprint.BlueprintSize(
                        parsed.getSize().width(),
                        parsed.getSize().height(),
                        parsed.getSize().depth()
                ),
                palette,
                blocks
        );
    }
}
