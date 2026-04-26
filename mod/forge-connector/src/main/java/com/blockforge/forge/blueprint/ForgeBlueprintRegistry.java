package com.blockforge.forge.blueprint;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.pack.LoadedBlueprintPack;
import com.blockforge.forge.pack.ForgeBlueprintPackLoader;
import com.blockforge.forge.pack.ForgeBlueprintPackRegistry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ForgeBlueprintRegistry {
    private final ForgeBlueprintLoader loader = new ForgeBlueprintLoader();
    private final Path directory;
    private final ForgeBlueprintPackRegistry packRegistry;
    private final Map<String, Blueprint> blueprints = new LinkedHashMap<>();
    private Set<String> looseBlueprintIds = Set.of();
    private List<String> lastWarnings = List.of();

    public ForgeBlueprintRegistry(Path directory) {
        this.directory = directory;
        this.packRegistry = new ForgeBlueprintPackRegistry(ForgeBlueprintPackLoader.defaultPackDirectory());
    }

    public LoadSummary reload() {
        ForgeBlueprintLoader.LoadResult result = loader.load(directory);

        blueprints.clear();
        List<String> warnings = new ArrayList<>(result.warnings());
        for (Blueprint blueprint : result.blueprints()) {
            blueprints.put(blueprint.getId(), blueprint);
        }
        looseBlueprintIds = Set.copyOf(blueprints.keySet());

        ForgeBlueprintPackRegistry.LoadResult packResult = packRegistry.reload(looseBlueprintIds);
        blueprints.putAll(packResult.blueprints());
        warnings.addAll(packResult.warnings());

        lastWarnings = List.copyOf(warnings);
        return new LoadSummary(blueprints.size(), packRegistry.getPacks().size(), lastWarnings);
    }

    public Optional<Blueprint> get(String id) {
        return Optional.ofNullable(blueprints.get(id));
    }

    public Collection<Blueprint> getBlueprints() {
        return List.copyOf(blueprints.values());
    }

    public Set<String> getIds() {
        return Set.copyOf(blueprints.keySet());
    }

    public Path getDirectory() {
        return directory;
    }

    public List<String> getLastWarnings() {
        return lastWarnings;
    }

    public Path getPackDirectory() {
        return packRegistry.getDirectory();
    }

    public List<LoadedBlueprintPack> getPacks() {
        return packRegistry.getPacks();
    }

    public ForgeBlueprintPackRegistry.LoadResult validatePacks() {
        return packRegistry.validate(looseBlueprintIds);
    }

    public record LoadSummary(int loadedCount, int packCount, List<String> warnings) {
        public LoadSummary {
            warnings = warnings == null ? List.of() : List.copyOf(warnings);
        }
    }
}
