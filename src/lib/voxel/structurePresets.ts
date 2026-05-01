import { blocksFromStore, setBlock } from "@/lib/voxel/utils";
import type { BlockType, PresetId, VoxelBlock, VoxelModel, VoxelSize } from "@/types/blueprint";

export type StructurePresetCategory =
  | "residential"
  | "utility"
  | "defense"
  | "fantasy"
  | "terrain_specific"
  | "ruin"
  | "civic";

export type StructurePresetSize = "small" | "medium" | "large" | "modular";

export type StructureConnectorDirection = "north" | "south" | "east" | "west" | "up" | "down";

export type StructureAttachmentPoint = {
  id: string;
  direction: StructureConnectorDirection;
  offsetX: number;
  offsetY: number;
  offsetZ: number;
  compatibleWith: PresetId[];
};

export type StructurePresetBiome =
  | "plains"
  | "forest"
  | "desert"
  | "snowy"
  | "swamp"
  | "mountain";

export type StructureSlopeLimit = "low" | "medium" | "high";
export type StructureRuinDecayLevel = "low" | "medium" | "high";

export type StructureTerrainContext = {
  biomeId: string;
  biomeTags: string[];
  nearWater: boolean;
  nearTrees: boolean;
  slopeScore: number;
  groundType: "solid" | "water" | "mixed";
  distanceFromWater?: number;
};

export type StructureTerrainCheckResult = {
  canGenerate: boolean;
  force: boolean;
  messages: string[];
};

export type StructurePresetFeature =
  | "door"
  | "windows"
  | "roof"
  | "chimney"
  | "porch"
  | "workshop"
  | "loft"
  | "stalls"
  | "tower"
  | "battlements"
  | "tree_support"
  | "dock"
  | "ruined"
  | "courtyard"
  | "gate"
  | "wall"
  | "simple_room"
  | "sloped_roof"
  | "fireplace"
  | "small_windows"
  | "front_path"
  | "main_room"
  | "small_bedroom"
  | "crop_patch"
  | "fenced_yard"
  | "storage_barrels"
  | "forge_area"
  | "anvil"
  | "blast_furnace"
  | "water_trough"
  | "storage_chest"
  | "open_work_area"
  | "tall_tower"
  | "ladder_access"
  | "top_platform"
  | "arrow_slits"
  | "central_keep"
  | "main_hall"
  | "inner_staircase"
  | "storage_room"
  | "roof_platform"
  | "double_towers"
  | "gate_arch"
  | "portcullis_style_bars"
  | "wall_walk"
  | "lantern_points"
  | "curtain_wall"
  | "top_walkway"
  | "torch_points"
  | "square_tower"
  | "internal_ladder"
  | "battlement_top"
  | "open_yard"
  | "well"
  | "training_dummy_area"
  | "storage_crates"
  | "lantern_posts"
  | "outer_wall"
  | "gatehouse"
  | "small_courtyard"
  | "corner_watch_posts"
  | "barracks_room"
  | "spiral_stairs"
  | "enchanting_room"
  | "bookshelves"
  | "balcony"
  | "crystal_roof"
  | "tree_trunk_support"
  | "elevated_platform"
  | "leaf_canopy"
  | "rope_bridge_style_path"
  | "wooden_dock"
  | "fishing_area"
  | "boat_space"
  | "barrels"
  | "flat_roof"
  | "shaded_entrance"
  | "sandstone_walls"
  | "steep_roof"
  | "snow_layer_roof"
  | "warm_lanterns"
  | "raised_floor"
  | "stilts"
  | "muddy_path"
  | "hanging_lantern"
  | "stone_foundation"
  | "spruce_roof"
  | "wide_roof"
  | "paper_window_style"
  | "garden_path"
  | "multi_tier_roof"
  | "central_column"
  | "stacked_floors"
  | "lanterns"
  | "counter"
  | "tables"
  | "upstairs_beds"
  | "large_hall"
  | "bedroom"
  | "study_room"
  | "hay_storage"
  | "animal_pens"
  | "high_roof"
  | "double_door"
  | "horse_stalls"
  | "hay_blocks"
  | "fenced_area"
  | "storage_chests"
  | "loading_door"
  | "crate_rows"
  | "broken_walls"
  | "missing_roof"
  | "rubble_floor"
  | "mossy_blocks"
  | "abandoned_chest_chance"
  | "broken_tower"
  | "partial_stairs"
  | "cracked_walls"
  | "vine_or_moss"
  | "top_missing_section"
  | "broken_outer_wall"
  | "collapsed_gate"
  | "partial_corner_towers"
  | "overgrown_courtyard"
  | "loot_chest_chance"
  | "cloth_roof_style"
  | "crates"
  | "small_hall"
  | "bell_tower"
  | "stained_glass"
  | "benches"
  | "simple_altar";

export type StructureGenerationRules = {
  biome: StructurePresetBiome;
  flattenFoundation?: boolean;
  allowTerrainFlattening?: boolean | "partial";
  foundationDepth?: number;
  minClearanceHeight?: number;
  preferredBiomeTags?: string[];
  forbiddenBiomeTags?: string[];
  maxSlope?: StructureSlopeLimit;
  requiresWaterNearby?: boolean;
  requiresTreesNearby?: boolean;
  allowPartialOverWater?: boolean;
  requiresSolidGround?: boolean;
  minDistanceFromWater?: number;
  maxDistanceFromWater?: number;
  globalAllowed?: boolean;
  avoidWater?: boolean;
  requiresWater?: boolean;
  requiresForest?: boolean;
  prefersMountain?: boolean;
  requiresFlatArea?: boolean;
  ruinLevel?: number;
  ruinDecayLevel?: StructureRuinDecayLevel;
  floors?: number;
  towerRadius?: number;
};

export type StructurePalette = {
  foundation: BlockType;
  wall: BlockType;
  trim: BlockType;
  roof: BlockType;
  accent: BlockType;
  glass: BlockType;
  floor: BlockType;
};

export type StructureFootprint = {
  width: number;
  depth: number;
  height: number;
};

export type StructurePresetDefinition = {
  id: PresetId;
  displayName: string;
  category: StructurePresetCategory;
  size: StructurePresetSize;
  modelSize: VoxelSize;
  footprint: StructureFootprint;
  palette: StructurePalette;
  features: StructurePresetFeature[];
  suitable?: string[];
  connectors?: Partial<Record<StructureConnectorDirection, PresetId[]>>;
  attachmentPoints?: StructureAttachmentPoint[];
  generationRules: StructureGenerationRules;
};

export type StructurePresetCategoryConfig = {
  enableResidential: boolean;
  enableUtility: boolean;
  enableDefense: boolean;
  enableFantasy: boolean;
  enableTerrainSpecific: boolean;
  enableRuin: boolean;
  enableCivic: boolean;
};

export type StructurePresetIndexEntry = {
  id: PresetId;
  displayName: string;
  category: StructurePresetCategory;
  size: StructurePresetSize;
  footprint: StructureFootprint;
  specialGenerationRules: string[];
};

const validCategories = new Set<StructurePresetCategory>([
  "residential",
  "utility",
  "defense",
  "fantasy",
  "terrain_specific",
  "ruin",
  "civic"
]);

const validSizes = new Set<StructurePresetSize>(["small", "medium", "large", "modular"]);

export const defaultStructurePresetCategoryConfig: StructurePresetCategoryConfig = {
  enableResidential: true,
  enableUtility: true,
  enableDefense: true,
  enableFantasy: true,
  enableTerrainSpecific: true,
  enableRuin: true,
  enableCivic: true
};

const castlePalette: StructurePalette = {
  foundation: "stone_bricks",
  wall: "stone_bricks",
  trim: "cracked_stone_bricks",
  roof: "dark_oak_planks",
  accent: "iron_bars",
  glass: "iron_bars",
  floor: "polished_andesite"
};

const fantasyPalette: StructurePalette = {
  foundation: "stone_bricks",
  wall: "deepslate",
  trim: "amethyst_block",
  roof: "purple_stained_glass",
  accent: "lantern",
  glass: "purple_stained_glass",
  floor: "bookshelf"
};

const biomePalettes: Record<StructurePresetBiome, StructurePalette> = {
  plains: {
    foundation: "cobblestone",
    wall: "oak_planks",
    trim: "oak_log",
    roof: "spruce_stairs",
    accent: "lantern",
    glass: "glass_pane",
    floor: "oak_planks"
  },
  forest: {
    foundation: "mossy_cobblestone",
    wall: "spruce_planks",
    trim: "oak_log",
    roof: "spruce_stairs",
    accent: "lantern",
    glass: "glass_pane",
    floor: "oak_planks"
  },
  desert: {
    foundation: "sandstone",
    wall: "smooth_sandstone",
    trim: "cut_sandstone",
    roof: "terracotta",
    accent: "lantern",
    glass: "glass_pane",
    floor: "smooth_sandstone"
  },
  snowy: {
    foundation: "stone_bricks",
    wall: "spruce_planks",
    trim: "spruce_log",
    roof: "snow",
    accent: "lantern",
    glass: "glass_pane",
    floor: "spruce_planks"
  },
  swamp: {
    foundation: "mud_bricks",
    wall: "dark_oak_planks",
    trim: "dark_oak_log",
    roof: "mangrove_planks",
    accent: "lantern",
    glass: "glass_pane",
    floor: "moss_block"
  },
  mountain: {
    foundation: "stone",
    wall: "stone_bricks",
    trim: "spruce_log",
    roof: "dark_oak_planks",
    accent: "lantern",
    glass: "glass_pane",
    floor: "spruce_planks"
  }
};

