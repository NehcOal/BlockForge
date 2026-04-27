package com.blockforge.connector.schematic;

import com.blockforge.common.schematic.SchematicIdUtil;
import com.blockforge.common.schematic.SpongeSchematicImportResult;
import com.blockforge.common.schematic.SpongeSchematicReader;
import com.blockforge.connector.blueprint.Blueprint;
import com.blockforge.connector.blueprint.BlueprintBlock;
import com.blockforge.connector.blueprint.BlueprintPaletteEntry;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class SchematicRegistry {
    private final Path directory;
    private final SpongeSchematicReader reader = new SpongeSchematicReader();
    private final Map<String, Blueprint> blueprints = new LinkedHashMap<>();
    private final Map<String, SpongeSchematicImportResult> results = new LinkedHashMap<>();
    private List<String> warnings = List.of();

    public SchematicRegistry(Path directory) {
        this.directory = directory;
    }

    public static Path defaultDirectory() {
        return FMLPaths.CONFIGDIR.get().resolve("blockforge").resolve("schematics");
    }

    public LoadResult reload(Set<String> reservedIds) {
        blueprints.clear();
        results.clear();
        List<String> nextWarnings = new ArrayList<>();
        try {
            Files.createDirectories(directory);
            try (Stream<Path> files = Files.list(directory)) {
                files.filter(path -> path.getFileName().toString().endsWith(".schem"))
                        .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                        .forEach(path -> loadOne(path, reservedIds, nextWarnings));
            }
        } catch (IOException error) {
            nextWarnings.add("Failed to scan NeoForge schematics directory: " + error.getMessage());
        }
        warnings = List.copyOf(nextWarnings);
        return new LoadResult(Map.copyOf(blueprints), List.copyOf(results.values()), warnings);
    }

    public LoadResult validate(Set<String> reservedIds) {
        Map<String, Blueprint> originalBlueprints = new LinkedHashMap<>(blueprints);
        Map<String, SpongeSchematicImportResult> originalResults = new LinkedHashMap<>(results);
        List<String> originalWarnings = warnings;
        LoadResult result = reload(reservedIds);
        blueprints.clear();
        blueprints.putAll(originalBlueprints);
        results.clear();
        results.putAll(originalResults);
        warnings = originalWarnings;
        return result;
    }

    private void loadOne(Path path, Set<String> reservedIds, List<String> warnings) {
        String id = SchematicIdUtil.idForFile(path.getFileName().toString());
        if (reservedIds.contains(id)) {
            warnings.add("Skipped schematic id conflict: " + id);
            return;
        }
        try {
            SpongeSchematicImportResult result = reader.read(path, id);
            blueprints.put(id, convert(result.blueprint()));
            results.put(id, result);
            warnings.addAll(result.warnings().stream().map(warning -> id + ": " + warning).toList());
        } catch (RuntimeException | IOException error) {
            warnings.add("Failed to load schematic " + path.getFileName() + ": " + error.getMessage());
        }
    }

    private Blueprint convert(com.blockforge.common.blueprint.Blueprint blueprint) {
        Map<String, BlueprintPaletteEntry> palette = new LinkedHashMap<>();
        blueprint.getPalette().forEach((key, entry) -> palette.put(key, new BlueprintPaletteEntry(entry.name(), entry.properties())));
        List<BlueprintBlock> blocks = blueprint.getBlocks().stream()
                .map(block -> new BlueprintBlock(block.getX(), block.getY(), block.getZ(), block.getState()))
                .toList();
        return new Blueprint(
                blueprint.getSchemaVersion(),
                blueprint.getId(),
                blueprint.getName(),
                blueprint.getDescription(),
                blueprint.getMinecraftVersion(),
                blueprint.getGenerator(),
                new Blueprint.BlueprintSize(blueprint.getSize().width(), blueprint.getSize().height(), blueprint.getSize().depth()),
                palette,
                blocks
        );
    }

    public Path getDirectory() {
        return directory;
    }

    public List<SpongeSchematicImportResult> getResults() {
        return List.copyOf(results.values());
    }

    public record LoadResult(
            Map<String, Blueprint> blueprints,
            List<SpongeSchematicImportResult> schematics,
            List<String> warnings
    ) {
    }
}
