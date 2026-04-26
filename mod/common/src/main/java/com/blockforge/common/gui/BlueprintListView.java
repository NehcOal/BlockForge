package com.blockforge.common.gui;

import java.util.List;

public record BlueprintListView(
        List<BlueprintSummary> blueprints,
        String selectedBlueprintId,
        int rotationDegrees
) {
    public BlueprintListView {
        blueprints = blueprints == null ? List.of() : List.copyOf(blueprints);
        selectedBlueprintId = selectedBlueprintId == null ? "" : selectedBlueprintId;
    }
}
