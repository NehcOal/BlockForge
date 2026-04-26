package com.blockforge.common.material.source;

import java.util.List;

public record MaterialSourceReport(
        String blueprintId,
        int totalRequiredItems,
        int totalAvailableItems,
        int totalMissingItems,
        boolean enoughMaterials,
        List<MaterialSourceItemEntry> entries,
        List<String> warnings
) {
    public MaterialSourceReport {
        entries = entries == null ? List.of() : List.copyOf(entries);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
        totalRequiredItems = Math.max(0, totalRequiredItems);
        totalAvailableItems = Math.max(0, totalAvailableItems);
        totalMissingItems = Math.max(0, totalMissingItems);
    }
}