type StructurePresetOptions = Partial<StructureGenerationRules & StructurePalette> & {
  connectors?: Partial<Record<StructureConnectorDirection, PresetId[]>>;
  attachmentPoints?: StructureAttachmentPoint[];
  suitable?: string[];
};

function castleOptions(options: StructurePresetOptions = {}): StructurePresetOptions {
  return {
    ...castlePalette,
    avoidWater: true,
    preferredBiomeTags: ["mountain", "plains"],
    ...options
  };
}

function fantasyOptions(options: StructurePresetOptions = {}): StructurePresetOptions {
  return {
    ...fantasyPalette,
    preferredBiomeTags: ["forest", "mountain", "plains"],
    requiresSolidGround: true,
    ...options
  };
}

export const structurePresets = [
  preset(
    "cottage",
    "Cottage",
    "residential",
    "small",
    9,
    7,
    7,
    "plains",
    ["simple_room", "sloped_roof", "fireplace", "small_windows", "front_path"],
    {
      flattenFoundation: true,
      avoidWater: true,
      wall: "oak_planks",
      roof: "spruce_stairs",
      foundation: "cobblestone",
      accent: "lantern",
      glass: "glass_pane"
    }
  ),
  preset(
    "farmhouse",
    "Farmhouse",
    "residential",
    "medium",
    13,
    11,
    8,
    "plains",
    ["main_room", "small_bedroom", "crop_patch", "fenced_yard", "storage_barrels"],
    {
      flattenFoundation: true,
      avoidWater: true,
      wall: "oak_planks",
      trim: "stripped_oak_log",
      roof: "oak_stairs",
      foundation: "cobblestone",
      accent: "barrel"
    }
  ),
  preset(
    "blacksmith",
    "Blacksmith",
    "utility",
    "small",
    11,
    9,
    7,
    "plains",
    ["forge_area", "anvil", "blast_furnace", "water_trough", "storage_chest", "open_work_area"],
    {
      flattenFoundation: true,
      avoidWater: true,
      wall: "cobblestone",
      trim: "stone_bricks",
      roof: "dark_oak_stairs",
      floor: "stone",
      accent: "iron_bars"
    }
  ),
  preset("inn", "Inn", "utility", "large", 17, 13, 11, "plains", ["main_hall", "counter", "tables", "fireplace", "upstairs_beds", "storage_room"], {
    suitable: ["village", "roadside", "plains", "forest"],
    wall: "oak_planks",
    trim: "stripped_oak_log",
    roof: "dark_oak_stairs",
    foundation: "cobblestone",
    preferredBiomeTags: ["village", "roadside", "plains", "forest"]
  }),
  preset("manor", "Manor", "residential", "large", 21, 17, 13, "plains", ["large_hall", "bedroom", "study_room", "balcony", "garden_path", "storage_room"], {
    requiresFlatArea: true,
    allowTerrainFlattening: true,
    wall: "oak_planks",
    trim: "stripped_oak_log",
    roof: "dark_oak_stairs",
    foundation: "stone_bricks",
    preferredBiomeTags: ["plains", "forest", "village"]
  }),
  preset("barn", "Barn", "utility", "medium", 15, 11, 10, "plains", ["hay_storage", "animal_pens", "high_roof", "double_door"], {
    wall: "stripped_oak_log",
    roof: "dark_oak_stairs",
    accent: "hay_block",
    preferredBiomeTags: ["plains", "village", "farm"]
  }),
  preset("stable", "Stable", "utility", "medium", 13, 9, 8, "plains", ["horse_stalls", "hay_blocks", "water_trough", "fenced_area"], {
    wall: "oak_planks",
    trim: "stripped_oak_log",
    roof: "spruce_stairs",
    accent: "fence",
    preferredBiomeTags: ["plains", "village", "farm"]
  }),
  preset("warehouse", "Warehouse", "utility", "large", 17, 13, 9, "plains", ["storage_chests", "barrels", "loading_door", "crate_rows"], {
    suitable: ["dock", "town", "market"],
    wall: "oak_planks",
    trim: "stripped_oak_log",
    roof: "dark_oak_stairs",
    foundation: "cobblestone",
    preferredBiomeTags: ["dock", "town", "market", "plains"]
  }),
  preset(
    "watchtower",
    "Watchtower",
    "defense",
    "medium",
    7,
    7,
    18,
    "mountain",
    ["tall_tower", "ladder_access", "top_platform", "battlements", "arrow_slits"],
    {
      flattenFoundation: true,
      avoidWater: true,
      wall: "stone_bricks",
      trim: "cobblestone",
      roof: "dark_oak_planks",
      accent: "fence",
      floors: 3,
      towerRadius: 3
    }
  ),
  preset("wizard_tower", "Wizard Tower", "fantasy", "medium", 9, 9, 28, "forest", ["tall_tower", "spiral_stairs", "enchanting_room", "bookshelves", "balcony", "crystal_roof"], fantasyOptions({
    maxSlope: "medium",
    preferredBiomeTags: ["forest", "mountain", "plains"],
    floors: 5,
    towerRadius: 4
  })),
  preset("treehouse", "Treehouse", "fantasy", "medium", 13, 13, 18, "forest", ["tree_trunk_support", "elevated_platform", "ladder_access", "leaf_canopy", "rope_bridge_style_path"], {
    requiresTreesNearby: true,
    requiresForest: true,
    allowTerrainFlattening: false,
    preferredBiomeTags: ["forest", "jungle", "taiga"],
    forbiddenBiomeTags: ["desert", "ocean"],
    wall: "spruce_planks",
    trim: "oak_log",
    roof: "spruce_stairs",
    floor: "spruce_planks"
  }),
  preset("mountain_lodge", "Mountain Lodge", "terrain_specific", "medium", 13, 11, 9, "mountain", ["stone_foundation", "spruce_roof", "balcony", "fireplace", "storage_room"], {
    prefersMountain: true,
    maxSlope: "high",
    allowTerrainFlattening: true,
    preferredBiomeTags: ["mountain", "snowy_slopes", "taiga"],
    foundation: "stone",
    wall: "spruce_planks",
    roof: "spruce_stairs",
    floor: "spruce_planks"
  }),
  preset("swamp_hut", "Swamp Hut", "terrain_specific", "small", 9, 9, 8, "swamp", ["raised_floor", "stilts", "muddy_path", "hanging_lantern"], {
    allowPartialOverWater: true,
    requiresSolidGround: false,
    preferredBiomeTags: ["swamp", "mangrove_swamp"],
    avoidWater: false
  }),
  preset("desert_house", "Desert House", "residential", "small", 9, 9, 7, "desert", ["flat_roof", "shaded_entrance", "small_courtyard", "sandstone_walls"], {
    requiresSolidGround: true,
    preferredBiomeTags: ["desert", "badlands"],
    foundation: "sandstone",
    wall: "smooth_sandstone",
    roof: "cut_sandstone",
    floor: "smooth_sandstone"
  }),
  preset("snow_cabin", "Snow Cabin", "residential", "small", 11, 9, 8, "snowy", ["steep_roof", "fireplace", "snow_layer_roof", "warm_lanterns"], {
    requiresSolidGround: true,
    preferredBiomeTags: ["snowy", "taiga"],
    roof: "snow",
    accent: "lantern"
  }),
  preset("dock_house", "Dock House", "terrain_specific", "medium", 13, 15, 8, "plains", ["wooden_dock", "fishing_area", "boat_space", "barrels", "lantern_posts"], {
    requiresWaterNearby: true,
    requiresWater: true,
    maxDistanceFromWater: 4,
    allowPartialOverWater: true,
    preferredBiomeTags: ["river", "ocean", "beach"],
    avoidWater: false,
    wall: "oak_planks",
    roof: "spruce_stairs",
    floor: "oak_planks"
  }),
  preset("japanese_house", "Japanese House", "residential", "medium", 13, 11, 8, "forest", ["raised_floor", "wide_roof", "paper_window_style", "garden_path"], {
    preferredBiomeTags: ["forest", "bamboo_jungle", "plains"],
    wall: "white_concrete",
    trim: "stripped_dark_oak_log",
    roof: "dark_oak_stairs",
    floor: "spruce_planks",
    accent: "bamboo"
  }),
  preset("pagoda", "Pagoda", "fantasy", "large", 13, 13, 24, "forest", ["multi_tier_roof", "central_column", "stacked_floors", "lanterns"], fantasyOptions({
    requiresFlatArea: true,
    allowTerrainFlattening: true,
    preferredBiomeTags: ["bamboo_jungle", "forest", "plains"],
    floors: 5,
    wall: "spruce_planks",
    trim: "stripped_dark_oak_log",
    roof: "dark_oak_stairs",
    floor: "spruce_planks"
  })),
  preset("ruined_house", "Ruined House", "ruin", "small", 11, 9, 7, "forest", ["broken_walls", "missing_roof", "rubble_floor", "mossy_blocks", "abandoned_chest_chance"], {
    allowTerrainFlattening: false,
    ruinDecayLevel: "medium",
    wall: "mossy_cobblestone",
    trim: "cracked_stone_bricks",
    roof: "dark_oak_stairs",
    preferredBiomeTags: ["forest", "swamp", "plains"]
  }),
  preset("ruined_tower", "Ruined Tower", "ruin", "medium", 9, 9, 18, "mountain", ["broken_tower", "partial_stairs", "cracked_walls", "vine_or_moss", "top_missing_section"], {
    ruinDecayLevel: "high",
    wall: "stone_bricks",
    trim: "cracked_stone_bricks",
    accent: "vine",
    towerRadius: 4,
    preferredBiomeTags: ["forest", "swamp", "mountain"]
  }),
  preset("ruined_fort", "Ruined Fort", "ruin", "large", 25, 25, 12, "mountain", ["broken_outer_wall", "collapsed_gate", "partial_corner_towers", "overgrown_courtyard", "loot_chest_chance"], {
    requiresFlatArea: true,
    allowTerrainFlattening: "partial",
    ruinDecayLevel: "high",
    wall: "stone_bricks",
    trim: "cracked_stone_bricks",
    foundation: "mossy_cobblestone",
    preferredBiomeTags: ["forest", "swamp", "mountain"]
  }),
  preset("market_stall", "Market Stall", "civic", "small", 5, 5, 5, "plains", ["cloth_roof_style", "counter", "crates", "barrels"], {
    suitable: ["village", "town_square", "roadside"],
    wall: "oak_planks",
    roof: "wool_red",
    accent: "barrel",
    preferredBiomeTags: ["village", "town_square", "roadside", "plains"]
  }),
  preset("chapel", "Chapel", "civic", "medium", 11, 17, 13, "plains", ["small_hall", "bell_tower", "stained_glass", "benches", "simple_altar"], {
    wall: "stone_bricks",
    trim: "smooth_stone",
    roof: "dark_oak_stairs",
    glass: "glass_pane",
    accent: "lantern",
    preferredBiomeTags: ["village", "plains", "forest"]
  }),
  preset("small_fort", "Small Fort", "defense", "large", 25, 25, 14, "mountain", ["outer_wall", "gatehouse", "small_courtyard", "corner_watch_posts", "barracks_room", "storage_room"], castleOptions({
    connectors: { north: ["castle_gatehouse"], east: ["castle_wall_segment"], south: ["castle_wall_segment"], west: ["castle_wall_segment"] },
    requiresFlatArea: true,
    allowTerrainFlattening: true,
    foundationDepth: 2,
    minClearanceHeight: 16
  })),
  preset("castle_keep", "Castle Keep", "defense", "large", 17, 17, 24, "mountain", ["central_keep", "main_hall", "inner_staircase", "battlements", "arrow_slits", "storage_room", "roof_platform"], castleOptions({
    connectors: { north: ["castle_courtyard"], south: ["castle_courtyard"], east: ["castle_wall_segment"], west: ["castle_wall_segment"] },
    requiresFlatArea: true,
    allowTerrainFlattening: true,
    foundationDepth: 3,
    minClearanceHeight: 26,
    floors: 5
  })),
  preset("castle_gatehouse", "Castle Gatehouse", "defense", "medium", 15, 9, 16, "mountain", ["double_towers", "gate_arch", "portcullis_style_bars", "wall_walk", "lantern_points"], castleOptions({
    connectors: { east: ["castle_wall_segment"], west: ["castle_wall_segment"] },
    requiresFlatArea: true,
    allowTerrainFlattening: true,
    foundationDepth: 2,
    minClearanceHeight: 18
  })),
  preset("castle_wall_segment", "Castle Wall Segment", "defense", "modular", 13, 3, 8, "mountain", ["curtain_wall", "battlements", "top_walkway", "torch_points"], castleOptions({
    connectors: { east: ["castle_wall_segment", "castle_corner_tower", "castle_gatehouse"], west: ["castle_wall_segment", "castle_corner_tower", "castle_gatehouse"] },
    requiresFlatArea: true,
    allowTerrainFlattening: true,
    foundationDepth: 1,
    minClearanceHeight: 10
  })),
  preset("castle_corner_tower", "Castle Corner Tower", "defense", "medium", 9, 9, 18, "mountain", ["square_tower", "internal_ladder", "arrow_slits", "battlement_top"], castleOptions({
    connectors: { north: ["castle_wall_segment"], east: ["castle_wall_segment"] },
    requiresFlatArea: true,
    allowTerrainFlattening: true,
    foundationDepth: 2,
    minClearanceHeight: 20,
    towerRadius: 4
  })),
  preset("castle_courtyard", "Castle Courtyard", "defense", "large", 21, 21, 5, "mountain", ["open_yard", "well", "training_dummy_area", "storage_crates", "lantern_posts"], castleOptions({
    connectors: { north: ["castle_gatehouse"], south: ["castle_keep"], east: ["castle_wall_segment"], west: ["castle_wall_segment"] },
    requiresFlatArea: true,
    allowTerrainFlattening: true,
    foundationDepth: 1,
    minClearanceHeight: 7
  }))
] as const satisfies readonly StructurePresetDefinition[];

