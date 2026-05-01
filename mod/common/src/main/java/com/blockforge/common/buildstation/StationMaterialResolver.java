package com.blockforge.common.buildstation;

import com.blockforge.common.material.MaterialReport;
import com.blockforge.common.material.source.MaterialSourceConfig;
import com.blockforge.common.material.source.MaterialSourceItemEntry;
import com.blockforge.common.material.source.MaterialSourcePlanner;
import com.blockforge.common.material.source.MaterialSourceReport;
import com.blockforge.common.material.source.MaterialSourceType;

import java.util.List;

public final class StationMaterialResolver {
    private StationMaterialResolver() {
    }

    public static StationMaterialResolution resolve(
            MaterialReport materialReport,
            MaterialSourceConfig config,
            List<MaterialSourceItemEntry> availableEntries,
            boolean requireMaterialCacheForStation,
            boolean allowOwnerInventoryForStation
    ) {
        MaterialSourceReport report = MaterialSourcePlanner.fromAvailableEntries(materialReport, config, availableEntries);
        boolean hasCacheSource = report.entries().stream()
                .anyMatch(entry -> entry.source() != null && isCacheLike(entry.source().type()) && entry.reserved() > 0);
        boolean hasPlayerSource = report.entries().stream()
                .anyMatch(entry -> entry.source() != null && entry.source().type() == MaterialSourceType.PLAYER_INVENTORY && entry.reserved() > 0);

        if (requireMaterialCacheForStation && !hasCacheSource) {
            return new StationMaterialResolution(false, report, "Station requires a bound Material Cache source.");
        }
        if (!allowOwnerInventoryForStation && hasPlayerSource) {
            return new StationMaterialResolution(false, report, "Station owner inventory is disabled as a material source.");
        }
        if (!report.enoughMaterials()) {
            return new StationMaterialResolution(false, report, "Required station materials are missing.");
        }
        return new StationMaterialResolution(true, report, "Station materials are available.");
    }

    private static boolean isCacheLike(MaterialSourceType type) {
        return type == MaterialSourceType.MATERIAL_CACHE
                || type == MaterialSourceType.MATERIAL_LINK
                || type == MaterialSourceType.MATERIAL_LINKED_CACHE
                || type == MaterialSourceType.BUILDER_STATION;
    }
}
