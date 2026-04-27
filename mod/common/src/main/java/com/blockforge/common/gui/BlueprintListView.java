package com.blockforge.common.gui;

import java.util.List;

public record BlueprintListView(
        List<BlueprintSummary> blueprints,
        int page,
        int pageSize,
        int totalItems,
        int totalPages,
        boolean hasPrevious,
        boolean hasNext,
        String selectedBlueprintId,
        int rotationDegrees
) {
    public BlueprintListView {
        blueprints = blueprints == null ? List.of() : List.copyOf(blueprints);
        selectedBlueprintId = selectedBlueprintId == null ? "" : selectedBlueprintId;
    }

    public BlueprintListView(List<BlueprintSummary> blueprints, String selectedBlueprintId, int rotationDegrees) {
        this(
                blueprints,
                0,
                BlueprintGuiQuery.DEFAULT_PAGE_SIZE,
                blueprints == null ? 0 : blueprints.size(),
                blueprints == null || blueprints.isEmpty() ? 0 : 1,
                false,
                false,
                selectedBlueprintId,
                rotationDegrees
        );
    }

    public static BlueprintListView from(PagedBlueprintResult result, String selectedBlueprintId, int rotationDegrees) {
        return new BlueprintListView(
                result.items(),
                result.page(),
                result.pageSize(),
                result.totalItems(),
                result.totalPages(),
                result.hasPrevious(),
                result.hasNext(),
                selectedBlueprintId,
                rotationDegrees
        );
    }
}
