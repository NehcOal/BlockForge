package com.blockforge.common.material.source;

import java.util.List;

public record MaterialSourceScanResult(
        List<MaterialSourceRef> sources,
        int scannedBlocks,
        int foundContainers,
        List<String> warnings
) {
    public MaterialSourceScanResult {
        sources = sources == null ? List.of() : List.copyOf(sources);
        scannedBlocks = Math.max(0, scannedBlocks);
        foundContainers = Math.max(0, foundContainers);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }
}
