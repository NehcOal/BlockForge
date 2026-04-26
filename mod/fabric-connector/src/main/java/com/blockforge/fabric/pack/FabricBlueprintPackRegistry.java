package com.blockforge.fabric.pack;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.pack.LoadedBlueprintPack;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FabricBlueprintPackRegistry {
    private final Path directory;
    private final FabricBlueprintPackLoader loader = new FabricBlueprintPackLoader();
    private List<LoadedBlueprintPack> packs = List.of();
    private List<String> warnings = List.of();

    public FabricBlueprintPackRegistry(Path directory) {
        this.directory = directory;
    }

    public LoadResult reload(Set<String> reservedIds) {
        FabricBlueprintPackLoader.LoadResult result = loader.load(directory, reservedIds);
        packs = result.packs();
        warnings = result.warnings();
        return new LoadResult(result.blueprints(), packs, warnings);
    }

    public LoadResult validate(Set<String> reservedIds) {
        FabricBlueprintPackLoader.LoadResult result = loader.load(directory, reservedIds);
        return new LoadResult(result.blueprints(), result.packs(), result.warnings());
    }

    public Path getDirectory() {
        return directory;
    }

    public List<LoadedBlueprintPack> getPacks() {
        return packs;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public record LoadResult(
            Map<String, Blueprint> blueprints,
            List<LoadedBlueprintPack> packs,
            List<String> warnings
    ) {
        public LoadResult {
            blueprints = blueprints == null ? Map.of() : Map.copyOf(blueprints);
            packs = packs == null ? List.of() : List.copyOf(packs);
            warnings = warnings == null ? List.of() : List.copyOf(warnings);
        }
    }
}
