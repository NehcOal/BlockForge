package com.blockforge.common.gameplaygui;

import java.util.List;

public record LoaderGuiParityReport(
        String loader,
        LoaderGuiParityStatus materialCacheGui,
        LoaderGuiParityStatus builderStationGui,
        LoaderGuiParityStatus constructionCoreGui,
        LoaderGuiParityStatus stationWorldPlacement,
        LoaderGuiParityStatus adminRollback,
        LoaderGuiParityStatus advancedCommands,
        LoaderGuiParityStatus auditPersistence,
        LoaderGuiParityStatus diagnosticsExport,
        boolean dedicatedServerSafe,
        List<String> notes
) {
    public LoaderGuiParityReport {
        loader = loader == null || loader.isBlank() ? "unknown" : loader;
        materialCacheGui = materialCacheGui == null ? LoaderGuiParityStatus.PENDING : materialCacheGui;
        builderStationGui = builderStationGui == null ? LoaderGuiParityStatus.PENDING : builderStationGui;
        constructionCoreGui = constructionCoreGui == null ? LoaderGuiParityStatus.PENDING : constructionCoreGui;
        stationWorldPlacement = stationWorldPlacement == null ? LoaderGuiParityStatus.PENDING : stationWorldPlacement;
        adminRollback = adminRollback == null ? LoaderGuiParityStatus.PENDING : adminRollback;
        advancedCommands = advancedCommands == null ? LoaderGuiParityStatus.PENDING : advancedCommands;
        auditPersistence = auditPersistence == null ? LoaderGuiParityStatus.PENDING : auditPersistence;
        diagnosticsExport = diagnosticsExport == null ? LoaderGuiParityStatus.PENDING : diagnosticsExport;
        notes = notes == null ? List.of() : List.copyOf(notes);
    }

    public boolean hasBlockingGaps() {
        return isBlocking(materialCacheGui)
                || isBlocking(builderStationGui)
                || isBlocking(stationWorldPlacement)
                || isBlocking(adminRollback)
                || !dedicatedServerSafe;
    }

    public String releaseReadinessLabel() {
        return hasBlockingGaps() ? "draft-pr-only" : "beta-candidate";
    }

    private static boolean isBlocking(LoaderGuiParityStatus status) {
        return status == LoaderGuiParityStatus.PARTIAL || status == LoaderGuiParityStatus.PENDING;
    }
}
