package com.blockforge.common.material;

import java.util.List;

public record MaterialReport(
        String blueprintId,
        int totalBlocks,
        int totalRequiredItems,
        int totalAvailableItems,
        boolean enoughMaterials,
        List<MaterialRequirement> requirements
) {
    public MaterialReport {
        requirements = List.copyOf(requirements);
    }

    public int missingItemTypes() {
        return (int) requirements.stream().filter(requirement -> requirement.missing() > 0).count();
    }

    public int requiredItemTypes() {
        return requirements.size();
    }
}
