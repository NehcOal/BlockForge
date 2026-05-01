package com.blockforge.common.contracts;

import java.util.List;

public final class ContractTemplates {
    private ContractTemplates() {
    }

    public static List<BuildContract> templates() {
        return List.of(
                template("starter_cottage", "Build a Starter Cottage", ContractType.STARTER_HOME, ContractDifficulty.EASY, 24, 450, true, true, true, true, List.of("minecraft:oak_planks"), List.of()),
                template("watchtower", "Build a Watchtower", ContractType.WATCHTOWER, ContractDifficulty.NORMAL, 80, 900, true, true, false, true, List.of("minecraft:cobblestone"), List.of()),
                template("stone_bridge", "Build a Stone Bridge", ContractType.BRIDGE, ContractDifficulty.NORMAL, 40, 700, false, false, false, true, List.of("minecraft:stone"), List.of()),
                template("storage_shed", "Build a Storage Shed", ContractType.STORAGE, ContractDifficulty.EASY, 30, 500, true, true, false, true, List.of("minecraft:chest"), List.of()),
                template("farm_hut", "Build a Farm Hut", ContractType.FARM_STRUCTURE, ContractDifficulty.EASY, 28, 520, true, true, true, true, List.of("minecraft:hay_block"), List.of()),
                template("mine_entrance", "Build a Mine Entrance", ContractType.DUNGEON_ENTRANCE, ContractDifficulty.NORMAL, 45, 650, false, true, false, true, List.of("minecraft:stone"), List.of()),
                template("market_stall", "Build a Market Stall", ContractType.MARKET_STALL, ContractDifficulty.EASY, 20, 350, false, true, false, true, List.of("minecraft:red_wool"), List.of()),
                template("small_shrine", "Build a Small Shrine", ContractType.DECORATION, ContractDifficulty.NORMAL, 35, 600, false, true, false, true, List.of("minecraft:stone_bricks"), List.of()),
                template("garden_fountain", "Build a Garden Fountain", ContractType.DECORATION, ContractDifficulty.NORMAL, 25, 500, false, false, false, true, List.of("minecraft:water"), List.of()),
                template("wall_segment", "Build a Defensive Wall Segment", ContractType.WALL_SEGMENT, ContractDifficulty.HARD, 100, 1200, false, false, false, true, List.of("minecraft:stone_bricks"), List.of("minecraft:tnt")),
                template("gatehouse", "Build a Gatehouse", ContractType.GATEHOUSE, ContractDifficulty.HARD, 120, 1500, false, true, false, true, List.of("minecraft:stone_bricks", "minecraft:iron_bars"), List.of("minecraft:tnt")),
                template("dock", "Build a Dock", ContractType.BRIDGE, ContractDifficulty.NORMAL, 35, 650, false, false, false, true, List.of("minecraft:oak_planks"), List.of()),
                template("custom_imported", "Build a Custom Imported Blueprint", ContractType.CUSTOM_BLUEPRINT, ContractDifficulty.HARD, 50, 2000, false, false, false, true, List.of(), List.of("minecraft:bedrock"))
        );
    }

    private static BuildContract template(String id, String title, ContractType type, ContractDifficulty difficulty, int minBlocks, int maxBlocks, boolean door, boolean roof, boolean windows, boolean foundation, List<String> required, List<String> banned) {
        int multiplier = ContractDifficultyScaler.reputationMultiplier(difficulty);
        return new BuildContract(
                id,
                title,
                "Alpha settlement contract: " + title + ".",
                type,
                difficulty,
                "",
                List.of(type.name().toLowerCase()),
                new BuildContractRequirements(minBlocks, maxBlocks, 1, 1, 1, 96, 96, 96, required, banned, door, roof, windows, foundation, true, 80),
                new BuildContractRewards(10 * multiplier, 25 * multiplier, List.of(), List.of(new ContractRewardItem("minecraft:emerald", multiplier)), ""),
                ContractStatus.AVAILABLE,
                null,
                "",
                0,
                0,
                0,
                0
        );
    }
}
