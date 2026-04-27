package com.blockforge.common.gui;

import java.util.List;

public record PagedBlueprintResult(
        List<BlueprintSummary> items,
        int page,
        int pageSize,
        int totalItems,
        int totalPages,
        boolean hasPrevious,
        boolean hasNext
) {
    public PagedBlueprintResult {
        items = items == null ? List.of() : List.copyOf(items);
    }
}
