package com.blockforge.fabric.blueprint;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.pack.LoadedBlueprintPack;
import com.blockforge.fabric.pack.FabricBlueprintPackLoader;
import com.blockforge.fabric.pack.FabricBlueprintPackRegistry;
import com.blockforge.fabric.schematic.FabricSchematicRegistry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class FabricBlueprintRegistry {
    private final Path directory;
    private final FabricBlueprintLoader loader;
    private final FabricBlueprintPackRegistry packRegistry;
    private final FabricSchematicRegistry schematicRegistry;
    private final Map<String, Blueprint> blueprints = new LinkedHashMap<>();
    private Set<String> looseBlueprintIds = Set.of();
    private List<String> warnings = List.of();

    public FabricBlueprintRegistry(Path directory) {
        this.directory = directory;
        this.loader = new FabricBlueprintLoader();
        this.packRegistry = new FabricBlueprintPackRegistry(FabricBlueprintPackLoader.defaultPackDirectory());
        this.schematicRegistry = new FabricSchematicRegistry(FabricSchematicRegistry.defaultDirectory());
    }

    public LoadSummary reload() {
        FabricBlueprintLoader.LoadResult result = loader.load(directory);
        blueprints.clear();
        List<String> nextWarnings = new ArrayList<>(result.warnings());

        for (Blueprint blueprint : result.blueprints()) {
            blueprints.put(blueprint.getId(), blueprint);
        }
        looseBlueprintIds = Set.copyOf(blueprints.keySet());

        FabricBlueprintPackRegistry.LoadResult packResult = packRegistry.reload(looseBlueprintIds);
        blueprints.putAll(packResult.blueprints());
        nextWarnings.addAll(packResult.warnings());

        Set<String> reservedIds = Set.copyOf(blueprints.keySet());
        FabricSchematicRegistry.LoadResult schematicResult = schematicRegistry.reload(reservedIds);
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

    public FabricSchematicRegistry.LoadResult validateSchematics() {
        return schematicRegistry.validate(Set.copyOf(blueprints.keySet()));
    }

    public FabricBlueprintPackRegistry.LoadResult validatePacks() {
        return packRegistry.validate(looseBlueprintIds);
    }

    public record LoadSummary(int loadedCount, int packCount, List<String> warnings) {
    }
}
