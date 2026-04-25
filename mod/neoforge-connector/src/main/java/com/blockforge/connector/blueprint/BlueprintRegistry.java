package com.blockforge.connector.blueprint;

import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BlueprintRegistry {
    private final Path directory;
    private final BlueprintLoader loader;
    private final Map<String, Blueprint> blueprints = new LinkedHashMap<>();
    private List<String> warnings = List.of();

    public BlueprintRegistry(Path directory) {
        this.directory = directory;
        this.loader = new BlueprintLoader();
    }

    public LoadSummary reload() {
        BlueprintLoader.LoadResult result = loader.load(directory);
        blueprints.clear();

        for (Blueprint blueprint : result.blueprints()) {
            blueprints.put(blueprint.getId(), blueprint);
        }

        warnings = result.warnings();
        return new LoadSummary(blueprints.size(), warnings);
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

    public record LoadSummary(int loadedCount, List<String> warnings) {
    }
}