export function validateStructurePresetDefinition(presetDefinition: StructurePresetDefinition): string[] {
  const errors: string[] = [];
  if (!presetDefinition.id) errors.push("id is required");
  if (!presetDefinition.displayName) errors.push("displayName is required");
  if (!validCategories.has(presetDefinition.category)) errors.push("category is invalid");
  if (!validSizes.has(presetDefinition.size)) errors.push("size is invalid");
  if (!validFootprint(presetDefinition.footprint)) errors.push("footprint must be positive");
  if (!validModelSize(presetDefinition.modelSize)) errors.push("modelSize must be positive");
  if (Object.keys(presetDefinition.palette).length === 0) errors.push("palette is required");
  if (Object.values(presetDefinition.palette).some((block) => !block)) errors.push("palette values are required");
  if (presetDefinition.features.length === 0) errors.push("at least one feature is required");
  if (!presetDefinition.generationRules.biome) errors.push("generationRules.biome is required");
  if (!presetDefinition.generationRules.globalAllowed && (presetDefinition.generationRules.preferredBiomeTags?.length ?? 0) === 0) {
    errors.push("preferredBiomeTags are required unless globalAllowed is true");
  }
  if (presetDefinition.generationRules.requiresWaterNearby && presetDefinition.generationRules.maxDistanceFromWater === undefined) {
    errors.push("water-nearby presets require maxDistanceFromWater");
  }
  if (presetDefinition.generationRules.requiresTreesNearby && (presetDefinition.generationRules.preferredBiomeTags?.length ?? 0) === 0) {
    errors.push("tree-nearby presets require preferredBiomeTags");
  }
  if (presetDefinition.category === "defense" && (!presetDefinition.palette.foundation || !presetDefinition.palette.wall)) {
    errors.push("defense presets require foundation and wall palette entries");
  }
  if (presetDefinition.category === "ruin" && !presetDefinition.generationRules.ruinDecayLevel) {
    errors.push("ruin presets require ruinDecayLevel");
  }
  if (presetDefinition.category === "terrain_specific" && Object.keys(presetDefinition.generationRules).length === 0) {
    errors.push("terrain_specific presets require generationRules");
  }
  if (presetDefinition.size === "modular" && Object.keys(presetDefinition.connectors ?? {}).length === 0) {
    errors.push("modular presets require connectors");
  }
  if (presetDefinition.id.startsWith("castle") && !presetDefinition.id.startsWith("castle_")) {
    errors.push("castle preset ids must use the castle_ prefix");
  }
  return errors;
}

