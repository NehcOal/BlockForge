package com.blockforge.common.gui;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class BlueprintGuiQueryService {
    private BlueprintGuiQueryService() {
    }

    public static PagedBlueprintResult query(List<BlueprintSummary> input, BlueprintGuiQuery query) {
        List<BlueprintSummary> source = input == null ? List.of() : input;
        BlueprintGuiQuery safeQuery = query == null ? BlueprintGuiQuery.firstPage() : query;
        List<BlueprintSummary> filtered = source.stream()
                .filter(summary -> matchesSource(summary, safeQuery.sourceFilter()))
                .filter(summary -> matchesWarning(summary, safeQuery.warningFilter()))
                .filter(summary -> matchesSearch(summary, safeQuery.searchText()))
                .sorted(comparator(safeQuery.sortMode()))
                .toList();

        int totalItems = filtered.size();
        int totalPages = totalItems == 0 ? 0 : (int) Math.ceil((double) totalItems / safeQuery.pageSize());
        int normalizedPage = normalizePage(safeQuery.page(), totalPages);
        int from = totalItems == 0 ? 0 : normalizedPage * safeQuery.pageSize();
        int to = Math.min(from + safeQuery.pageSize(), totalItems);
        List<BlueprintSummary> items = totalItems == 0 ? List.of() : filtered.subList(from, to);

        return new PagedBlueprintResult(
                items,
                normalizedPage,
                safeQuery.pageSize(),
                totalItems,
                totalPages,
                normalizedPage > 0,
                totalPages > 0 && normalizedPage < totalPages - 1
        );
    }

    private static boolean matchesSource(BlueprintSummary summary, BlueprintSourceFilter filter) {
        if (filter == BlueprintSourceFilter.ALL) {
            return true;
        }
        return summary.sourceType().equalsIgnoreCase(filter.name());
    }

    private static boolean matchesWarning(BlueprintSummary summary, BlueprintWarningFilter filter) {
        return switch (filter) {
            case ALL -> true;
            case WITH_WARNINGS -> summary.warningCount() > 0;
            case WITHOUT_WARNINGS -> summary.warningCount() == 0;
        };
    }

    private static boolean matchesSearch(BlueprintSummary summary, String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return true;
        }
        String query = searchText.trim().toLowerCase(Locale.ROOT);
        return contains(summary.id(), query)
                || contains(summary.name(), query)
                || contains(summary.sourceId(), query)
                || summary.tags().stream().anyMatch(tag -> contains(tag, query));
    }

    private static boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private static Comparator<BlueprintSummary> comparator(BlueprintSortMode mode) {
        Comparator<BlueprintSummary> byName = Comparator
                .comparing(BlueprintSummary::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(BlueprintSummary::id, String.CASE_INSENSITIVE_ORDER);
        return switch (mode) {
            case NAME_ASC -> byName;
            case NAME_DESC -> byName.reversed();
            case BLOCKS_ASC -> Comparator.comparingInt(BlueprintSummary::blockCount).thenComparing(byName);
            case BLOCKS_DESC -> Comparator.comparingInt(BlueprintSummary::blockCount).reversed().thenComparing(byName);
            case SOURCE_ASC -> Comparator.comparing(BlueprintSummary::sourceType, String.CASE_INSENSITIVE_ORDER).thenComparing(byName);
            case SOURCE_DESC -> Comparator.comparing(BlueprintSummary::sourceType, String.CASE_INSENSITIVE_ORDER).reversed().thenComparing(byName);
        };
    }

    private static int normalizePage(int page, int totalPages) {
        if (totalPages <= 0 || page < 0) {
            return 0;
        }
        return Math.min(page, totalPages - 1);
    }
}
