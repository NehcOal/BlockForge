package com.blockforge.common.material.source;

public record MaterialSourceConfig(
        boolean enableNearbyContainers,
        int searchRadius,
        MaterialSourcePriority priority,
        boolean allowPartialFromContainers,
        boolean returnRefundsToOriginalSource,
        int maxContainersScanned
) {
    public static final boolean DEFAULT_ENABLE_NEARBY_CONTAINERS = false;
    public static final int DEFAULT_SEARCH_RADIUS = 8;
    public static final MaterialSourcePriority DEFAULT_PRIORITY = MaterialSourcePriority.PLAYER_FIRST;
    public static final boolean DEFAULT_ALLOW_PARTIAL_FROM_CONTAINERS = true;
    public static final boolean DEFAULT_RETURN_REFUNDS_TO_ORIGINAL_SOURCE = true;
    public static final int DEFAULT_MAX_CONTAINERS_SCANNED = 64;

    public MaterialSourceConfig {
        priority = priority == null ? DEFAULT_PRIORITY : priority;
        searchRadius = Math.max(0, searchRadius);
        maxContainersScanned = Math.max(0, maxContainersScanned);
    }

    public static MaterialSourceConfig defaults() {
        return new MaterialSourceConfig(
                DEFAULT_ENABLE_NEARBY_CONTAINERS,
                DEFAULT_SEARCH_RADIUS,
                DEFAULT_PRIORITY,
                DEFAULT_ALLOW_PARTIAL_FROM_CONTAINERS,
                DEFAULT_RETURN_REFUNDS_TO_ORIGINAL_SOURCE,
                DEFAULT_MAX_CONTAINERS_SCANNED
        );
    }
}