export function canGenerateStructurePresetAt(
  context: StructureTerrainContext,
  presetDefinition: StructurePresetDefinition,
  options: { force?: boolean } = {}
): StructureTerrainCheckResult {
  const messages: string[] = [];
  if (options.force) {
    return { canGenerate: true, force: true, messages: ["force mode enabled; terrain checks bypassed"] };
  }

  const rules = presetDefinition.generationRules;
  const tags = new Set([context.biomeId, ...context.biomeTags]);
  // Preferred biome tags are hints for placement ranking; they do not block debug or fallback generation.
  if (rules.forbiddenBiomeTags?.some((tag) => tags.has(tag))) {
    messages.push(`biome ${context.biomeId} matches forbidden terrain tags`);
  }
  if (rules.requiresWaterNearby && !context.nearWater) {
    messages.push("water nearby is required");
  }
  if (rules.requiresTreesNearby && !context.nearTrees) {
    messages.push("trees nearby are required");
  }
  if (rules.requiresSolidGround && context.groundType !== "solid") {
    messages.push("solid ground is required");
  }
  if (!rules.allowPartialOverWater && context.groundType === "water") {
    messages.push("cannot generate fully over water");
  }
  if (rules.minDistanceFromWater !== undefined && (context.distanceFromWater ?? Number.POSITIVE_INFINITY) < rules.minDistanceFromWater) {
    messages.push(`too close to water; minimum distance is ${rules.minDistanceFromWater}`);
  }
  if (rules.maxDistanceFromWater !== undefined && (context.distanceFromWater ?? Number.POSITIVE_INFINITY) > rules.maxDistanceFromWater) {
    messages.push(`too far from water; maximum distance is ${rules.maxDistanceFromWater}`);
  }
  if (rules.maxSlope && context.slopeScore > slopeLimit(rules.maxSlope)) {
    messages.push(`slope ${context.slopeScore} exceeds ${rules.maxSlope} limit`);
  }

  return { canGenerate: messages.length === 0, force: false, messages };
}

export function validateStructurePresetRegistry(definitions: readonly StructurePresetDefinition[]): string[] {
  const errors: string[] = [];
  const seen = new Set<string>();
  for (const definition of definitions) {
    if (seen.has(definition.id)) errors.push(`duplicate preset id: ${definition.id}`);
    seen.add(definition.id);
    errors.push(...validateStructurePresetDefinition(definition).map((error) => `${definition.id}: ${error}`));
  }
  return errors;
}

export function createStructurePresetIndex(
  options: {
    category?: StructurePresetCategory;
    config?: Partial<StructurePresetCategoryConfig>;
  } = {}
): StructurePresetIndexEntry[] {
  const config = { ...defaultStructurePresetCategoryConfig, ...options.config };
  return structurePresets
    .filter((presetDefinition) => !options.category || presetDefinition.category === options.category)
    .filter((presetDefinition) => categoryEnabled(presetDefinition.category, config))
    .map((presetDefinition) => ({
      id: presetDefinition.id,
      displayName: presetDefinition.displayName,
      category: presetDefinition.category,
      size: presetDefinition.size,
      footprint: presetDefinition.footprint,
      specialGenerationRules: describeSpecialRules(presetDefinition)
    }));
}

export function createStructurePresetModel(presetDefinition: StructurePresetDefinition): VoxelModel & { id: PresetId } {
  const store = new Map<string, VoxelBlock>();
  if (presetDefinition.category === "ruin") {
    buildRuinPreset(store, presetDefinition);
  } else if (presetDefinition.features.includes("counter") && presetDefinition.features.includes("upstairs_beds")) {
    buildInn(store, presetDefinition);
  } else if (presetDefinition.features.includes("large_hall")) {
    buildManor(store, presetDefinition);
  } else if (presetDefinition.features.includes("hay_storage")) {
    buildBarn(store, presetDefinition);
  } else if (presetDefinition.features.includes("horse_stalls")) {
    buildStable(store, presetDefinition);
  } else if (presetDefinition.features.includes("storage_chests")) {
    buildWarehouse(store, presetDefinition);
  } else if (presetDefinition.features.includes("cloth_roof_style")) {
    buildMarketStall(store, presetDefinition);
  } else if (presetDefinition.features.includes("bell_tower")) {
    buildChapel(store, presetDefinition);
  } else if (presetDefinition.features.includes("crystal_roof")) {
    buildWizardTower(store, presetDefinition);
  } else if (presetDefinition.features.includes("tree_trunk_support")) {
    buildTreehouse(store, presetDefinition);
  } else if (presetDefinition.features.includes("wooden_dock")) {
    buildDockHouse(store, presetDefinition);
  } else if (presetDefinition.features.includes("multi_tier_roof")) {
    buildPagoda(store, presetDefinition);
  } else if (presetDefinition.features.includes("flat_roof")) {
    buildFlatRoofHouse(store, presetDefinition);
  } else if (presetDefinition.features.includes("steep_roof")) {
    buildSnowCabin(store, presetDefinition);
  } else if (presetDefinition.features.includes("stilts")) {
    buildStiltHut(store, presetDefinition);
  } else if (presetDefinition.features.includes("stone_foundation")) {
    buildMountainLodge(store, presetDefinition);
  } else if (presetDefinition.features.includes("wide_roof")) {
    buildJapaneseHouse(store, presetDefinition);
  } else if (presetDefinition.features.includes("central_keep")) {
    buildCastleKeep(store, presetDefinition);
  } else if (presetDefinition.features.includes("double_towers")) {
    buildCastleGatehouse(store, presetDefinition);
  } else if (presetDefinition.features.includes("curtain_wall")) {
    buildCastleWallSegment(store, presetDefinition);
  } else if (presetDefinition.features.includes("square_tower")) {
    buildCastleCornerTower(store, presetDefinition);
  } else if (presetDefinition.features.includes("open_yard")) {
    buildCastleCourtyard(store, presetDefinition);
  } else if (presetDefinition.features.includes("outer_wall")) {
    buildSmallFort(store, presetDefinition);
  } else if (presetDefinition.features.includes("tower") || presetDefinition.features.includes("tall_tower")) {
    buildTower(store, presetDefinition);
  } else if (presetDefinition.features.includes("wall") && !presetDefinition.features.includes("courtyard")) {
    buildWallSegment(store, presetDefinition);
  } else if (presetDefinition.features.includes("courtyard")) {
    buildCourtyard(store, presetDefinition);
  } else {
    buildHouseLike(store, presetDefinition);
  }

  applyFeatureDetails(store, presetDefinition);
  if (presetDefinition.category !== "ruin") {
    applyRuin(store, presetDefinition);
  }

  return {
    id: presetDefinition.id,
    name: presetDefinition.displayName,
    description: `${presetDefinition.displayName} preset with ${presetDefinition.category.replace("_", " ")} tags and ${presetDefinition.generationRules.biome} palette.`,
    size: presetDefinition.modelSize,
    blocks: blocksFromStore(store)
  };
}

function preset(
  id: PresetId,
  displayName: string,
  category: StructurePresetCategory,
  size: StructurePresetSize,
  width: number,
  depth: number,
  height: number,
  biome: StructurePresetBiome,
  features: StructurePresetFeature[],
  overrides: StructurePresetOptions = {}
): StructurePresetDefinition {
  const palette = { ...biomePalettes[biome], ...pickPaletteOverrides(overrides) };
  const extraDepth = hasFeature(features, ["dock", "wooden_dock"]) ? 4 : 0;
  return {
    id,
    displayName,
    category,
    size,
    modelSize: { width, height, depth: depth + extraDepth },
    footprint: { width, depth, height },
    palette,
    features,
    suitable: overrides.suitable,
    connectors: overrides.connectors,
    attachmentPoints: overrides.attachmentPoints ?? createAttachmentPoints(id, { width, depth, height }, overrides.connectors),
    generationRules: {
      biome,
      flattenFoundation: overrides.flattenFoundation ?? true,
      allowTerrainFlattening: overrides.allowTerrainFlattening,
      foundationDepth: overrides.foundationDepth,
      minClearanceHeight: overrides.minClearanceHeight,
      preferredBiomeTags: overrides.preferredBiomeTags ?? [biome],
      forbiddenBiomeTags: overrides.forbiddenBiomeTags,
      maxSlope: overrides.maxSlope,
      requiresWaterNearby: overrides.requiresWaterNearby,
      requiresTreesNearby: overrides.requiresTreesNearby,
      allowPartialOverWater: overrides.allowPartialOverWater,
      requiresSolidGround: overrides.requiresSolidGround,
      minDistanceFromWater: overrides.minDistanceFromWater,
      maxDistanceFromWater: overrides.maxDistanceFromWater,
      globalAllowed: overrides.globalAllowed,
      avoidWater: overrides.avoidWater ?? !overrides.requiresWater,
      requiresWater: overrides.requiresWater,
      requiresForest: overrides.requiresForest,
      prefersMountain: overrides.prefersMountain,
      requiresFlatArea: overrides.requiresFlatArea,
      ruinLevel: overrides.ruinLevel,
      ruinDecayLevel: overrides.ruinDecayLevel,
      floors: overrides.floors,
      towerRadius: overrides.towerRadius
    }
  };
}

function pickPaletteOverrides(overrides: StructurePresetOptions): Partial<StructurePalette> {
  const paletteOverrides: Partial<StructurePalette> = {};
  for (const key of ["foundation", "wall", "trim", "roof", "accent", "glass", "floor"] as const) {
    if (overrides[key]) {
      paletteOverrides[key] = overrides[key];
    }
  }
  return paletteOverrides;
}

