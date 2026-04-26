package com.blockforge.common.pack;

import java.util.List;

public record BlueprintPackLoadResult(
        List<LoadedBlueprintPack> packs,
        List<String> warnings
) {
    public BlueprintPackLoadResult {
        packs = packs == null ? List.of() : List.copyOf(packs);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }
}
