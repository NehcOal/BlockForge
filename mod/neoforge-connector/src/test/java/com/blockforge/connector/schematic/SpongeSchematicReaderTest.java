package com.blockforge.connector.schematic;

import com.blockforge.common.blueprint.BlueprintPaletteEntry;
import com.blockforge.common.schematic.SpongeSchematicImportResult;
import com.blockforge.common.schematic.SpongeSchematicReader;
import com.blockforge.common.schematic.VarIntCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpongeSchematicReaderTest {
    @TempDir
    Path tempDir;

    @Test
    void importsCommonBlockstatePropertiesAndWarnings() throws IOException {
        Path file = tempDir.resolve("state_samples.schem");
        writeSchematic(file, 3, 2, 1, 1, Map.of(
                "minecraft:air", 0,
                "minecraft:oak_door[facing=north,half=lower,hinge=left,open=false,powered=false]", 1,
                "minecraft:oak_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]", 2
        ), List.of(1, 2), true);

        SpongeSchematicImportResult result = new SpongeSchematicReader().read(file, "schem/state_samples");

        assertEquals("schem/state_samples", result.blueprint().getId());
        assertEquals(2, result.blueprint().getSize().width());
        assertEquals(2, result.blueprint().getBlockCount());
        assertEquals(2, result.blueprint().getPalette().size());
        assertEquals(9, result.blueprint().getPalettePropertyCount());
        assertEquals(3, result.warnings().size());

        BlueprintPaletteEntry door = result.blueprint().getPalette().get("s1");
        assertEquals("minecraft:oak_door", door.name());
        assertEquals("left", door.properties().get("hinge"));
        assertEquals("false", door.properties().get("powered"));

        BlueprintPaletteEntry stairs = result.blueprint().getPalette().get("s2");
        assertEquals("minecraft:oak_stairs", stairs.name());
        assertEquals("straight", stairs.properties().get("shape"));
        assertEquals("false", stairs.properties().get("waterlogged"));
    }

    @Test
    void rejectsMissingPaletteIndexes() throws IOException {
        Path file = tempDir.resolve("missing_palette.schem");
        writeSchematic(file, 3, 1, 1, 1, Map.of("minecraft:air", 0), List.of(1), false);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> new SpongeSchematicReader().read(file, "schem/missing_palette")
        );

        assertTrue(error.getMessage().contains("missing palette index"));
    }

    @Test
    void rejectsUnsupportedVersion() throws IOException {
        Path file = tempDir.resolve("old_version.schem");
        writeSchematic(file, 2, 1, 1, 1, Map.of("minecraft:air", 0), List.of(0), false);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> new SpongeSchematicReader().read(file, "schem/old_version")
        );

        assertTrue(error.getMessage().contains("unsupported schematic Version: 2"));
    }

    private void writeSchematic(
            Path file,
            int version,
            int width,
            int height,
            int length,
            Map<String, Integer> palette,
            List<Integer> data,
            boolean includeIgnoredContent
    ) throws IOException {
        try (DataOutputStream output = new DataOutputStream(new GZIPOutputStream(Files.newOutputStream(file)))) {
            output.writeByte(10);
            output.writeUTF("Schematic");
            writeIntTag(output, "Version", version);
            writeIntTag(output, "DataVersion", 3955);
            writeShortTag(output, "Width", width);
            writeShortTag(output, "Height", height);
            writeShortTag(output, "Length", length);
            writeIntArrayTag(output, "Offset", new int[]{0, 0, 0});
            writeEmptyCompoundTag(output, "Metadata");

            output.writeByte(10);
            output.writeUTF("Blocks");
            writePaletteTag(output, palette);
            writeByteArrayTag(output, "Data", VarIntCodec.encode(data));
            if (includeIgnoredContent) {
                writeEmptyListTag(output, "BlockEntities", 10);
            }
            output.writeByte(0);

            if (includeIgnoredContent) {
                writeEmptyListTag(output, "Entities", 10);
                writeByteArrayTag(output, "Biomes", new byte[]{0, 0});
            }
            output.writeByte(0);
        }
    }

    private void writePaletteTag(DataOutputStream output, Map<String, Integer> palette) throws IOException {
        output.writeByte(10);
        output.writeUTF("Palette");
        for (Map.Entry<String, Integer> entry : sortedEntries(palette).entrySet()) {
            writeIntTag(output, entry.getKey(), entry.getValue());
        }
        output.writeByte(0);
    }

    private Map<String, Integer> sortedEntries(Map<String, Integer> entries) {
        return new LinkedHashMap<>(entries.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(
                        LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        LinkedHashMap::putAll
                ));
    }

    private void writeIntTag(DataOutputStream output, String name, int value) throws IOException {
        output.writeByte(3);
        output.writeUTF(name);
        output.writeInt(value);
    }

    private void writeShortTag(DataOutputStream output, String name, int value) throws IOException {
        output.writeByte(2);
        output.writeUTF(name);
        output.writeShort(value);
    }

    private void writeByteArrayTag(DataOutputStream output, String name, byte[] value) throws IOException {
        output.writeByte(7);
        output.writeUTF(name);
        output.writeInt(value.length);
        output.write(value);
    }

    private void writeIntArrayTag(DataOutputStream output, String name, int[] value) throws IOException {
        output.writeByte(11);
        output.writeUTF(name);
        output.writeInt(value.length);
        for (int item : value) {
            output.writeInt(item);
        }
    }

    private void writeEmptyListTag(DataOutputStream output, String name, int itemType) throws IOException {
        output.writeByte(9);
        output.writeUTF(name);
        output.writeByte(itemType);
        output.writeInt(0);
    }

    private void writeEmptyCompoundTag(DataOutputStream output, String name) throws IOException {
        output.writeByte(10);
        output.writeUTF(name);
        output.writeByte(0);
    }
}