function createAttachmentPoints(
  id: PresetId,
  footprint: StructureFootprint,
  connectors?: Partial<Record<StructureConnectorDirection, PresetId[]>>
): StructureAttachmentPoint[] | undefined {
  if (!connectors) return undefined;
  const midpointX = Math.floor(footprint.width / 2);
  const midpointZ = Math.floor(footprint.depth / 2);
  const offsets: Record<StructureConnectorDirection, [number, number, number]> = {
    north: [midpointX, 1, 0],
    south: [midpointX, 1, footprint.depth - 1],
    east: [footprint.width - 1, 1, midpointZ],
    west: [0, 1, midpointZ],
    up: [midpointX, footprint.height - 1, midpointZ],
    down: [midpointX, 0, midpointZ]
  };
  return Object.entries(connectors).map(([direction, compatibleWith]) => {
    const [offsetX, offsetY, offsetZ] = offsets[direction as StructureConnectorDirection];
    return {
      id: `${id}:${direction}`,
      direction: direction as StructureConnectorDirection,
      offsetX,
      offsetY,
      offsetZ,
      compatibleWith: compatibleWith ?? []
    };
  });
}

function buildHouseLike(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth, height } = definition.footprint;
  const wallTop = Math.max(3, height - 4);
  fillRect(store, 0, 0, 0, width, depth, definition.palette.foundation);
  for (let y = 1; y <= wallTop; y += 1) {
    for (let x = 0; x < width; x += 1) {
      for (let z = 0; z < depth; z += 1) {
        const wall = x === 0 || x === width - 1 || z === 0 || z === depth - 1;
        if (!wall) continue;
        const corner = (x === 0 || x === width - 1) && (z === 0 || z === depth - 1);
        setBlock(store, x, y, z, corner ? definition.palette.trim : definition.palette.wall);
      }
    }
  }
  buildPitchedRoof(store, definition, wallTop + 1);
}

function buildTower(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth, height } = definition.footprint;
  const min = 1;
  const maxX = width - 2;
  const maxZ = depth - 2;
  for (let y = 0; y < height - 2; y += 1) {
    for (let x = min; x <= maxX; x += 1) {
      for (let z = min; z <= maxZ; z += 1) {
        const wall = x === min || x === maxX || z === min || z === maxZ;
        const floor = y === 0 || y % 5 === 0;
        if (wall || floor) setBlock(store, x, y, z, floor ? definition.palette.floor : definition.palette.wall);
      }
    }
  }
  buildBattlements(store, definition, height - 2);
}

function buildWallSegment(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth, height } = definition.footprint;
  for (let x = 0; x < width; x += 1) {
    for (let z = 0; z < depth; z += 1) {
      setBlock(store, x, 0, z, definition.palette.foundation);
      for (let y = 1; y < height - 1; y += 1) {
        if (z === 0 || z === depth - 1 || y < 3) setBlock(store, x, y, z, definition.palette.wall);
      }
    }
  }
  buildBattlements(store, definition, height - 1);
}

function buildCourtyard(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth, height } = definition.footprint;
  fillRect(store, 0, 0, 0, width, depth, definition.palette.floor);
  for (let y = 1; y <= Math.max(3, height - 2); y += 1) {
    for (let x = 0; x < width; x += 1) {
      setBlock(store, x, y, 0, definition.palette.wall);
      setBlock(store, x, y, depth - 1, definition.palette.wall);
    }
    for (let z = 0; z < depth; z += 1) {
      setBlock(store, 0, y, z, definition.palette.wall);
      setBlock(store, width - 1, y, z, definition.palette.wall);
    }
  }
  buildBattlements(store, definition, height - 1);
}

function buildWizardTower(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  buildTower(store, definition);
  const { width, depth, height } = definition.footprint;
  const centerX = Math.floor(width / 2);
  const centerZ = Math.floor(depth / 2);
  for (let y = 1; y < height - 3; y += 1) {
    if (y % 2 === 0) setBlock(store, centerX + ((y / 2) % 2 === 0 ? 1 : -1), y, centerZ, "ladder");
  }
  for (let x = 2; x < width - 2; x += 1) {
    setBlock(store, x, Math.floor(height / 2), 2, "bookshelf");
    setBlock(store, x, Math.floor(height / 2), depth - 3, "bookshelf");
  }
  setBlock(store, centerX, Math.floor(height / 2), centerZ, "amethyst_block");
  for (let x = 2; x < width - 2; x += 1) setBlock(store, x, height - 4, depth - 1, definition.palette.roof);
  setBlock(store, centerX, height - 1, centerZ, "amethyst_block");
  setBlock(store, centerX, height - 2, centerZ, "purple_stained_glass");
}

function buildTreehouse(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth, height } = definition.footprint;
  const centerX = Math.floor(width / 2);
  const centerZ = Math.floor(depth / 2);
  for (let y = 0; y <= 8; y += 1) setBlock(store, centerX, y, centerZ, "oak_log");
  fillRect(store, 2, 8, 2, width - 4, depth - 4, definition.palette.floor);
  for (let y = 9; y <= 12; y += 1) {
    for (let x = 2; x < width - 2; x += 1) {
      setBlock(store, x, y, 2, definition.palette.wall);
      setBlock(store, x, y, depth - 3, definition.palette.wall);
    }
    for (let z = 2; z < depth - 2; z += 1) {
      setBlock(store, 2, y, z, definition.palette.wall);
      setBlock(store, width - 3, y, z, definition.palette.wall);
    }
  }
  for (let x = 1; x < width - 1; x += 1) {
    for (let z = 1; z < depth - 1; z += 1) if ((x + z) % 2 === 0) setBlock(store, x, height - 2, z, "leaves");
  }
  for (let y = 1; y <= 8; y += 1) setBlock(store, centerX + 1, y, centerZ, "ladder");
  for (let x = 0; x < 4; x += 1) setBlock(store, x, 8, centerZ, definition.palette.floor);
}

function buildDockHouse(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth, height } = definition.footprint;
  for (let x = 0; x < definition.modelSize.width; x += 1) {
    for (let z = depth - 4; z < definition.modelSize.depth; z += 1) setBlock(store, x, 0, z, "water");
  }
  fillRect(store, 1, 1, 1, width - 2, depth - 6, definition.palette.floor);
  for (let y = 2; y <= height - 4; y += 1) {
    for (let x = 1; x < width - 1; x += 1) {
      setBlock(store, x, y, 1, definition.palette.wall);
      setBlock(store, x, y, depth - 6, definition.palette.wall);
    }
    for (let z = 1; z <= depth - 6; z += 1) {
      setBlock(store, 1, y, z, definition.palette.wall);
      setBlock(store, width - 2, y, z, definition.palette.wall);
    }
  }
  buildPitchedRoof(store, definition, height - 3);
  for (let x = Math.floor(width / 2) - 2; x <= Math.floor(width / 2) + 2; x += 1) {
    for (let z = depth - 5; z < definition.modelSize.depth; z += 1) setBlock(store, x, 1, z, definition.palette.floor);
  }
  setBlock(store, 2, 2, depth - 6, "barrel");
  setBlock(store, width - 3, 2, depth - 6, "lantern");
}

function buildFlatRoofHouse(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth, height } = definition.footprint;
  fillRect(store, 0, 0, 0, width, depth, definition.palette.foundation);
  for (let y = 1; y <= height - 3; y += 1) {
    for (let x = 0; x < width; x += 1) {
      setBlock(store, x, y, 0, definition.palette.wall);
      setBlock(store, x, y, depth - 1, definition.palette.wall);
    }
    for (let z = 0; z < depth; z += 1) {
      setBlock(store, 0, y, z, definition.palette.wall);
      setBlock(store, width - 1, y, z, definition.palette.wall);
    }
  }
  fillRect(store, 0, height - 2, 0, width, depth, definition.palette.roof);
  for (let x = 2; x < width - 2; x += 1) setBlock(store, x, 1, depth - 3, "sandstone");
}

function buildSnowCabin(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  buildHouseLike(store, definition);
  const { width, depth, height } = definition.footprint;
  for (let x = 0; x < width; x += 1) {
    setBlock(store, x, height - 1, 0, "snow");
    setBlock(store, x, height - 1, depth - 1, "snow");
  }
  setBlock(store, width - 3, 1, depth - 3, "stone");
  setBlock(store, width - 3, 2, depth - 3, "lantern");
  setBlock(store, 1, 2, 1, "lantern");
}

function buildStiltHut(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth, height } = definition.footprint;
  setBlock(store, 0, 0, 0, definition.palette.foundation);
  setBlock(store, width - 1, 0, depth - 1, definition.palette.foundation);
  for (const [x, z] of [[1, 1], [width - 2, 1], [1, depth - 2], [width - 2, depth - 2]] as const) {
    for (let y = 0; y <= 2; y += 1) setBlock(store, x, y, z, "mangrove_log");
  }
  fillRect(store, 1, 3, 1, width - 2, depth - 2, definition.palette.floor);
  for (let y = 4; y <= height - 3; y += 1) {
    for (let x = 1; x < width - 1; x += 1) {
      setBlock(store, x, y, 1, definition.palette.wall);
      setBlock(store, x, y, depth - 2, definition.palette.wall);
    }
    for (let z = 1; z < depth - 1; z += 1) {
      setBlock(store, 1, y, z, definition.palette.wall);
      setBlock(store, width - 2, y, z, definition.palette.wall);
    }
  }
  buildPitchedRoof(store, definition, height - 2);
  setBlock(store, Math.floor(width / 2), 2, 0, "mud");
  setBlock(store, Math.floor(width / 2), 5, 1, "lantern");
}

