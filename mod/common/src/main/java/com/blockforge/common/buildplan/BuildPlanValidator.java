package com.blockforge.common.buildplan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BuildPlanValidator {
    private BuildPlanValidator() {
    }

    public static List<BuildIssue> validate(BuildPlan plan, int minY, int maxY) {
        return validateLayers(plan.layers(), minY, maxY);
    }

    public static List<BuildIssue> validateLayers(List<BuildLayer> layers, int minY, int maxY) {
        List<BuildIssue> issues = new ArrayList<>();
        Set<String> coordinates = new HashSet<>();
        for (BuildLayer layer : layers) {
            for (BuildStep step : layer.steps()) {
                String key = step.x() + "," + step.y() + "," + step.z();
                if (!coordinates.add(key)) {
                    issues.add(new BuildIssue("duplicate_coordinate", "error", step.x(), step.y(), step.z(), "Duplicate planned coordinate " + key + ".", "Keep only one step for each world coordinate."));
                }
                if (step.y() < minY || step.y() > maxY) {
                    issues.add(new BuildIssue("out_of_world", "error", step.x(), step.y(), step.z(), "Planned block is outside world height.", "Move the plan base position or reduce blueprint height."));
                }
                if (step.blockId().isBlank()) {
                    issues.add(new BuildIssue("missing_palette_reference", "error", step.x(), step.y(), step.z(), "Step references a missing palette entry: " + step.paletteKey() + ".", "Fix the blueprint palette before building."));
                }
            }
        }
        return issues;
    }
}
