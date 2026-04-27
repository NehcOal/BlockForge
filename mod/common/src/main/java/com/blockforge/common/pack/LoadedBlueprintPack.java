package com.blockforge.common.pack;

import java.util.List;

public record LoadedBlueprintPack(
        BlueprintPackManifest manifest,
        List<BlueprintPackRegistryEntry> entries,
        List<String> warnings
) {
    public LoadedBlueprintPack {
        entries = entries == null ? List.of() : List.copyOf(entries);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }
}
