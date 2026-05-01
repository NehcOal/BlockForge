package com.blockforge.connector.material;

import com.blockforge.common.material.MaterialReport;
import com.blockforge.common.material.MaterialRequirement;
import com.blockforge.common.material.source.MaterialSourceConfig;
import com.blockforge.common.material.source.MaterialSourceItemEntry;
import com.blockforge.common.material.source.MaterialSourcePlanner;
import com.blockforge.common.material.source.MaterialSourcePriority;
import com.blockforge.common.material.source.MaterialSourceRef;
import com.blockforge.common.material.source.MaterialSourceReport;
import com.blockforge.common.material.source.MaterialSourceType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaterialSourcePlannerTest {
    @Test
    void playerFirstStillAllowsMaterialCacheSources() {
        MaterialSourceReport report = MaterialSourcePlanner.fromAvailableEntries(
                report(3),
                config(MaterialSourcePriority.PLAYER_FIRST, true),
                List.of(
                        entry(MaterialSourceType.PLAYER_INVENTORY, 1),
                        entry(MaterialSourceType.MATERIAL_CACHE, 2)
                )
        );

        assertTrue(report.enoughMaterials());
        assertEquals(0, report.totalMissingItems());
        assertEquals(2, report.entries().size());
    }

    @Test
    void cacheFirstRanksCacheBeforePlayerInventory() {
        MaterialSourceReport report = MaterialSourcePlanner.fromAvailableEntries(
                report(2),
                config(MaterialSourcePriority.CACHE_FIRST, true),
                List.of(
                        entry(MaterialSourceType.PLAYER_INVENTORY, 2),
                        entry(MaterialSourceType.MATERIAL_CACHE, 2)
                )
        );

        assertEquals(MaterialSourceType.MATERIAL_CACHE, report.entries().getFirst().source().type());
        assertEquals(1, report.entries().size());
    }

    @Test
    void playerOnlyExcludesMaterialCacheSources() {
        MaterialSourceReport report = MaterialSourcePlanner.fromAvailableEntries(
                report(2),
                config(MaterialSourcePriority.PLAYER_ONLY, true),
                List.of(entry(MaterialSourceType.MATERIAL_CACHE, 2))
        );

        assertEquals(2, report.totalMissingItems());
        assertEquals(0, report.entries().getFirst().reserved());
    }

    @Test
    void simplePlannerRespectsCacheOnlyByNotUsingPlayerInventory() {
        MaterialSourceReport report = MaterialSourcePlanner.plan(
                report(2),
                config(MaterialSourcePriority.CACHE_ONLY, true),
                List.of()
        );

        assertEquals(2, report.totalMissingItems());
        assertEquals(0, report.entries().getFirst().reserved());
    }

    @Test
    void simplePlannerRespectsPlayerOnlyAvailability() {
        MaterialSourceReport report = MaterialSourcePlanner.plan(
                report(2, 2),
                config(MaterialSourcePriority.PLAYER_ONLY, true),
                List.of()
        );

        assertTrue(report.enoughMaterials());
        assertEquals(2, report.entries().getFirst().reserved());
    }

    @Test
    void disabledNearbyContainersRemainExcludedInFirstModes() {
        MaterialSourceReport report = MaterialSourcePlanner.fromAvailableEntries(
                report(2),
                config(MaterialSourcePriority.CONTAINER_FIRST, false),
                List.of(entry(MaterialSourceType.NEARBY_CONTAINER, 2))
        );

        assertEquals(2, report.totalMissingItems());
        assertEquals(0, report.entries().getFirst().reserved());
    }

    private static MaterialReport report(int required) {
        return report(required, 0);
    }

    private static MaterialReport report(int required, int available) {
        return new MaterialReport(
                "tiny",
                required,
                required,
                available,
                available >= required,
                List.of(new MaterialRequirement("stone", "minecraft:stone", "minecraft:stone", required, available, Math.max(0, required - available)))
        );
    }

    private static MaterialSourceConfig config(MaterialSourcePriority priority, boolean enableContainers) {
        return new MaterialSourceConfig(enableContainers, 8, priority, true, true, 64);
    }

    private static MaterialSourceItemEntry entry(MaterialSourceType type, int available) {
        return new MaterialSourceItemEntry(
                "minecraft:stone",
                0,
                available,
                0,
                0,
                new MaterialSourceRef(type, type.name().toLowerCase(), type.name(), "minecraft:overworld", 0, 64, 0)
        );
    }
}
