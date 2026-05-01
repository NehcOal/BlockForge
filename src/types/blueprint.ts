export type PresetId =
  | "medieval-tower"
  | "small-cottage"
  | "dungeon-entrance"
  | "stone-bridge"
  | "pixel-statue"
  | "cottage"
  | "farmhouse"
  | "blacksmith"
  | "inn"
  | "barn"
  | "stable"
  | "manor"
  | "warehouse"
  | "watchtower"
  | "wizard_tower"
  | "treehouse"
  | "mountain_lodge"
  | "swamp_hut"
  | "desert_house"
  | "snow_cabin"
  | "dock_house"
  | "japanese_house"
  | "pagoda"
  | "ruined_house"
  | "ruined_tower"
  | "ruined_fort"
  | "market_stall"
  | "chapel"
  | "small_fort"
  | "castle_keep"
  | "castle_gatehouse"
  | "castle_wall_segment"
  | "castle_corner_tower"
  | "castle_courtyard";

export type BlockType =
  | "stone_bricks"
  | "cobblestone"
  | "oak_planks"
  | "oak_log"
  | "glass"
  | "torch"
  | "door"
  | "stone"
  | "grass"
  | "water"
  | "gold_block"
  | "wool_red"
  | "wool_blue"
  | "wool_white"
  | "spruce_planks"
  | "spruce_log"
  | "dark_oak_planks"
  | "dark_oak_log"
  | "mangrove_planks"
  | "mangrove_log"
  | "mossy_cobblestone"
  | "sandstone"
  | "smooth_sandstone"
  | "terracotta"
  | "snow"
  | "mud"
  | "deepslate"
  | "iron_block"
  | "hay_block"
  | "leaves"
  | "spruce_stairs"
  | "dark_oak_stairs"
  | "oak_stairs"
  | "stripped_oak_log"
  | "lantern"
  | "trapdoor"
  | "glass_pane"
  | "fence"
  | "barrel"
  | "chest"
  | "anvil"
  | "blast_furnace"
  | "iron_bars"
  | "chain"
  | "ladder"
  | "cracked_stone_bricks"
  | "mossy_stone_bricks"
  | "polished_andesite"
  | "dark_oak_fence"
  | "cut_sandstone"
  | "mud_bricks"
  | "moss_block"
  | "amethyst_block"
  | "purple_stained_glass"
  | "bookshelf"
  | "white_concrete"
  | "stripped_dark_oak_log"
  | "bamboo"
  | "smooth_stone"
  | "vine"
  | "cobweb";

export type VoxelBlock = {
  x: number;
  y: number;
  z: number;
  block: BlockType;
};

export type VoxelSize = {
  width: number;
  height: number;
  depth: number;
};

export type VoxelModel = {
  id: string;
  name: string;
  description: string;
  size: VoxelSize;
  blocks: VoxelBlock[];
};

export type PresetOption = {
  id: PresetId;
  label: string;
  description: string;
};
