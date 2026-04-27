package com.blockforge.forge.client.gui;

import com.blockforge.common.gui.BlueprintListView;
import com.blockforge.common.gui.BlueprintSummary;

import java.util.List;
import java.util.Optional;

public final class ForgeBlueprintClientCache {
    private static List<BlueprintSummary> blueprints = List.of();
    private static String selectedBlueprintId = "";
    private static int rotationDegrees;
    private static boolean loading;
    private static String message = "";
    private static int page;
    private static int pageSize = 8;
    private static int totalItems;
    private static int totalPages;
    private static boolean hasPrevious;
    private static boolean hasNext;

    private ForgeBlueprintClientCache() {
    }

    public static void beginLoading() {
        loading = true;
        message = "";
    }

    public static void apply(BlueprintListView view) {
        blueprints = view.blueprints();
        selectedBlueprintId = view.selectedBlueprintId();
        rotationDegrees = view.rotationDegrees();
        page = view.page();
        pageSize = view.pageSize();
        totalItems = view.totalItems();
        totalPages = view.totalPages();
        hasPrevious = view.hasPrevious();
        hasNext = view.hasNext();
        loading = false;

        if (selectedBlueprintId.isBlank() && !blueprints.isEmpty()) {
            selectedBlueprintId = blueprints.getFirst().id();
        }
    }

    public static List<BlueprintSummary> blueprints() {
        return blueprints;
    }

    public static boolean loading() {
        return loading;
    }

    public static String message() {
        return message;
    }

    public static void setMessage(String newMessage) {
        loading = false;
        message = newMessage == null ? "" : newMessage;
    }

    public static String selectedBlueprintId() {
        return selectedBlueprintId;
    }

    public static int rotationDegrees() {
        return rotationDegrees;
    }

    public static void selectLocally(String blueprintId) {
        selectedBlueprintId = blueprintId == null ? "" : blueprintId;
    }

    public static void setRotationDegrees(int degrees) {
        rotationDegrees = degrees;
    }

    public static void setSelected(String blueprintId, int degrees) {
        selectedBlueprintId = blueprintId == null ? "" : blueprintId;
        rotationDegrees = degrees;
        loading = false;
    }

    public static Optional<BlueprintSummary> selectedBlueprint() {
        return blueprints.stream()
                .filter(summary -> summary.id().equals(selectedBlueprintId))
                .findFirst();
    }

    public static int page() { return page; }
    public static int pageSize() { return pageSize; }
    public static int totalItems() { return totalItems; }
    public static int totalPages() { return totalPages; }
    public static boolean hasPrevious() { return hasPrevious; }
    public static boolean hasNext() { return hasNext; }
}
