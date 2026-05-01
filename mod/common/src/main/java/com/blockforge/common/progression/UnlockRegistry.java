package com.blockforge.common.progression;

import java.util.List;

public final class UnlockRegistry {
    public static final String ADVANCED_WAND_MODES = "advanced_wand_modes";
    public static final String BUILDER_STATION_ACCESS = "builder_station_access";
    public static final String MATERIAL_NETWORK_ACCESS = "material_network_access";
    public static final String HARD_CONTRACTS = "hard_contracts";
    public static final String MASTER_CONTRACTS = "master_contracts";
    public static final String LARGER_BUILD_LIMIT = "larger_build_limit";
    public static final String COSMETIC_BLOCKS_PLANNED = "cosmetic_blocks_planned";

    private UnlockRegistry() {
    }

    public static List<String> unlocksForLevel(int level) {
        if (level >= 5) {
            return List.of(ADVANCED_WAND_MODES, BUILDER_STATION_ACCESS, MATERIAL_NETWORK_ACCESS, HARD_CONTRACTS, MASTER_CONTRACTS, LARGER_BUILD_LIMIT, COSMETIC_BLOCKS_PLANNED);
        }
        if (level >= 3) {
            return List.of(ADVANCED_WAND_MODES, BUILDER_STATION_ACCESS, MATERIAL_NETWORK_ACCESS, HARD_CONTRACTS);
        }
        if (level >= 2) {
            return List.of(ADVANCED_WAND_MODES, BUILDER_STATION_ACCESS);
        }
        return List.of();
    }
}
