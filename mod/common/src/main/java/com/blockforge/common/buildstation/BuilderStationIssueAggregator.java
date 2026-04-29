package com.blockforge.common.buildstation;

import com.blockforge.common.buildplan.BuildIssue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class BuilderStationIssueAggregator {
    private BuilderStationIssueAggregator() {
    }

    public static Map<String, Long> countByType(List<BuildIssue> issues) {
        List<BuildIssue> safeIssues = issues == null ? List.of() : issues;
        return safeIssues.stream()
                .collect(Collectors.groupingBy(BuildIssue::type, Collectors.counting()));
    }

    public static long countSeverity(List<BuildIssue> issues, String severity) {
        String resolved = severity == null ? "" : severity;
        List<BuildIssue> safeIssues = issues == null ? List.of() : issues;
        return safeIssues.stream()
                .filter(issue -> issue.severity().equalsIgnoreCase(resolved))
                .count();
    }
}
