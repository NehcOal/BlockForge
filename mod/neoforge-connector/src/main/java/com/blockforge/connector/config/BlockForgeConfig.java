package com.blockforge.connector.config;

/**
 * Centralized safety limits for the Connector MVP.
 *
 * <p>v0.6.1 keeps these values in code to avoid adding config-system risk before
 * in-game validation. A NeoForge common config file is planned for a later pass.</p>
 */
public final class BlockForgeConfig {
    private static final int MAX_BLOCKS_PER_BUILD = 10_000;
    private static final int WAND_COOLDOWN_SECONDS = 2;
    private static final int MAX_UNDO_SNAPSHOTS_PER_PLAYER = 5;
    private static final boolean ALLOW_REPLACE_NON_AIR = true;
    private static final boolean PROTECT_BLOCK_ENTITIES = true;

    private BlockForgeConfig() {
    }

    public static int maxBlocksPerBuild() {
        return MAX_BLOCKS_PER_BUILD;
    }

    public static int wandCooldownSeconds() {
        return WAND_COOLDOWN_SECONDS;
    }

    public static long wandCooldownTicks() {
        return WAND_COOLDOWN_SECONDS * 20L;
    }

    public static int maxUndoSnapshotsPerPlayer() {
        return MAX_UNDO_SNAPSHOTS_PER_PLAYER;
    }

    public static boolean allowReplaceNonAir() {
        return ALLOW_REPLACE_NON_AIR;
    }

    public static boolean protectBlockEntities() {
        return PROTECT_BLOCK_ENTITIES;
    }
}
