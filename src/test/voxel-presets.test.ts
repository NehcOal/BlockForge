import { describe, expect, it } from "vitest";
import {
  getAllPresets,
  getBlockKey,
  getUsedBlockTypes,
  canGenerateStructurePresetAt,
  generateStructurePresetFromDebugCommand,
  listStructurePresetDebugCommands,
  runStructurePresetDebugCommand,
  structurePresets,
  validateStructurePresetDefinition,
  validateStructurePresetRegistry,
  validateVoxelModel
} from "@/lib/voxel";
import type { PresetId } from "@/types/blueprint";
import type { StructurePresetDefinition } from "@/lib/voxel";

const batchPresetIds = ["cottage", "farmhouse", "blacksmith", "watchtower"] as const satisfies readonly PresetId[];
const castlePresetIds = [
  "castle_keep",
  "castle_gatehouse",
  "castle_wall_segment",
  "castle_corner_tower",
  "castle_courtyard",
  "small_fort"
] as const satisfies readonly PresetId[];
const finalBatchPresetIds = [
  "inn",
  "manor",
  "barn",
  "stable",
  "warehouse",
  "ruined_house",
  "ruined_tower",
  "ruined_fort",
  "market_stall",
  "chapel"
] as const satisfies readonly PresetId[];
const terrainPresetIds = [
  "wizard_tower",
  "treehouse",
  "dock_house",
  "desert_house",
  "snow_cabin",
  "swamp_hut",
  "mountain_lodge",
  "japanese_house",
  "pagoda"
] as const satisfies readonly PresetId[];

const minimumBlockCounts: Partial<Record<PresetId, number>> = {
  "medieval-tower": 250,
  "small-cottage": 180,
  "dungeon-entrance": 160,
  "stone-bridge": 130,
  "pixel-statue": 100
};

