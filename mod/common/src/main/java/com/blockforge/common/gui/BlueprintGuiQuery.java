package com.blockforge.common.gui;

public record BlueprintGuiQuery(
        String searchText,
        BlueprintSourceFilter sourceFilter,
        BlueprintWarningFilter warningFilter,
        BlueprintSortMode sortMode,
        int page,
        int pageSize
) {
    public static final int DEFAULT_PAGE_SIZE = 8;

    public BlueprintGuiQuery {
        searchText = searchText == null ? "" : searchText.trim();
        sourceFilter = sourceFilter == null ? BlueprintSourceFilter.ALL : sourceFilter;
        warningFilter = warningFilter == null ? BlueprintWarningFilter.ALL : warningFilter;
        sortMode = sortMode == null ? BlueprintSortMode.NAME_ASC : sortMode;
        pageSize = pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public static BlueprintGuiQuery firstPage() {
        return new BlueprintGuiQuery(
                "",
                BlueprintSourceFilter.ALL,
                BlueprintWarningFilter.ALL,
                BlueprintSortMode.NAME_ASC,
                0,
                DEFAULT_PAGE_SIZE
        );
    }
}
