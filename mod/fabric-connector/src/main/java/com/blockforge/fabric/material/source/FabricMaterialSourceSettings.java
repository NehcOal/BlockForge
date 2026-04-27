package com.blockforge.fabric.material.source;

import com.blockforge.common.material.source.MaterialSourceConfig;
import com.blockforge.common.material.source.MaterialSourcePriority;

public final class FabricMaterialSourceSettings {
    public static final boolean DEFAULT_ENABLE_NEARBY_CONTAINERS = false;
    public static final int DEFAULT_NEARBY_CONTAINER_SEARCH_RADIUS = 8;
    public static final int DEFAULT_NEARBY_CONTAINER_MAX_SCANNED = 64;
    public static final MaterialSourcePriority DEFAULT_MATERIAL_SOURCE_PRIORITY = MaterialSourcePriority.PLAYER_FIRST;
    public static final boolean DEFAULT_RETURN_REFUNDS_TO_ORIGINAL_SOURCE = true;
    public static final boolean DEFAULT_ALLOW_PARTIAL_FROM_CONTAINERS = true;

    private static volatile boolean enableNearbyContainers = DEFAULT_ENABLE_NEARBY_CONTAINERS;
    private static volatile int nearbyContainerSearchRadius = DEFAULT_NEARBY_CONTAINER_SEARCH_RADIUS;
    private static volatile int nearbyContainerMaxScanned = DEFAULT_NEARBY_CONTAINER_MAX_SCANNED;
    private static volatile MaterialSourcePriority materialSourcePriority = DEFAULT_MATERIAL_SOURCE_PRIORITY;
    private static volatile boolean returnRefundsToOriginalSource = DEFAULT_RETURN_REFUNDS_TO_ORIGINAL_SOURCE;
    private static volatile boolean allowPartialFromContainers = DEFAULT_ALLOW_PARTIAL_FROM_CONTAINERS;

    private FabricMaterialSourceSettings() {
    }

    public static MaterialSourceConfig config() {
        return new MaterialSourceConfig(
                enableNearbyContainers,
                nearbyContainerSearchRadius,
                materialSourcePriority,
                allowPartialFromContainers,
                returnRefundsToOriginalSource,
                nearbyContainerMaxScanned
        );
    }

    public static boolean enableNearbyContainers() {
        return enableNearbyContainers;
    }

    public static void setEnableNearbyContainers(boolean enabled) {
        enableNearbyContainers = enabled;
    }

    public static int nearbyContainerSearchRadius() {
        return nearbyContainerSearchRadius;
    }

    public static void setNearbyContainerSearchRadius(int radius) {
        nearbyContainerSearchRadius = Math.max(1, Math.min(32, radius));
    }

    public static int nearbyContainerMaxScanned() {
        return nearbyContainerMaxScanned;
    }

    public static MaterialSourcePriority materialSourcePriority() {
        return materialSourcePriority;
    }

    public static void setMaterialSourcePriority(MaterialSourcePriority priority) {
        materialSourcePriority = priority == null ? DEFAULT_MATERIAL_SOURCE_PRIORITY : priority;
    }

    public static boolean returnRefundsToOriginalSource() {
        return returnRefundsToOriginalSource;
    }

    public static boolean allowPartialFromContainers() {
        return allowPartialFromContainers;
    }
}