describe("voxel presets", () => {
  const presets = getAllPresets();

  it("returns the legacy presets plus expanded structure presets", () => {
    expect(presets.length).toBe(5 + structurePresets.length);
    expect(presets.map((preset) => preset.id)).toContain("castle_keep");
    expect(presets.map((preset) => preset.id)).toContain("dock_house");
  });

  it("generates non-empty block lists that meet the MVP minimums", () => {
    for (const preset of presets) {
      expect(preset.blocks.length).toBeGreaterThan(0);
      expect(preset.blocks.length).toBeGreaterThanOrEqual(minimumBlockCounts[preset.id] ?? 20);
    }
  });

  it("generates blocks with required fields", () => {
    for (const preset of presets) {
      for (const block of preset.blocks) {
        expect(block).toHaveProperty("x");
        expect(block).toHaveProperty("y");
        expect(block).toHaveProperty("z");
        expect(block).toHaveProperty("block");
      }
    }
  });

  it("keeps every block coordinate inside its model size", () => {
    for (const preset of presets) {
      for (const block of preset.blocks) {
        expect(block.x).toBeGreaterThanOrEqual(0);
        expect(block.x).toBeLessThan(preset.size.width);
        expect(block.y).toBeGreaterThanOrEqual(0);
        expect(block.y).toBeLessThan(preset.size.height);
        expect(block.z).toBeGreaterThanOrEqual(0);
        expect(block.z).toBeLessThan(preset.size.depth);
      }
    }
  });

  it("does not generate duplicate coordinates", () => {
    for (const preset of presets) {
      const keys = preset.blocks.map(getBlockKey);
      expect(new Set(keys).size).toBe(keys.length);
    }
  });

  it("returns valid voxel models", () => {
    for (const preset of presets) {
      expect(validateVoxelModel(preset)).toBe(true);
    }
  });

  it("tracks at least one block type per preset", () => {
    for (const preset of presets) {
      expect(getUsedBlockTypes(preset).length).toBeGreaterThan(0);
    }
  });

  it("keeps structure preset ids unique and metadata valid", () => {
    const ids = structurePresets.map((preset) => preset.id);
    expect(new Set(ids).size).toBe(ids.length);

    for (const preset of structurePresets) {
      expect(validateStructurePresetDefinition(preset)).toEqual([]);
      expect(Object.keys(preset.palette).length).toBeGreaterThan(0);
      expect(preset.features.length).toBeGreaterThan(0);
      expect(preset.footprint.width).toBeGreaterThan(0);
      expect(preset.footprint.depth).toBeGreaterThan(0);
      expect(preset.footprint.height).toBeGreaterThan(0);
      expect(["small", "medium", "large", "modular"]).toContain(preset.size);
    }
  });

  it("registers final batch utility, ruin, and civic presets", () => {
    expect(structurePresets.find((preset) => preset.id === "inn")).toMatchObject({
      category: "utility",
      size: "large",
      footprint: { width: 17, depth: 13, height: 11 }
    });
    expect(structurePresets.find((preset) => preset.id === "manor")).toMatchObject({
      category: "residential",
      size: "large",
      footprint: { width: 21, depth: 17, height: 13 }
    });
    expect(structurePresets.find((preset) => preset.id === "warehouse")).toMatchObject({
      category: "utility",
      size: "large",
      footprint: { width: 17, depth: 13, height: 9 }
    });
    expect(structurePresets.find((preset) => preset.id === "ruined_fort")).toMatchObject({
      category: "ruin",
      size: "large",
      footprint: { width: 25, depth: 25, height: 12 }
    });
    expect(structurePresets.find((preset) => preset.id === "market_stall")).toMatchObject({
      category: "civic",
      size: "small",
      footprint: { width: 5, depth: 5, height: 5 }
    });
    expect(structurePresets.find((preset) => preset.id === "chapel")).toMatchObject({
      category: "civic",
      size: "medium",
      footprint: { width: 11, depth: 17, height: 13 }
    });
    for (const presetId of ["ruined_house", "ruined_tower", "ruined_fort"] as const) {
      expect(structurePresets.find((preset) => preset.id === presetId)?.generationRules.ruinDecayLevel).toBeTruthy();
    }
  });

  it("registers the first batch structure presets with required metadata", () => {
    expect(structurePresets.find((preset) => preset.id === "cottage")).toMatchObject({
      category: "residential",
      size: "small",
      footprint: { width: 9, depth: 7, height: 7 }
    });
    expect(structurePresets.find((preset) => preset.id === "farmhouse")).toMatchObject({
      category: "residential",
      size: "medium",
      footprint: { width: 13, depth: 11, height: 8 }
    });
    expect(structurePresets.find((preset) => preset.id === "blacksmith")).toMatchObject({
      category: "utility",
      size: "small",
      footprint: { width: 11, depth: 9, height: 7 }
    });
    expect(structurePresets.find((preset) => preset.id === "watchtower")).toMatchObject({
      category: "defense",
      size: "medium",
      footprint: { width: 7, depth: 7, height: 18 }
    });

    for (const presetId of batchPresetIds) {
      const preset = structurePresets.find((candidate) => candidate.id === presetId);
      expect(preset?.generationRules.flattenFoundation).toBe(true);
      expect(preset?.generationRules.avoidWater).toBe(true);
      expect(preset?.palette.foundation).toBeTruthy();
      expect(preset?.palette.wall).toBeTruthy();
      expect(preset?.palette.roof).toBeTruthy();
      expect(preset?.palette.accent).toBeTruthy();
    }
  });

  it("generates the first batch through the debug command entrypoint", () => {
    const commands = listStructurePresetDebugCommands();
    for (const presetId of batchPresetIds) {
      const command = `/structurepreset generate ${presetId}`;
      expect(commands).toContain(command);
      const result = generateStructurePresetFromDebugCommand(command);
      expect(result.ok).toBe(true);
      expect(result.errors).toEqual([]);
      expect(result.model?.id).toBe(presetId);
      expect(result.model && validateVoxelModel(result.model)).toBe(true);
    }
  });

  it("registers castle modules with connectors and shared defense metadata", () => {
    expect(structurePresets.find((preset) => preset.id === "castle_keep")).toMatchObject({
      category: "defense",
      size: "large",
      footprint: { width: 17, depth: 17, height: 24 }
    });
    expect(structurePresets.find((preset) => preset.id === "castle_gatehouse")).toMatchObject({
      category: "defense",
      size: "medium",
      footprint: { width: 15, depth: 9, height: 16 }
    });
    expect(structurePresets.find((preset) => preset.id === "castle_wall_segment")).toMatchObject({
      category: "defense",
      size: "modular",
      footprint: { width: 13, depth: 3, height: 8 }
    });
    expect(structurePresets.find((preset) => preset.id === "castle_corner_tower")).toMatchObject({
      category: "defense",
      size: "medium",
      footprint: { width: 9, depth: 9, height: 18 }
    });
    expect(structurePresets.find((preset) => preset.id === "castle_courtyard")).toMatchObject({
      category: "defense",
      size: "large",
      footprint: { width: 21, depth: 21, height: 5 }
    });
    expect(structurePresets.find((preset) => preset.id === "small_fort")).toMatchObject({
      category: "defense",
      size: "large",
      footprint: { width: 25, depth: 25, height: 14 }
    });

    for (const presetId of castlePresetIds) {
      const preset = structurePresets.find((candidate) => candidate.id === presetId);
      expect(preset?.palette.foundation).toBe("stone_bricks");
      expect(preset?.palette.wall).toBe("stone_bricks");
      expect(preset?.generationRules.requiresFlatArea).toBe(true);
      expect(preset?.generationRules.allowTerrainFlattening).toBe(true);
      expect(preset?.attachmentPoints?.length).toBeGreaterThan(0);
    }
  });

  it("generates castle modules through the debug command entrypoint", () => {
    const commands = listStructurePresetDebugCommands();
    for (const presetId of castlePresetIds) {
      const command = `/structurepreset generate ${presetId}`;
      expect(commands).toContain(command);
      const result = generateStructurePresetFromDebugCommand(command);
      expect(result.ok).toBe(true);
      expect(result.errors).toEqual([]);
      expect(result.logs).toEqual(expect.arrayContaining([`preset id: ${presetId}`]));
      expect(result.model?.id).toBe(presetId);
      expect(result.model && validateVoxelModel(result.model)).toBe(true);
    }
  });

  it("keeps castle modules visibly distinct", () => {
    const generated = Object.fromEntries(
      castlePresetIds.map((presetId) => {
        const result = generateStructurePresetFromDebugCommand(`/structurepreset generate ${presetId}`);
        if (!result.model) throw new Error(`Missing model for ${presetId}`);
        return [presetId, getUsedBlockTypes(result.model)];
      })
    ) as Record<(typeof castlePresetIds)[number], string[]>;

    expect(generated.castle_keep).toEqual(expect.arrayContaining(["stone_bricks", "cracked_stone_bricks", "mossy_stone_bricks", "barrel"]));
    expect(generated.castle_gatehouse).toEqual(expect.arrayContaining(["iron_bars", "chain", "lantern"]));
    expect(generated.castle_wall_segment).toEqual(expect.arrayContaining(["polished_andesite", "torch"]));
    expect(generated.castle_corner_tower).toEqual(expect.arrayContaining(["ladder", "iron_bars"]));
    expect(generated.castle_courtyard).toEqual(expect.arrayContaining(["water", "barrel", "dark_oak_fence"]));
    expect(generated.small_fort).toEqual(expect.arrayContaining(["iron_bars", "chest", "barrel"]));
  });

  it("reports invalid structure registry data", () => {
    expect(validateStructurePresetRegistry([structurePresets[0], structurePresets[0]])).toEqual(
      expect.arrayContaining([`duplicate preset id: ${structurePresets[0].id}`])
    );

    const emptyPalette = {
      ...structurePresets.find((preset) => preset.id === "castle_keep"),
      palette: {}
    } as unknown as StructurePresetDefinition;
    expect(validateStructurePresetDefinition(emptyPalette)).toEqual(expect.arrayContaining(["palette is required"]));

    const modularWithoutConnectors = {
      ...structurePresets.find((preset) => preset.id === "castle_wall_segment"),
      connectors: undefined
    } as unknown as StructurePresetDefinition;
    expect(validateStructurePresetDefinition(modularWithoutConnectors)).toEqual(expect.arrayContaining(["modular presets require connectors"]));

    const ruinWithoutDecay = {
      ...structurePresets.find((preset) => preset.id === "ruined_house"),
      generationRules: {
        ...structurePresets.find((preset) => preset.id === "ruined_house")?.generationRules,
        ruinDecayLevel: undefined
      }
    } as unknown as StructurePresetDefinition;
    expect(validateStructurePresetDefinition(ruinWithoutDecay)).toEqual(expect.arrayContaining(["ruin presets require ruinDecayLevel"]));
  });

  it("registers terrain and fantasy presets with environment rules", () => {
    expect(structurePresets.find((preset) => preset.id === "wizard_tower")).toMatchObject({
      category: "fantasy",
      size: "medium",
      footprint: { width: 9, depth: 9, height: 28 }
    });
    expect(structurePresets.find((preset) => preset.id === "treehouse")).toMatchObject({
      category: "fantasy",
      size: "medium",
      footprint: { width: 13, depth: 13, height: 18 }
    });
    expect(structurePresets.find((preset) => preset.id === "dock_house")).toMatchObject({
      category: "terrain_specific",
      size: "medium",
      footprint: { width: 13, depth: 15, height: 8 }
    });
    expect(structurePresets.find((preset) => preset.id === "desert_house")).toMatchObject({
      category: "residential",
      size: "small",
      footprint: { width: 9, depth: 9, height: 7 }
    });
    expect(structurePresets.find((preset) => preset.id === "snow_cabin")).toMatchObject({
      category: "residential",
      size: "small",
      footprint: { width: 11, depth: 9, height: 8 }
    });

    expect(structurePresets.find((preset) => preset.id === "dock_house")?.generationRules.requiresWaterNearby).toBe(true);
    expect(structurePresets.find((preset) => preset.id === "treehouse")?.generationRules.requiresTreesNearby).toBe(true);
    expect(structurePresets.find((preset) => preset.id === "mountain_lodge")?.generationRules.maxSlope).toBe("high");
    for (const presetId of terrainPresetIds) {
      expect(structurePresets.find((preset) => preset.id === presetId)?.generationRules.preferredBiomeTags?.length).toBeGreaterThan(0);
    }
  });

  it("checks terrain rules unless debug force mode is enabled", () => {
    const dock = structurePresets.find((preset) => preset.id === "dock_house");
    const treehouse = structurePresets.find((preset) => preset.id === "treehouse");
    if (!dock || !treehouse) throw new Error("Missing terrain presets");

    expect(canGenerateStructurePresetAt({
      biomeId: "plains",
      biomeTags: ["plains"],
      nearWater: false,
      nearTrees: false,
      slopeScore: 0.1,
      groundType: "solid",
      distanceFromWater: 20
    }, dock).canGenerate).toBe(false);

    expect(canGenerateStructurePresetAt({
      biomeId: "forest",
      biomeTags: ["forest"],
      nearWater: false,
      nearTrees: true,
      slopeScore: 0.1,
      groundType: "solid",
      distanceFromWater: 12
    }, treehouse).canGenerate).toBe(true);

    const blocked = generateStructurePresetFromDebugCommand("/structurepreset generate dock_house");
    expect(blocked.ok).toBe(false);
    expect(blocked.errors).toEqual(expect.arrayContaining(["water nearby is required"]));

    const forced = generateStructurePresetFromDebugCommand("/structurepreset generate dock_house --force");
    expect(forced.ok).toBe(true);
    expect(forced.logs).toEqual(expect.arrayContaining(["force mode: enabled"]));
    expect(forced.model && validateVoxelModel(forced.model)).toBe(true);
  });

  it("generates terrain and fantasy presets in force mode", () => {
    for (const presetId of terrainPresetIds) {
      const result = generateStructurePresetFromDebugCommand(`/structurepreset generate ${presetId} --force`);
      expect(result.ok).toBe(true);
      expect(result.model?.id).toBe(presetId);
      expect(result.model && validateVoxelModel(result.model)).toBe(true);
    }
  });

  it("keeps terrain and fantasy palettes distinct", () => {
    const generated = Object.fromEntries(
      terrainPresetIds.map((presetId) => {
        const result = generateStructurePresetFromDebugCommand(`/structurepreset generate ${presetId} --force`);
        if (!result.model) throw new Error(`Missing model for ${presetId}`);
        return [presetId, getUsedBlockTypes(result.model)];
      })
    ) as Record<(typeof terrainPresetIds)[number], string[]>;

    expect(generated.wizard_tower).toEqual(expect.arrayContaining(["amethyst_block", "purple_stained_glass", "bookshelf"]));
    expect(generated.treehouse).toEqual(expect.arrayContaining(["oak_log", "leaves", "ladder"]));
    expect(generated.dock_house).toEqual(expect.arrayContaining(["water", "barrel", "lantern"]));
    expect(generated.desert_house).toEqual(expect.arrayContaining(["sandstone", "smooth_sandstone", "cut_sandstone"]));
    expect(generated.snow_cabin).toEqual(expect.arrayContaining(["snow", "lantern"]));
    expect(generated.swamp_hut).toEqual(expect.arrayContaining(["mud", "mud_bricks", "mangrove_log"]));
    expect(generated.mountain_lodge).toEqual(expect.arrayContaining(["stone", "spruce_planks", "barrel"]));
    expect(generated.japanese_house).toEqual(expect.arrayContaining(["white_concrete", "dark_oak_stairs", "bamboo"]));
    expect(generated.pagoda).toEqual(expect.arrayContaining(["stripped_dark_oak_log", "dark_oak_stairs", "lantern"]));
  });

  it("generates final batch presets and keeps their silhouettes distinct", () => {
    const generated = Object.fromEntries(
      finalBatchPresetIds.map((presetId) => {
        const result = generateStructurePresetFromDebugCommand(`/structurepreset generate ${presetId} --force`);
        if (!result.model) throw new Error(`Missing model for ${presetId}`);
        return [presetId, getUsedBlockTypes(result.model)];
      })
    ) as Record<(typeof finalBatchPresetIds)[number], string[]>;

    expect(generated.inn).toEqual(expect.arrayContaining(["barrel", "wool_white", "lantern"]));
    expect(generated.manor).toEqual(expect.arrayContaining(["bookshelf", "dark_oak_fence", "grass"]));
    expect(generated.barn).toEqual(expect.arrayContaining(["hay_block", "door", "dark_oak_stairs"]));
    expect(generated.stable).toEqual(expect.arrayContaining(["fence", "hay_block", "water"]));
    expect(generated.warehouse).toEqual(expect.arrayContaining(["chest", "barrel", "door"]));
    expect(generated.ruined_house).toEqual(expect.arrayContaining(["mossy_cobblestone", "cobweb", "vine"]));
    expect(generated.ruined_tower).toEqual(expect.arrayContaining(["cracked_stone_bricks", "vine"]));
    expect(generated.ruined_fort).toEqual(expect.arrayContaining(["mossy_cobblestone", "chest", "vine"]));
    expect(generated.market_stall).toEqual(expect.arrayContaining(["wool_red", "barrel", "chest"]));
    expect(generated.chapel).toEqual(expect.arrayContaining(["smooth_stone", "lantern", "glass_pane"]));
  });

  it("supports debug list, category filter, and validate commands", () => {
    const list = runStructurePresetDebugCommand("/structurepreset list");
    expect(list.ok).toBe(true);
    expect(list.entries?.length).toBe(structurePresets.length);
    expect(list.entries?.[0]).toHaveProperty("specialGenerationRules");

    const ruins = runStructurePresetDebugCommand("/structurepreset list category ruin");
    expect(ruins.ok).toBe(true);
    expect(ruins.entries?.map((entry) => entry.id)).toEqual(expect.arrayContaining(["ruined_house", "ruined_tower", "ruined_fort"]));
    expect(ruins.entries?.every((entry) => entry.category === "ruin")).toBe(true);

    const validate = runStructurePresetDebugCommand("/structurepreset validate");
    expect(validate.ok).toBe(true);
    expect(validate.errors).toEqual([]);
    expect(validate.logs).toEqual(expect.arrayContaining(["validation passed"]));
    expect(listStructurePresetDebugCommands()).toEqual(expect.arrayContaining(["/structurepreset list", "/structurepreset validate"]));
  });

  it("reports invalid terrain rule metadata", () => {
    const dockWithoutDistance = {
      ...structurePresets.find((preset) => preset.id === "dock_house"),
      generationRules: {
        ...structurePresets.find((preset) => preset.id === "dock_house")?.generationRules,
        maxDistanceFromWater: undefined
      }
    } as unknown as StructurePresetDefinition;
    expect(validateStructurePresetDefinition(dockWithoutDistance)).toEqual(expect.arrayContaining(["water-nearby presets require maxDistanceFromWater"]));

    const noBiomePreference = {
      ...structurePresets.find((preset) => preset.id === "snow_cabin"),
      generationRules: {
        ...structurePresets.find((preset) => preset.id === "snow_cabin")?.generationRules,
        preferredBiomeTags: []
      }
    } as unknown as StructurePresetDefinition;
    expect(validateStructurePresetDefinition(noBiomePreference)).toEqual(expect.arrayContaining(["preferredBiomeTags are required unless globalAllowed is true"]));
  });

  it("keeps the first batch visually and functionally distinct", () => {
    const generated = Object.fromEntries(
      batchPresetIds.map((presetId) => {
        const result = generateStructurePresetFromDebugCommand(`/structurepreset generate ${presetId}`);
        if (!result.model) throw new Error(`Missing model for ${presetId}`);
        return [presetId, getUsedBlockTypes(result.model)];
      })
    ) as Record<(typeof batchPresetIds)[number], string[]>;

    expect(generated.cottage).toEqual(expect.arrayContaining(["spruce_stairs", "lantern"]));
    expect(generated.farmhouse).toEqual(expect.arrayContaining(["hay_block", "fence", "barrel"]));
    expect(generated.blacksmith).toEqual(expect.arrayContaining(["anvil", "blast_furnace", "iron_bars", "water"]));
    expect(generated.watchtower).toEqual(expect.arrayContaining(["ladder", "iron_bars", "dark_oak_planks"]));
  });

  it("exposes special terrain generation rules", () => {
    expect(structurePresets.find((preset) => preset.id === "dock_house")?.generationRules.requiresWater).toBe(true);
    expect(structurePresets.find((preset) => preset.id === "treehouse")?.generationRules.requiresForest).toBe(true);
    expect(structurePresets.find((preset) => preset.id === "mountain_lodge")?.generationRules.prefersMountain).toBe(true);
    expect(structurePresets.find((preset) => preset.id === "castle_keep")?.generationRules.requiresFlatArea).toBe(true);
    expect(structurePresets.find((preset) => preset.id === "small_fort")?.generationRules.requiresFlatArea).toBe(true);
  });
});
