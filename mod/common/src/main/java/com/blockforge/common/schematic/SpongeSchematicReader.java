package com.blockforge.common.schematic;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.blueprint.BlueprintBlock;
import com.blockforge.common.blueprint.BlueprintPaletteEntry;
import com.blockforge.common.blueprint.BlueprintSize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class SpongeSchematicReader {
    public static final int MAX_VOLUME = 1_000_000;
    public static final long MAX_FILE_BYTES = 10L * 1024L * 1024L;

    public SpongeSchematicImportResult read(Path path, String id) throws IOException {
        if (Files.size(path) > MAX_FILE_BYTES) {
            throw new IllegalArgumentException("schematic file exceeds 10 MB: " + path.getFileName());
        }
        byte[] uncompressed = readBoundedGzip(path);
        Object root = new NbtReader(uncompressed).readNamedRoot("Schematic");
        if (!(root instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("schematic root must be a compound");
        }
        return convert(map, id);
    }

    @SuppressWarnings("unchecked")
    private SpongeSchematicImportResult convert(Map<?, ?> root, String id) {
        int version = intValue(root.get("Version"), "Version");
        if (version != 3) {
            throw new IllegalArgumentException("unsupported schematic Version: " + version);
        }
        int width = intValue(root.get("Width"), "Width");
        int height = intValue(root.get("Height"), "Height");
        int length = intValue(root.get("Length"), "Length");
        int volume = width * height * length;
        if (width <= 0 || height <= 0 || length <= 0 || volume > MAX_VOLUME) {
            throw new IllegalArgumentException("schematic volume exceeds limit or has invalid size");
        }

        Map<?, ?> blocksRoot = compoundValue(root.get("Blocks"), "Blocks");
        Map<?, ?> paletteRoot = compoundValue(blocksRoot.get("Palette"), "Blocks.Palette");
        Map<String, Integer> palette = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : paletteRoot.entrySet()) {
            palette.put(String.valueOf(entry.getKey()), intValue(entry.getValue(), "palette index"));
        }

        List<Integer> data = VarIntCodec.decode(byteArrayValue(blocksRoot.get("Data"), "Blocks.Data"));
        if (data.size() != volume) {
            throw new IllegalArgumentException("Blocks.Data length does not match schematic volume");
        }
        validatePaletteData(palette, data);

        List<String> warnings = new ArrayList<>();
        if (blocksRoot.containsKey("BlockEntities")) warnings.add("BlockEntities ignored in schematic Alpha.");
        if (root.containsKey("Entities")) warnings.add("Entities ignored in schematic Alpha.");
        if (root.containsKey("Biomes")) warnings.add("Biomes ignored in schematic Alpha.");

        Map<Integer, String> stateByIndex = new LinkedHashMap<>();
        palette.forEach((state, index) -> stateByIndex.put(index, state));
        Map<String, BlueprintPaletteEntry> blueprintPalette = new LinkedHashMap<>();
        Map<String, String> keyByState = new LinkedHashMap<>();
        palette.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .forEach(entry -> {
                    if (!entry.getKey().equals("minecraft:air")) {
                        String key = "s" + entry.getValue();
                        blueprintPalette.put(key, BlockStateStringCodec.decode(entry.getKey()));
                        keyByState.put(entry.getKey(), key);
                    }
                });

        List<BlueprintBlock> blueprintBlocks = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {
                    String state = stateByIndex.get(data.get(index(x, y, z, width, length)));
                    if (state == null || state.equals("minecraft:air")) {
                        continue;
                    }
                    String key = keyByState.get(state);
                    if (key != null) {
                        blueprintBlocks.add(new BlueprintBlock(x, y, z, key));
                    }
                }
            }
        }

        SpongeSchematic schematic = new SpongeSchematic(version, intValue(root.get("DataVersion"), "DataVersion"),
                width, height, length, intArrayValue(root.get("Offset")), palette, data, warnings);
        Blueprint blueprint = new Blueprint(2, id, id, "Imported from Sponge Schematic v3.", "1.21.1", "BlockForge",
                new BlueprintSize(width, height, length), blueprintPalette, blueprintBlocks);
        return new SpongeSchematicImportResult(blueprint, schematic, warnings);
    }

    private int index(int x, int y, int z, int width, int length) {
        return x + z * width + y * width * length;
    }

    private byte[] readBoundedGzip(Path path) throws IOException {
        try (GZIPInputStream gzip = new GZIPInputStream(Files.newInputStream(path))) {
            return readBounded(gzip, MAX_FILE_BYTES);
        }
    }

    private byte[] readBounded(InputStream input, long maxBytes) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        long total = 0;
        while (true) {
            int read = input.read(buffer);
            if (read < 0) {
                return output.toByteArray();
            }
            total += read;
            if (total > maxBytes) {
                throw new IllegalArgumentException("schematic uncompressed NBT exceeds 10 MB");
            }
            output.write(buffer, 0, read);
        }
    }

    private void validatePaletteData(Map<String, Integer> palette, List<Integer> data) {
        if (data.stream().anyMatch(index -> !palette.containsValue(index))) {
            throw new IllegalArgumentException("Blocks.Data references a missing palette index");
        }
    }

    private int intValue(Object value, String field) {
        if (!(value instanceof Number number)) {
            throw new IllegalArgumentException(field + " must be numeric");
        }
        return number.intValue();
    }

    private Map<?, ?> compoundValue(Object value, String field) {
        if (!(value instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException(field + " must be a compound");
        }
        return map;
    }

    private byte[] byteArrayValue(Object value, String field) {
        if (!(value instanceof byte[] bytes)) {
            throw new IllegalArgumentException(field + " must be a byte array");
        }
        return bytes;
    }

    private int[] intArrayValue(Object value) {
        return value instanceof int[] array && array.length == 3 ? array : new int[]{0, 0, 0};
    }

    private static final class NbtReader {
        private final DataInputStream input;

        private NbtReader(byte[] bytes) {
            this.input = new DataInputStream(new ByteArrayInputStream(bytes));
        }

        Object readNamedRoot(String expectedName) throws IOException {
            int tag = input.readUnsignedByte();
            if (tag != 10) throw new IllegalArgumentException("root tag must be compound");
            String name = input.readUTF();
            if (!expectedName.equals(name)) throw new IllegalArgumentException("root name must be " + expectedName);
            return readPayload(tag);
        }

        Object readPayload(int tag) throws IOException {
            return switch (tag) {
                case 1 -> input.readByte();
                case 2 -> input.readShort();
                case 3 -> input.readInt();
                case 4 -> input.readLong();
                case 7 -> {
                    int length = input.readInt();
                    byte[] bytes = input.readNBytes(length);
                    yield bytes;
                }
                case 8 -> input.readUTF();
                case 9 -> {
                    int itemType = input.readUnsignedByte();
                    int length = input.readInt();
                    List<Object> list = new ArrayList<>();
                    for (int i = 0; i < length; i++) list.add(readPayload(itemType));
                    yield list;
                }
                case 10 -> {
                    Map<String, Object> compound = new LinkedHashMap<>();
                    while (true) {
                        int childTag = input.readUnsignedByte();
                        if (childTag == 0) break;
                        compound.put(input.readUTF(), readPayload(childTag));
                    }
                    yield compound;
                }
                case 11 -> {
                    int length = input.readInt();
                    int[] array = new int[length];
                    for (int i = 0; i < length; i++) array[i] = input.readInt();
                    yield array;
                }
                default -> throw new IllegalArgumentException("unsupported NBT tag: " + tag);
            };
        }
    }
}
