package com.blockforge.connector.config;

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
}
