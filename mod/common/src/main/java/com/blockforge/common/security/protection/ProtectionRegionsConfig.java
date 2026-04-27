package com.blockforge.common.security.protection;

import java.util.List;

public record ProtectionRegionsConfig(int schemaVersion, List<BlockForgeProtectionRegion> regions, List<String> warnings) {
    public ProtectionRegionsConfig {
        regions = regions == null ? List.of() : List.copyOf(regions);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }

    public static ProtectionRegionsConfig empty() {
        return new ProtectionRegionsConfig(1, List.of(), List.of());
    }
}
