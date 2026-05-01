package com.blockforge.common.serverplay;

import java.util.List;
import java.util.Map;

public record DiagnosticsSnapshot(
        String version,
        String loader,
        String minecraftVersion,
        Map<String, String> configSummary,
        int loadedBlueprints,
        int loadedPacks,
        int loadedSchematics,
        int loadedLitematics,
        int activeStations,
        int activeJobs,
        int auditEntries,
        int quotaDenials,
        int cooldownDenials,
        int materialNetworkSources,
        boolean protectionEnabled,
        boolean nearbyContainersEnabled,
        List<String> warningList
) {
    public DiagnosticsSnapshot {
        version = version == null ? "" : version;
        loader = loader == null ? "" : loader;
        minecraftVersion = minecraftVersion == null ? "" : minecraftVersion;
        configSummary = configSummary == null ? Map.of() : Map.copyOf(configSummary);
        warningList = warningList == null ? List.of() : List.copyOf(warningList);
    }
}
