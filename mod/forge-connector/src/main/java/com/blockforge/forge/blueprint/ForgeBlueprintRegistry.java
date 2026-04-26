package com.blockforge.forge.blueprint;

import com.blockforge.common.blueprint.Blueprint;

import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ForgeBlueprintRegistry {
    private final ForgeBlueprintLoader loader = new ForgeBlueprintLoader();
    private final Path directory;
    private final Map<String, Blueprint> blueprints = new LinkedHashMap<>();
    private List<String> lastWarnings = List.of();

    public ForgeBlueprintRegistry(Path directory) {
        this.directory = directory;
    }

    public LoadSummary reload() {
        ForgeBlueprintLoader.LoadResult result = loader.load(directory);

        blueprints.clear();
        for (Blueprint blueprint : result.blueprints()) {
            blueprints.put(blueprint.getId(), blueprint);
        }

        lastWarnings = List.copyOf(result.warnings());
        return new LoadSummary(blueprints.size(), lastWarnings);
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

    public record LoadSummary(int loadedCount, List<String> warnings) {
        public LoadSummary {
            warnings = warnings == null ? List.of() : List.copyOf(warnings);
        }
    }
}
