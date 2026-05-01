package com.blockforge.common.house;

import java.util.List;

public record HouseQualityReport(
        int total,
        int enclosure,
        int roof,
        int entrance,
        int windows,
        int interior,
        int buildability,
        int materials,
        List<String> warnings,
        List<String> suggestions
) {
    public HouseQualityReport {
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
        suggestions = suggestions == null ? List.of() : List.copyOf(suggestions);
    }
}