function buildMountainLodge(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  buildHouseLike(store, definition);
  const { width, depth, height } = definition.footprint;
  for (let x = 0; x < width; x += 1) {
    for (let z = 0; z < depth; z += 1) if (x === 0 || z === 0 || x === width - 1 || z === depth - 1) setBlock(store, x, 1, z, "stone");
  }
  for (let x = 3; x < width - 3; x += 1) setBlock(store, x, height - 4, depth - 1, "dark_oak_fence");
  setBlock(store, 2, 1, depth - 3, "barrel");
}

function buildJapaneseHouse(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  buildHouseLike(store, definition);
  const { width, depth, height } = definition.footprint;
  for (let x = 0; x < width; x += 1) {
    setBlock(store, x, height - 2, 0, definition.palette.roof);
    setBlock(store, x, height - 2, depth - 1, definition.palette.roof);
  }
  for (const [x, z] of [[2, 0], [width - 3, 0], [0, 3], [width - 1, 3]] as const) setBlock(store, x, 2, z, "white_concrete");
  for (let z = 0; z < 4; z += 1) setBlock(store, Math.floor(width / 2), 0, z, "bamboo");
}

function buildPagoda(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth, height } = definition.footprint;
  const centerX = Math.floor(width / 2);
  const centerZ = Math.floor(depth / 2);
  for (let y = 0; y < height - 2; y += 1) {
    const tier = Math.floor(y / 5);
    const inset = Math.min(3, tier);
    for (let x = inset + 1; x < width - inset - 1; x += 1) {
      for (let z = inset + 1; z < depth - inset - 1; z += 1) {
        const wall = x === inset + 1 || x === width - inset - 2 || z === inset + 1 || z === depth - inset - 2;
        if (wall || y % 5 === 0) setBlock(store, x, y, z, wall ? definition.palette.wall : definition.palette.floor);
      }
    }
    setBlock(store, centerX, y, centerZ, "stripped_dark_oak_log");
    if (y > 0 && y % 5 === 0) {
      for (let x = inset; x < width - inset; x += 1) {
        setBlock(store, x, y, inset, definition.palette.roof);
        setBlock(store, x, y, depth - inset - 1, definition.palette.roof);
      }
      for (let z = inset; z < depth - inset; z += 1) {
        setBlock(store, inset, y, z, definition.palette.roof);
        setBlock(store, width - inset - 1, y, z, definition.palette.roof);
      }
    }
  }
  setBlock(store, centerX, height - 1, centerZ, "lantern");
}

function buildInn(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  buildHouseLike(store, definition);
  const { width, depth, height } = definition.footprint;
  for (let x = 3; x < width - 3; x += 3) {
    setBlock(store, x, 1, Math.floor(depth / 2), "barrel");
    setBlock(store, x, 1, Math.floor(depth / 2) + 1, "oak_planks");
  }
  for (let x = width - 5; x < width - 2; x += 1) setBlock(store, x, 1, 2, "barrel");
  for (let x = 3; x < width - 3; x += 2) setBlock(store, x, height - 5, depth - 3, "wool_white");
  setBlock(store, width - 3, 1, depth - 3, "stone");
  setBlock(store, width - 3, 2, depth - 3, "lantern");
}

function buildManor(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  buildHouseLike(store, definition);
  const { width, depth, height } = definition.footprint;
  for (let x = 4; x < width - 4; x += 1) setBlock(store, x, 1, Math.floor(depth / 2), definition.palette.floor);
  for (let x = 5; x < width - 5; x += 1) setBlock(store, x, height - 4, depth - 1, "dark_oak_fence");
  for (let z = 0; z < 5; z += 1) setBlock(store, Math.floor(width / 2), 0, z, "grass");
  setBlock(store, 3, 1, depth - 3, "bookshelf");
  setBlock(store, width - 4, 1, depth - 3, "chest");
}

function buildBarn(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth, height } = definition.footprint;
  fillRect(store, 0, 0, 0, width, depth, "cobblestone");
  for (let y = 1; y <= height - 5; y += 1) {
    for (let x = 0; x < width; x += 1) {
      setBlock(store, x, y, 0, definition.palette.wall);
      setBlock(store, x, y, depth - 1, definition.palette.wall);
    }
    for (let z = 0; z < depth; z += 1) {
      setBlock(store, 0, y, z, definition.palette.wall);
      setBlock(store, width - 1, y, z, definition.palette.wall);
    }
  }
  for (let y = 1; y <= 4; y += 1) {
    setBlock(store, Math.floor(width / 2), y, 0, "door");
    setBlock(store, Math.floor(width / 2) - 1, y, 0, "door");
  }
  buildPitchedRoof(store, definition, height - 4);
  for (let x = 2; x < width - 2; x += 3) setBlock(store, x, 1, depth - 3, "hay_block");
  for (let z = 2; z < depth - 2; z += 3) setBlock(store, 2, 1, z, "fence");
}

function buildStable(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  buildHouseLike(store, definition);
  const { width, depth } = definition.footprint;
  for (let z = 2; z < depth - 2; z += 2) {
    setBlock(store, 2, 1, z, "fence");
    setBlock(store, 5, 1, z, "fence");
    setBlock(store, width - 3, 1, z, "hay_block");
  }
  setBlock(store, 1, 1, depth - 2, "water");
  setBlock(store, 2, 1, depth - 2, "water");
  setBlock(store, width - 2, 1, 1, "barrel");
}

function buildWarehouse(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  buildHouseLike(store, definition);
  const { width, depth } = definition.footprint;
  for (let y = 1; y <= 4; y += 1) {
    setBlock(store, Math.floor(width / 2), y, 0, "door");
    setBlock(store, Math.floor(width / 2) - 1, y, 0, "door");
  }
  for (let x = 3; x < width - 3; x += 3) {
    for (let z = 3; z < depth - 3; z += 3) {
      setBlock(store, x, 1, z, "chest");
      setBlock(store, x + 1, 1, z, "barrel");
    }
  }
}

function buildMarketStall(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth, height } = definition.footprint;
  fillRect(store, 0, 0, 0, width, depth, "grass");
  for (const [x, z] of [[0, 0], [width - 1, 0], [0, depth - 1], [width - 1, depth - 1]] as const) {
    for (let y = 1; y < height - 1; y += 1) setBlock(store, x, y, z, "fence");
  }
  for (let x = 0; x < width; x += 1) {
    for (let z = 0; z < depth; z += 1) if ((x + z) % 2 === 0) setBlock(store, x, height - 1, z, definition.palette.roof);
  }
  for (let x = 1; x < width - 1; x += 1) setBlock(store, x, 1, 1, "oak_planks");
  setBlock(store, 1, 1, depth - 2, "barrel");
  setBlock(store, width - 2, 1, depth - 2, "chest");
}

function buildChapel(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  buildHouseLike(store, definition);
  const { width, depth, height } = definition.footprint;
  for (let y = 1; y < height; y += 1) {
    for (let x = 1; x <= 3; x += 1) {
      for (let z = 1; z <= 3; z += 1) {
        const wall = x === 1 || x === 3 || z === 1 || z === 3;
        if (wall || y % 4 === 0) setBlock(store, x, y, z, definition.palette.wall);
      }
    }
  }
  setBlock(store, 2, height - 1, 2, "lantern");
  setBlock(store, Math.floor(width / 2), 3, 0, "glass_pane");
  setBlock(store, Math.floor(width / 2), 4, depth - 1, "glass_pane");
  for (let z = 4; z < depth - 3; z += 3) {
    setBlock(store, 3, 1, z, "oak_planks");
    setBlock(store, width - 4, 1, z, "oak_planks");
  }
  setBlock(store, Math.floor(width / 2), 1, depth - 3, "smooth_stone");
  setBlock(store, Math.floor(width / 2), 2, depth - 3, "lantern");
}

function buildRuinPreset(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  if (definition.features.includes("broken_tower")) {
    buildTower(store, definition);
  } else if (definition.features.includes("broken_outer_wall")) {
    buildSmallFort(store, definition);
  } else {
    buildHouseLike(store, definition);
  }
  applyRuin(store, definition);
  const { width, depth } = definition.footprint;
  for (let x = 1; x < width - 1; x += 3) {
    for (let z = 1; z < depth - 1; z += 4) setBlock(store, x, 1, z, (x + z) % 2 === 0 ? "mossy_cobblestone" : "cobweb");
  }
  setBlock(store, 1, 1, 1, "vine");
  if (definition.features.includes("abandoned_chest_chance") || definition.features.includes("loot_chest_chance")) {
    setBlock(store, Math.max(1, width - 3), 1, Math.max(1, depth - 3), "chest");
  }
  if (definition.features.includes("cracked_walls") || definition.features.includes("broken_tower")) {
    setBlock(store, Math.floor(width / 2), 2, 1, "cracked_stone_bricks");
  }
}

