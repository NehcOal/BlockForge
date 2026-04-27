package com.blockforge.connector.config;

import com.blockforge.common.material.source.MaterialSourceConfig;
import com.blockforge.common.material.source.MaterialSourcePriority;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class BlockForgeConfig {
    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.IntValue MAX_BLOCKS_PER_BUILD;
    private static final ModConfigSpec.IntValue WAND_COOLDOWN_SECONDS;
    private static final ModConfigSpec.IntValue MAX_UNDO_SNAPSHOTS_PER_PLAYER;
    private static final ModConfigSpec.BooleanValue ALLOW_REPLACE_NON_AIR;
    private static final ModConfigSpec.BooleanValue PROTECT_BLOCK_ENTITIES;
    private static final ModConfigSpec.BooleanValue REQUIRE_MATERIALS_IN_SURVIVAL;
    private static final ModConfigSpec.BooleanValue CREATIVE_MODE_BYPASSES_MATERIALS;
    private static final ModConfigSpec.BooleanValue ALLOW_BUILD_IN_ADVENTURE_MODE;
    private static final ModConfigSpec.BooleanValue ALLOW_BUILD_IN_SPECTATOR_MODE;
    private static final ModConfigSpec.ConfigValue<String> MATERIAL_COST_MODE;
    private static final ModConfigSpec.BooleanValue ENABLE_NEARBY_CONTAINERS;
    private static final ModConfigSpec.IntValue NEARBY_CONTAINER_SEARCH_RADIUS;
    private static final ModConfigSpec.IntValue NEARBY_CONTAINER_MAX_SCANNED;
    private static final ModConfigSpec.EnumValue<MaterialSourcePriority> MATERIAL_SOURCE_PRIORITY;
    private static final ModConfigSpec.BooleanValue RETURN_REFUNDS_TO_ORIGINAL_SOURCE;
    private static final ModConfigSpec.BooleanValue ALLOW_PARTIAL_FROM_CONTAINERS;
    private static final ModConfigSpec.BooleanValue ENABLE_PROTECTION_REGIONS;
    private static final ModConfigSpec.BooleanValue REQUIRE_PERMISSIONS;
    private static final ModConfigSpec.IntValue PERMISSION_FALLBACK_BUILD_LEVEL;
    private static final ModConfigSpec.IntValue PERMISSION_FALLBACK_ADMIN_LEVEL;
    private static final ModConfigSpec.BooleanValue ENFORCE_PROTECTION_ON_UNDO;
    private static final ModConfigSpec.BooleanValue HIDE_INACCESSIBLE_CONTAINERS_FROM_SOURCES_SCAN;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("build");
        MAX_BLOCKS_PER_BUILD = builder
                .comment("Maximum number of blocks allowed in a single BlockForge build.")
                .defineInRange("maxBlocksPerBuild", 10_000, 1, 1_000_000);
        ALLOW_REPLACE_NON_AIR = builder
                .comment("When false, BlockForge only places into air or replaceable blocks.")
                .define("allowReplaceNonAir", true);
        PROTECT_BLOCK_ENTITIES = builder
                .comment("When true, BlockForge skips positions containing block entities such as chests.")
                .define("protectBlockEntities", true);
        ALLOW_BUILD_IN_ADVENTURE_MODE = builder
                .comment("Allow players in adventure mode to build with BlockForge.")
                .define("allowBuildInAdventureMode", false);
        ALLOW_BUILD_IN_SPECTATOR_MODE = builder
                .comment("Allow players in spectator mode to build with BlockForge.")
                .define("allowBuildInSpectatorMode", false);
        builder.pop();

        builder.push("wand");
        WAND_COOLDOWN_SECONDS = builder
                .comment("Builder Wand cooldown in seconds.")
                .defineInRange("wandCooldownSeconds", 2, 0, 3600);
        builder.pop();

        builder.push("undo");
        MAX_UNDO_SNAPSHOTS_PER_PLAYER = builder
                .comment("Maximum in-memory undo snapshots stored per player.")
                .defineInRange("maxUndoSnapshotsPerPlayer", 5, 0, 100);
        builder.pop();

        builder.push("materials");
        REQUIRE_MATERIALS_IN_SURVIVAL = builder
                .comment("Require survival players to have materials before building.")
                .define("requireMaterialsInSurvival", true);
        CREATIVE_MODE_BYPASSES_MATERIALS = builder
                .comment("Creative players bypass material checks and do not consume materials.")
                .define("creativeModeBypassesMaterials", true);
        MATERIAL_COST_MODE = builder
                .comment("Material cost mode. v1.0 RC supports simple: one placed block costs one block.asItem().")
                .define("materialCostMode", "simple");
        ENABLE_NEARBY_CONTAINERS = builder
                .comment("Enable nearby container material sourcing for survival builds. Disabled by default.")
                .define("enableNearbyContainers", MaterialSourceConfig.DEFAULT_ENABLE_NEARBY_CONTAINERS);
        NEARBY_CONTAINER_SEARCH_RADIUS = builder
                .comment("Radius around the build origin to scan for loaded nearby containers.")
                .defineInRange("nearbyContainerSearchRadius", MaterialSourceConfig.DEFAULT_SEARCH_RADIUS, 1, 32);
        NEARBY_CONTAINER_MAX_SCANNED = builder
                .comment("Maximum number of nearby containers scanned for one build.")
                .defineInRange("nearbyContainerMaxScanned", MaterialSourceConfig.DEFAULT_MAX_CONTAINERS_SCANNED, 1, 512);
        MATERIAL_SOURCE_PRIORITY = builder
                .comment("Material source priority: PLAYER_FIRST, CONTAINER_FIRST, PLAYER_ONLY, or CONTAINER_ONLY.")
                .defineEnum("materialSourcePriority", MaterialSourceConfig.DEFAULT_PRIORITY);
        RETURN_REFUNDS_TO_ORIGINAL_SOURCE = builder
                .comment("When true, undo tries to refund nearby-container materials to their original container first.")
                .define("returnRefundsToOriginalSource", MaterialSourceConfig.DEFAULT_RETURN_REFUNDS_TO_ORIGINAL_SOURCE);
        ALLOW_PARTIAL_FROM_CONTAINERS = builder
                .comment("Allow one material requirement to be split between player inventory and nearby containers.")
                .define("allowPartialFromContainers", MaterialSourceConfig.DEFAULT_ALLOW_PARTIAL_FROM_CONTAINERS);
        builder.pop();

        builder.push("security");
        ENABLE_PROTECTION_REGIONS = builder
                .comment("Enable BlockForge built-in protection regions from config/blockforge/protection-regions.json.")
                .define("enableProtectionRegions", true);
        REQUIRE_PERMISSIONS = builder
                .comment("When true, BlockForge permission checks require the configured permission provider or OP fallback.")
                .define("requirePermissions", false);
        PERMISSION_FALLBACK_BUILD_LEVEL = builder
                .comment("Vanilla fallback permission level for build-related permission checks.")
                .defineInRange("permissionFallbackBuildLevel", 0, 0, 4);
        PERMISSION_FALLBACK_ADMIN_LEVEL = builder
                .comment("Vanilla fallback permission level for admin permission checks.")
                .defineInRange("permissionFallbackAdminLevel", 2, 0, 4);
        ENFORCE_PROTECTION_ON_UNDO = builder
                .comment("When true, undo restore also respects current protection regions.")
                .define("enforceProtectionOnUndo", false);
        HIDE_INACCESSIBLE_CONTAINERS_FROM_SOURCES_SCAN = builder
                .comment("Hide containers denied by protection regions from sources scan output.")
                .define("hideInaccessibleContainersFromSourcesScan", true);
        builder.pop();

        SPEC = builder.build();
    }

    private BlockForgeConfig() {
    }

    public static int maxBlocksPerBuild() {
        return MAX_BLOCKS_PER_BUILD.get();
    }

    public static int wandCooldownSeconds() {
        return WAND_COOLDOWN_SECONDS.get();
    }

    public static long wandCooldownTicks() {
        return wandCooldownSeconds() * 20L;
    }

    public static int maxUndoSnapshotsPerPlayer() {
        return MAX_UNDO_SNAPSHOTS_PER_PLAYER.get();
    }

    public static boolean allowReplaceNonAir() {
        return ALLOW_REPLACE_NON_AIR.get();
    }

    public static boolean protectBlockEntities() {
        return PROTECT_BLOCK_ENTITIES.get();
    }

    public static boolean requireMaterialsInSurvival() {
        return REQUIRE_MATERIALS_IN_SURVIVAL.get();
    }

    public static boolean creativeModeBypassesMaterials() {
        return CREATIVE_MODE_BYPASSES_MATERIALS.get();
    }

    public static boolean allowBuildInAdventureMode() {
        return ALLOW_BUILD_IN_ADVENTURE_MODE.get();
    }

    public static boolean allowBuildInSpectatorMode() {
        return ALLOW_BUILD_IN_SPECTATOR_MODE.get();
    }

    public static String materialCostMode() {
        return MATERIAL_COST_MODE.get();
    }

    public static boolean enableNearbyContainers() {
        return ENABLE_NEARBY_CONTAINERS.get();
    }

    public static int nearbyContainerSearchRadius() {
        return NEARBY_CONTAINER_SEARCH_RADIUS.get();
    }

    public static int nearbyContainerMaxScanned() {
        return NEARBY_CONTAINER_MAX_SCANNED.get();
    }

    public static MaterialSourcePriority materialSourcePriority() {
        return MATERIAL_SOURCE_PRIORITY.get();
    }

    public static boolean returnRefundsToOriginalSource() {
        return RETURN_REFUNDS_TO_ORIGINAL_SOURCE.get();
    }

    public static boolean allowPartialFromContainers() {
        return ALLOW_PARTIAL_FROM_CONTAINERS.get();
    }

    public static MaterialSourceConfig materialSourceConfig() {
        return new MaterialSourceConfig(
                enableNearbyContainers(),
                nearbyContainerSearchRadius(),
                materialSourcePriority(),
                allowPartialFromContainers(),
                returnRefundsToOriginalSource(),
                nearbyContainerMaxScanned()
        );
    }

    public static boolean enableProtectionRegions() {
        return ENABLE_PROTECTION_REGIONS.get();
    }

    public static boolean requirePermissions() {
        return REQUIRE_PERMISSIONS.get();
    }

    public static int permissionFallbackBuildLevel() {
        return PERMISSION_FALLBACK_BUILD_LEVEL.get();
    }

    public static int permissionFallbackAdminLevel() {
        return PERMISSION_FALLBACK_ADMIN_LEVEL.get();
    }

    public static boolean enforceProtectionOnUndo() {
        return ENFORCE_PROTECTION_ON_UNDO.get();
    }

    public static boolean hideInaccessibleContainersFromSourcesScan() {
        return HIDE_INACCESSIBLE_CONTAINERS_FROM_SOURCES_SCAN.get();
    }
}
