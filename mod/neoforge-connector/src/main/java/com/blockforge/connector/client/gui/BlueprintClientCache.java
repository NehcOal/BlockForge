package com.blockforge.connector.client.gui;

import com.blockforge.connector.network.payload.BlueprintSummary;
import com.blockforge.connector.network.payload.MaterialReportPayload;

import java.util.List;
import java.util.Optional;

public final class BlueprintClientCache {
    private static List<BlueprintSummary> blueprints = List.of();
    private static String selectedBlueprintId;
    private static int rotation;
    private static boolean loading;
    private static String error = "";
    private static MaterialReportPayload materialReport;

    private BlueprintClientCache() {
    }

    public static void beginLoading() {
        loading = true;
        error = "";
    }

    public static void setBlueprints(List<BlueprintSummary> summaries) {
        blueprints = List.copyOf(summaries);
        loading = false;
        error = "";

        if ((selectedBlueprintId == null || find(selectedBlueprintId).isEmpty()) && !blueprints.isEmpty()) {
            selectedBlueprintId = blueprints.getFirst().id();
        }
    }

    public static List<BlueprintSummary> blueprints() {
        return blueprints;
    }

    public static boolean loading() {
        return loading;
    }

    public static String error() {
        return error;
    }

    public static void setError(String message) {
        loading = false;
        error = message == null ? "" : message;
    }

    public static String selectedBlueprintId() {
        return selectedBlueprintId;
    }

    public static int rotation() {
        return rotation;
    }

    public static void selectLocally(String blueprintId) {
        selectedBlueprintId = blueprintId;
        materialReport = null;
    }

    public static void setRotation(int degrees) {
        rotation = degrees;
    }

    public static void setSelected(String blueprintId, int degrees) {
        selectedBlueprintId = blueprintId;
        rotation = degrees;
        loading = false;
        error = "";
    }

    public static MaterialReportPayload materialReport() {
        return materialReport;
    }

    public static void setMaterialReport(MaterialReportPayload report) {
        materialReport = report;
        loading = false;
        error = "";
    }

    public static Optional<BlueprintSummary> selectedBlueprint() {
        if (selectedBlueprintId == null) {
            return Optional.empty();
        }

        return find(selectedBlueprintId);
    }

    private static Optional<BlueprintSummary> find(String id) {
        return blueprints.stream().filter(summary -> summary.id().equals(id)).findFirst();
    }
}
