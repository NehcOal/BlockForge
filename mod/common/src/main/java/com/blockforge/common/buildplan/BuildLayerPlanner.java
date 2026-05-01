package com.blockforge.common.buildplan;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class BuildLayerPlanner {
    private BuildLayerPlanner() {
    }

    public static List<BuildLayer> planLayers(List<BuildStep> steps) {
        Map<Integer, List<BuildStep>> byY = steps.stream()
                .sorted(Comparator.comparingInt(BuildStep::y)
                        .thenComparingInt(BuildStep::x)
                        .thenComparingInt(BuildStep::z))
                .collect(Collectors.groupingBy(BuildStep::y, TreeMap::new, Collectors.toList()));
        return byY.entrySet()
                .stream()
                .map(entry -> new BuildLayer(entry.getKey(), entry.getValue(), entry.getValue().size()))
                .toList();
    }
}