function buildCastleKeep(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth, height } = definition.footprint;
  fillRect(store, 0, 0, 0, width, depth, definition.palette.foundation);
  for (let y = 1; y < height - 3; y += 1) {
    for (let x = 1; x < width - 1; x += 1) {
      for (let z = 1; z < depth - 1; z += 1) {
        const wall = x === 1 || x === width - 2 || z === 1 || z === depth - 2;
        const floor = y % 5 === 0;
        if (wall || floor) setBlock(store, x, y, z, floor ? definition.palette.floor : castleWallBlock(x, y, z, definition));
      }
    }
  }
  for (let x = 4; x < width - 4; x += 1) {
    for (let z = 4; z < depth - 4; z += 1) setBlock(store, x, 1, z, definition.palette.floor);
  }
  setBlock(store, Math.floor(width / 2), 1, Math.floor(depth / 2), "barrel");
  setBlock(store, Math.floor(width / 2) + 1, 1, Math.floor(depth / 2), "chest");
  buildBattlements(store, definition, height - 3);
}

function buildCastleGatehouse(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth, height } = definition.footprint;
  fillRect(store, 0, 0, 0, width, depth, definition.palette.foundation);
  const leftTowerEnd = 4;
  const rightTowerStart = width - 5;
  for (let y = 1; y < height - 2; y += 1) {
    for (let x = 0; x < width; x += 1) {
      for (let z = 0; z < depth; z += 1) {
        const inLeftTower = x <= leftTowerEnd;
        const inRightTower = x >= rightTowerStart;
        const inBridgeWall = z === 0 || z === depth - 1 || y >= 6;
        const gateGap = x >= 6 && x <= 8 && z === 0 && y <= 4;
        if ((inLeftTower || inRightTower || inBridgeWall) && !gateGap) {
          setBlock(store, x, y, z, castleWallBlock(x, y, z, definition));
        }
      }
    }
  }
  for (let y = 1; y <= 5; y += 1) setBlock(store, Math.floor(width / 2), y, 0, y % 2 === 0 ? "iron_bars" : "chain");
  for (const x of [2, width - 3]) setBlock(store, x, 5, 1, "lantern");
  buildBattlements(store, definition, height - 2);
}

function buildCastleWallSegment(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth, height } = definition.footprint;
  fillRect(store, 0, 0, 0, width, depth, definition.palette.foundation);
  for (let x = 0; x < width; x += 1) {
    for (let y = 1; y < height - 1; y += 1) {
      for (let z = 0; z < depth; z += 1) {
        const face = z === 0 || z === depth - 1;
        const core = z === 1 && y <= 5;
        if (face || core) setBlock(store, x, y, z, castleWallBlock(x, y, z, definition));
      }
    }
    setBlock(store, x, height - 2, 1, definition.palette.floor);
    if (x % 4 === 2) setBlock(store, x, height - 1, 1, "torch");
  }
  buildBattlements(store, definition, height - 1);
}

function buildCastleCornerTower(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  buildTower(store, definition);
  const { width, depth, height } = definition.footprint;
  for (let y = 1; y < height - 2; y += 1) setBlock(store, Math.floor(width / 2), y, Math.floor(depth / 2), "ladder");
  for (let x = 1; x < width; x += 1) setBlock(store, x, 3, 0, definition.palette.wall);
  for (let z = 1; z < depth; z += 1) setBlock(store, width - 1, 3, z, definition.palette.wall);
}

function buildCastleCourtyard(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth } = definition.footprint;
  fillRect(store, 0, 0, 0, width, depth, definition.palette.floor);
  for (let x = 0; x < width; x += 1) {
    setBlock(store, x, 1, 0, definition.palette.wall);
    setBlock(store, x, 1, depth - 1, definition.palette.wall);
  }
  for (let z = 0; z < depth; z += 1) {
    setBlock(store, 0, 1, z, definition.palette.wall);
    setBlock(store, width - 1, 1, z, definition.palette.wall);
  }
  const centerX = Math.floor(width / 2);
  const centerZ = Math.floor(depth / 2);
  setBlock(store, centerX, 1, centerZ, "water");
  setBlock(store, centerX - 1, 1, centerZ, "stone_bricks");
  setBlock(store, centerX + 1, 1, centerZ, "stone_bricks");
  for (const [x, z] of [[4, 4], [width - 5, 4], [4, depth - 5], [width - 5, depth - 5]] as const) {
    setBlock(store, x, 1, z, "dark_oak_fence");
    setBlock(store, x, 2, z, "lantern");
  }
  for (let x = 3; x <= 6; x += 1) setBlock(store, x, 1, depth - 4, "barrel");
  for (let z = 3; z <= 6; z += 1) setBlock(store, width - 4, 1, z, "hay_block");
}

function buildSmallFort(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth, height } = definition.footprint;
  fillRect(store, 0, 0, 0, width, depth, definition.palette.floor);
  for (let y = 1; y <= 6; y += 1) {
    for (let x = 0; x < width; x += 1) {
      const gateGap = x >= Math.floor(width / 2) - 1 && x <= Math.floor(width / 2) + 1 && y <= 4;
      if (!gateGap) setBlock(store, x, y, 0, definition.palette.wall);
      setBlock(store, x, y, depth - 1, definition.palette.wall);
    }
    for (let z = 0; z < depth; z += 1) {
      setBlock(store, 0, y, z, definition.palette.wall);
      setBlock(store, width - 1, y, z, definition.palette.wall);
    }
  }
  buildBattlements(store, definition, 7);
  for (const [startX, startZ] of [[1, 1], [width - 5, 1], [1, depth - 5], [width - 5, depth - 5]] as const) {
    for (let y = 1; y <= height - 3; y += 1) {
      for (let x = startX; x < startX + 4; x += 1) {
        for (let z = startZ; z < startZ + 4; z += 1) {
          const wall = x === startX || x === startX + 3 || z === startZ || z === startZ + 3;
          if (wall || y % 5 === 0) setBlock(store, x, y, z, wall ? definition.palette.wall : definition.palette.floor);
        }
      }
    }
  }
  for (let x = 7; x <= 12; x += 1) {
    for (let z = depth - 6; z <= depth - 3; z += 1) setBlock(store, x, 1, z, x === 7 || x === 12 || z === depth - 6 || z === depth - 3 ? definition.palette.wall : definition.palette.floor);
  }
  setBlock(store, 8, 1, depth - 5, "barrel");
  setBlock(store, 9, 1, depth - 5, "chest");
  for (let y = 1; y <= 5; y += 1) setBlock(store, Math.floor(width / 2), y, 0, "iron_bars");
}

