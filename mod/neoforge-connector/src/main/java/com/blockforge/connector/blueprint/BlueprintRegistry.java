package com.blockforge.connector.blueprint;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import com.blockforge.common.pack.LoadedBlueprintPack;
import com.blockforge.connector.pack.BlueprintPackLoader;
import com.blockforge.connector.pack.BlueprintPackRegistry;
import com.blockforge.connector.schematic.SchematicRegistry;

public class BlueprintRegistry {
    private final Path directory;
    private final BlueprintLoader loader;
    private final BlueprintPackRegistry packRegistry;
    private final SchematicRegistry schematicRegistry;
    private final Map<String, Blueprint> blueprints = new LinkedHashMap<>();
    private Set<String> looseBlueprintIds = Set.of();
    private List<String> warnings = List.of();

    public BlueprintRegistry(Path directory) {
        this.directory = directory;
        this.loader = new BlueprintLoader();
        this.packRegistry = new BlueprintPackRegistry(BlueprintPackLoader.defaultPackDirectory());
        this.schematicRegistry = new SchematicRegistry(SchematicRegistry.defaultDirectory());
    }

    public LoadSummary reload() {
        BlueprintLoader.LoadResult result = loader.load(directory);
        blueprints.clear();
        List<String> nextWarnings = new ArrayList<>(result.warnings());

        for (Blueprint blueprint : result.blueprints()) {
            blueprints.put(blueprint.getId(), blueprint);
        }
        looseBlueprintIds = Set.copyOf(blueprints.keySet());

        BlueprintPackRegistry.LoadResult packResult = packRegistry.reload(looseBlueprintIds);
        blueprints.putAll(packResult.blueprints());
        nextWarnings.addAll(packResult.warnings());

        Set<String> reservedIds = Set.copyOf(blueprints.keySet());
        SchematicRegistry.LoadResult schematicResult = schematicRegistry.reload(reservedIds);
        blueprints.putAll(schematicResult.blueprints());
        nextWarnings.addAll(schematicResult.warnings());

        warnings = List.copyOf(nextWarnings);
        return new LoadSummary(blueprints.size(), packRegistry.getPacks().size(), warnings);
    }

    public Optional<Blueprint> get(String id) {
        return Optional.ofNullable(blueprints.get(id));
    }

    public Collection<Blueprint> getBlueprints() {
        return blueprints.values();
    }

    public Collection<String> getIds() {
        return blueprints.keySet();
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public Path getDirectory() {
        return directory;
    }

    public Path getPackDirectory() {
        return packRegistry.getDirectory();
    }

    public List<LoadedBlueprintPack> getPacks() {
        return packRegistry.getPacks();
    }

    public Path getSchematicDirectory() {
        return schematicRegistry.getDirectory();
    }

    public List<com.blockforge.common.schematic.SpongeSchematicImportResult> getSchematics() {
        return schematicRegistry.getResults();
    }

    public SchematicRegistry.LoadResult validateSchematics() {
        return schematicRegistry.validate(Set.copyOf(blueprints.keySet()));
    }

    public BlueprintPackRegistry.LoadResult validatePacks() {
        return packRegistry.validate(looseBlueprintIds);
    }

    public record LoadSummary(int loadedCount, int packCount, List<String> warnings) {
    }
}
