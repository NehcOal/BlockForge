package com.blockforge.connector.buildstation;

import com.blockforge.common.buildstation.StationMaterialResolution;
import com.blockforge.common.buildstation.StationMaterialResolver;
import com.blockforge.common.material.MaterialReport;
import com.blockforge.common.material.MaterialRequirement;
import com.blockforge.common.material.source.MaterialSourceConfig;
import com.blockforge.common.material.source.MaterialSourceItemEntry;
import com.blockforge.common.material.source.MaterialSourcePriority;
import com.blockforge.common.material.source.MaterialSourceRef;
import com.blockforge.common.material.source.MaterialSourceType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StationMaterialResolverTest {
    @Test
    void requiresCacheWhenConfigured() {
        StationMaterialResolution resolution = StationMaterialResolver.resolve(
                report(4),
                config(MaterialSourcePriority.PLAYER_FIRST),
                List.of(entry(MaterialSourceType.PLAYER_INVENTORY, 4)),
                true,
                true
        );

        assertFalse(resolution.allowed());
        assertTrue(resolution.message().contains("Material Cache"));
    }

    @Test
    void allowsLinkedCacheAndRejectsOwnerInventoryWhenDisabled() {
        StationMaterialResolution cacheResolution = StationMaterialResolver.resolve(
                report(4),
                config(MaterialSourcePriority.CACHE_FIRST),
                List.of(entry(MaterialSourceType.MATERIAL_LINKED_CACHE, 4)),
                true,
                false
        );
        StationMaterialResolution playerResolution = StationMaterialResolver.resolve(
                report(4),
                config(MaterialSourcePriority.PLAYER_FIRST),
                List.of(entry(MaterialSourceType.PLAYER_INVENTORY, 4)),
                false,
                false
        );

        assertTrue(cacheResolution.allowed());
        assertFalse(playerResolution.allowed());
    }

    private static MaterialReport report(int required) {
        return new MaterialReport(
                "tiny",
                required,
                required,
                0,
                false,
                List.of(new MaterialRequirement("stone", "minecraft:stone", "minecraft:stone", required, 0, required))
        );
    }

    private static MaterialSourceConfig config(MaterialSourcePriority priority) {
        return new MaterialSourceConfig(true, 8, priority, true, true, 64);
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