function applyFeatureDetails(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const { width, depth, height } = definition.footprint;
  const centerX = Math.floor(width / 2);
  const centerZ = Math.floor(depth / 2);

  if (hasAnyFeature(definition, ["door", "gate", "simple_room", "main_room", "main_hall", "central_keep", "forge_area"])) {
    const doorHeight = definition.features.includes("gate") ? 4 : 2;
    for (let y = 1; y <= doorHeight; y += 1) {
      setBlock(store, centerX, y, 0, "door");
      if (definition.features.includes("gate") && centerX > 0) setBlock(store, centerX - 1, y, 0, "door");
    }
    setBlock(store, Math.min(width - 2, centerX + 1), 2, 1, definition.palette.accent);
  }
  if (hasAnyFeature(definition, ["windows", "small_windows"])) {
    for (const [x, z] of [[2, 0], [width - 3, 0], [0, centerZ], [width - 1, centerZ]] as const) {
      if (x >= 0 && x < width && z >= 0 && z < depth) {
        setBlock(store, x, 2, z, definition.palette.glass);
        if (height > 9) setBlock(store, x, 5, z, definition.palette.glass);
      }
    }
  }
  if (definition.features.includes("arrow_slits")) {
    for (const y of [4, 8, 12].filter((value) => value < height - 1)) {
      setBlock(store, centerX, y, 1, "iron_bars");
      setBlock(store, centerX, y, depth - 2, "iron_bars");
      setBlock(store, 1, y, centerZ, "iron_bars");
      setBlock(store, width - 2, y, centerZ, "iron_bars");
    }
  }
  if (hasAnyFeature(definition, ["chimney", "fireplace", "forge_area"])) {
    for (let y = Math.max(1, height - 4); y < height; y += 1) setBlock(store, width - 3, y, depth - 3, definition.palette.foundation);
    if (definition.features.includes("fireplace")) {
      setBlock(store, width - 3, 1, depth - 3, "stone");
      setBlock(store, width - 3, 2, depth - 3, "torch");
    }
  }
  if (definition.features.includes("front_path")) {
    for (let z = 0; z < Math.min(3, depth); z += 1) {
      setBlock(store, centerX, 0, z, "cobblestone");
      if (centerX > 0) setBlock(store, centerX - 1, 0, z, "cobblestone");
    }
  }
  if (definition.features.includes("crop_patch")) {
    for (let x = 1; x < Math.min(width - 1, 6); x += 1) {
      for (let z = Math.max(1, depth - 4); z < depth - 1; z += 1) {
        setBlock(store, x, 1, z, (x + z) % 2 === 0 ? "hay_block" : "grass");
      }
    }
  }
  if (definition.features.includes("fenced_yard")) {
    for (let x = 0; x < width; x += 1) {
      setBlock(store, x, 1, depth - 1, "fence");
    }
    for (let z = 1; z < depth; z += 1) {
      setBlock(store, 0, 1, z, "fence");
      setBlock(store, width - 1, 1, z, "fence");
    }
  }
  if (definition.features.includes("storage_barrels")) {
    setBlock(store, width - 3, 1, depth - 3, "barrel");
    setBlock(store, width - 4, 1, depth - 3, "barrel");
  }
  if (definition.features.includes("forge_area")) {
    for (let x = width - 4; x < width - 1; x += 1) {
      setBlock(store, x, 1, depth - 2, "stone");
    }
    setBlock(store, width - 3, 2, depth - 2, "iron_bars");
    setBlock(store, width - 2, 2, depth - 2, "chain");
  }
  if (definition.features.includes("anvil")) setBlock(store, centerX - 1, 1, centerZ, "anvil");
  if (definition.features.includes("blast_furnace")) setBlock(store, centerX + 1, 1, centerZ, "blast_furnace");
  if (definition.features.includes("water_trough")) {
    setBlock(store, 1, 1, depth - 2, "water");
    setBlock(store, 2, 1, depth - 2, "water");
  }
  if (definition.features.includes("storage_chest")) setBlock(store, width - 2, 1, 1, "chest");
  if (definition.features.includes("open_work_area")) {
    for (let x = 1; x < width - 1; x += 1) setBlock(store, x, 1, 1, definition.palette.floor);
  }
  if (definition.features.includes("ladder_access")) {
    for (let y = 1; y < height - 2; y += 1) setBlock(store, centerX, y, centerZ, "ladder");
  }
  if (definition.features.includes("top_platform")) {
    for (let x = 1; x < width - 1; x += 1) {
      for (let z = 1; z < depth - 1; z += 1) setBlock(store, x, height - 2, z, definition.palette.roof);
    }
  }
  if (definition.features.includes("dock")) {
    for (let x = centerX - 2; x <= centerX + 2; x += 1) {
      for (let z = depth - 1; z < Math.min(definition.modelSize.depth, depth + 4); z += 1) {
        setBlock(store, x, 1, z, definition.palette.floor);
      }
    }
    for (let x = 0; x < definition.modelSize.width; x += 1) {
      for (let z = depth; z < definition.modelSize.depth; z += 1) setBlock(store, x, 0, z, "water");
    }
  }
  if (definition.features.includes("tree_support")) {
    for (let y = 0; y < Math.min(7, height); y += 1) setBlock(store, centerX, y, centerZ, "oak_log");
    for (let x = centerX - 3; x <= centerX + 3; x += 1) {
      for (let z = centerZ - 3; z <= centerZ + 3; z += 1) {
        if (x >= 0 && z >= 0 && x < definition.modelSize.width && z < definition.modelSize.depth) setBlock(store, x, Math.min(height - 1, 7), z, "leaves");
      }
    }
  }
  if (definition.features.includes("stalls")) {
    for (let x = 2; x < width - 2; x += 4) {
      setBlock(store, x, 1, depth - 2, "hay_block");
      setBlock(store, x + 1, 1, depth - 2, "hay_block");
    }
  }
}

function applyRuin(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition): void {
  const ruinLevel = definition.generationRules.ruinLevel ?? ruinDecayNumber(definition.generationRules.ruinDecayLevel);
  if (ruinLevel <= 0) return;
  for (const block of Array.from(store.values())) {
    const edgeChip = (block.x + block.y * 2 + block.z * 3) % (7 - Math.min(5, ruinLevel)) === 0;
    const upper = block.y > Math.floor(definition.footprint.height / 2);
    const roofDamage = definition.features.includes("missing_roof") && block.y >= definition.footprint.height - 3;
    const topMissing = definition.features.includes("top_missing_section") && block.y >= definition.footprint.height - 5;
    if ((edgeChip && upper) || roofDamage || topMissing) store.delete(`${block.x}:${block.y}:${block.z}`);
    if (!upper && edgeChip && block.block === definition.palette.wall) {
      setBlock(store, block.x, block.y, block.z, (block.x + block.z) % 2 === 0 ? "mossy_stone_bricks" : "cracked_stone_bricks");
    }
  }
}

function buildPitchedRoof(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition, baseY: number): void {
  const { width, depth, height } = definition.footprint;
  const roofLevels = Math.max(2, height - baseY);
  for (let level = 0; level < roofLevels; level += 1) {
    const y = Math.min(height - 1, baseY + level);
    const inset = Math.min(level, Math.floor(Math.min(width, depth) / 2) - 1);
    for (let x = inset; x < width - inset; x += 1) {
      setBlock(store, x, y, inset, definition.palette.roof);
      setBlock(store, x, y, depth - 1 - inset, definition.palette.roof);
    }
  }
}

function buildBattlements(store: Map<string, VoxelBlock>, definition: StructurePresetDefinition, y: number): void {
  const { width, depth } = definition.footprint;
  for (let x = 0; x < width; x += 2) {
    setBlock(store, x, y, 0, definition.palette.wall);
    setBlock(store, x, y, depth - 1, definition.palette.wall);
  }
  for (let z = 0; z < depth; z += 2) {
    setBlock(store, 0, y, z, definition.palette.wall);
    setBlock(store, width - 1, y, z, definition.palette.wall);
  }
}

function fillRect(store: Map<string, VoxelBlock>, startX: number, y: number, startZ: number, width: number, depth: number, block: BlockType): void {
  for (let x = startX; x < startX + width; x += 1) {
    for (let z = startZ; z < startZ + depth; z += 1) setBlock(store, x, y, z, block);
  }
}

function castleWallBlock(x: number, y: number, z: number, definition: StructurePresetDefinition): BlockType {
  const variant = (x * 5 + y * 3 + z) % 11;
  if (variant === 0) return "mossy_stone_bricks";
  if (variant === 3) return "cracked_stone_bricks";
  return definition.palette.wall;
}

function hasAnyFeature(definition: StructurePresetDefinition, features: StructurePresetFeature[]): boolean {
  return features.some((feature) => definition.features.includes(feature));
}

function hasFeature(features: StructurePresetFeature[], candidates: StructurePresetFeature[]): boolean {
  return candidates.some((feature) => features.includes(feature));
}

function slopeLimit(limit: StructureSlopeLimit): number {
  if (limit === "low") return 0.2;
  if (limit === "medium") return 0.45;
  return 0.8;
}

function ruinDecayNumber(level?: StructureRuinDecayLevel): number {
  if (level === "low") return 2;
  if (level === "medium") return 4;
  if (level === "high") return 5;
  return 0;
}

function categoryEnabled(category: StructurePresetCategory, config: StructurePresetCategoryConfig): boolean {
  if (category === "residential") return config.enableResidential;
  if (category === "utility") return config.enableUtility;
  if (category === "defense") return config.enableDefense;
  if (category === "fantasy") return config.enableFantasy;
  if (category === "terrain_specific") return config.enableTerrainSpecific;
  if (category === "ruin") return config.enableRuin;
  return config.enableCivic;
}

function describeSpecialRules(definition: StructurePresetDefinition): string[] {
  const rules = definition.generationRules;
  const descriptions: string[] = [];
  if (rules.requiresFlatArea) descriptions.push("requiresFlatArea");
  if (rules.allowTerrainFlattening) descriptions.push(`allowTerrainFlattening:${rules.allowTerrainFlattening}`);
  if (rules.requiresWaterNearby) descriptions.push("requiresWaterNearby");
  if (rules.requiresTreesNearby) descriptions.push("requiresTreesNearby");
  if (rules.requiresSolidGround) descriptions.push("requiresSolidGround");
  if (rules.maxSlope) descriptions.push(`maxSlope:${rules.maxSlope}`);
  if (rules.ruinDecayLevel) descriptions.push(`ruinDecayLevel:${rules.ruinDecayLevel}`);
  if (definition.size === "modular") descriptions.push("modular");
  return descriptions;
}

function validFootprint(footprint: StructureFootprint): boolean {
  return [footprint.width, footprint.depth, footprint.height].every((value) => Number.isInteger(value) && value > 0);
}

function validModelSize(size: VoxelSize): boolean {
  return [size.width, size.depth, size.height].every((value) => Number.isInteger(value) && value > 0);
}
