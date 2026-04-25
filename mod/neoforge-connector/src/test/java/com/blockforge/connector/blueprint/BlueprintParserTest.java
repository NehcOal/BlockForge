package com.blockforge.connector.blueprint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlueprintParserTest {
    private final BlueprintParser parser = new BlueprintParser();

    @Test
    void parsesValidBlueprintV2() {
        Blueprint blueprint = parser.parse(jsonObject("""
                {
                  "schemaVersion": 2,
                  "id": "tiny_test",
                  "name": "Tiny Test",
                  "minecraftVersion": "1.21.1",
                  "generator": "BlockForge",
                  "size": { "width": 1, "height": 1, "depth": 1 },
                  "palette": {
                    "stone": { "name": "minecraft:stone" }
                  },
                  "blocks": [
                    { "x": 0, "y": 0, "z": 0, "state": "stone" }
                  ]
                }
                """));

        assertEquals("tiny_test", blueprint.getId());
        assertEquals(1, blueprint.getBlockCount());
    }

    @Test
    void rejectsMalformedPaletteEntryAsIllegalArgumentException() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> parser.parse(jsonObject("""
                {
                  "schemaVersion": 2,
                  "id": "bad_palette",
                  "name": "Bad Palette",
                  "minecraftVersion": "1.21.1",
                  "generator": "BlockForge",
                  "size": { "width": 1, "height": 1, "depth": 1 },
                  "palette": {
                    "stone": "minecraft:stone"
                  },
                  "blocks": [
                    { "x": 0, "y": 0, "z": 0, "state": "stone" }
                  ]
                }
                """)));

        assertTrue(error.getMessage().contains("palette.stone"));
    }

    @Test
    void rejectsBlockOutsideDeclaredSize() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> parser.parse(jsonObject("""
                {
                  "schemaVersion": 2,
                  "id": "out_of_bounds",
                  "name": "Out Of Bounds",
                  "minecraftVersion": "1.21.1",
                  "generator": "BlockForge",
                  "size": { "width": 1, "height": 1, "depth": 1 },
                  "palette": {
                    "stone": { "name": "minecraft:stone" }
                  },
                  "blocks": [
                    { "x": 1, "y": 0, "z": 0, "state": "stone" }
                  ]
                }
                """)));

        assertTrue(error.getMessage().contains("outside declared size"));
    }

    @Test
    void rejectsDuplicateBlockCoordinates() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> parser.parse(jsonObject("""
                {
                  "schemaVersion": 2,
                  "id": "duplicate_blocks",
                  "name": "Duplicate Blocks",
                  "minecraftVersion": "1.21.1",
                  "generator": "BlockForge",
                  "size": { "width": 1, "height": 1, "depth": 1 },
                  "palette": {
                    "stone": { "name": "minecraft:stone" }
                  },
                  "blocks": [
                    { "x": 0, "y": 0, "z": 0, "state": "stone" },
                    { "x": 0, "y": 0, "z": 0, "state": "stone" }
                  ]
                }
                """)));

        assertTrue(error.getMessage().contains("duplicate block coordinate"));
    }

    @Test
    void rejectsMissingPaletteReference() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> parser.parse(jsonObject("""
                {
                  "schemaVersion": 2,
                  "id": "missing_palette",
                  "name": "Missing Palette",
                  "minecraftVersion": "1.21.1",
                  "generator": "BlockForge",
                  "size": { "width": 1, "height": 1, "depth": 1 },
                  "palette": {
                    "stone": { "name": "minecraft:stone" }
                  },
                  "blocks": [
                    { "x": 0, "y": 0, "z": 0, "state": "glass" }
                  ]
                }
                """)));

        assertTrue(error.getMessage().contains("missing palette entry"));
    }

    private JsonObject jsonObject(String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }
}
