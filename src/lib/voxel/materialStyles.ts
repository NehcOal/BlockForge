import type { BlockType } from "@/types/blueprint";

export type VoxelMaterialStyle = {
  color: string;
  emissive?: string;
  emissiveIntensity?: number;
  metalness: number;
  opacity: number;
  roughness: number;
  transparent: boolean;
};

export const materialStyles: Record<BlockType, VoxelMaterialStyle> = {
  stone_bricks: material("#8b8f91", 0.88),
  cobblestone: material("#62676a", 0.94),
  oak_planks: material("#b9864b", 0.76),
  oak_log: material("#6d4322", 0.82),
  glass: material("#9ad7ff", 0.18, { opacity: 0.38, transparent: true }),
  torch: material("#ffb02e", 0.38, { emissive: "#ff8a1f", emissiveIntensity: 0.45 }),
  door: material("#74451f", 0.84),
  stone: material("#7b7f81", 0.9),
  grass: material("#4d923f", 0.78),
  water: material("#2f82d0", 0.24, { opacity: 0.5, transparent: true }),
  gold_block: material("#f2c84b", 0.34, { emissive: "#533800", emissiveIntensity: 0.08, metalness: 0.26 }),
  wool_red: material("#c53c38", 0.9),
  wool_blue: material("#315fba", 0.9),
  wool_white: material("#eeeae0", 0.86),
  spruce_planks: material("#7a5131", 0.78),
  spruce_log: material("#4d3422", 0.84),
  dark_oak_planks: material("#4a2f1f", 0.8),
  dark_oak_log: material("#33231b", 0.86),
  mangrove_planks: material("#8b4a3a", 0.78),
  mangrove_log: material("#5b2d2e", 0.84),
  mossy_cobblestone: material("#4f6650", 0.94),
  sandstone: material("#d8c487", 0.82),
  smooth_sandstone: material("#e3d19a", 0.74),
  terracotta: material("#b76545", 0.86),
  snow: material("#f4f8fb", 0.55),
  mud: material("#4a3a31", 0.92),
  deepslate: material("#3a3d42", 0.92),
  iron_block: material("#c5cbd0", 0.38, { metalness: 0.22 }),
  hay_block: material("#cfa43a", 0.86),
  leaves: material("#3f7b3b", 0.8, { opacity: 0.82, transparent: true }),
  spruce_stairs: material("#6b452b", 0.78),
  dark_oak_stairs: material("#3f281b", 0.8),
  oak_stairs: material("#ad7a43", 0.76),
  stripped_oak_log: material("#c18b52", 0.76),
  lantern: material("#e8b84f", 0.42, { emissive: "#e58a27", emissiveIntensity: 0.32, metalness: 0.12 }),
  trapdoor: material("#76512e", 0.82),
  glass_pane: material("#b3e4ff", 0.16, { opacity: 0.34, transparent: true }),
  fence: material("#8b5f33", 0.8),
  barrel: material("#7f5732", 0.8),
  chest: material("#9a6936", 0.78),
  anvil: material("#4f5558", 0.46, { metalness: 0.18 }),
  blast_furnace: material("#55585b", 0.66, { emissive: "#5a2b18", emissiveIntensity: 0.08 }),
  iron_bars: material("#8f969a", 0.52, { metalness: 0.18 }),
  chain: material("#70777c", 0.5, { metalness: 0.2 }),
  ladder: material("#9a6a3a", 0.8),
  cracked_stone_bricks: material("#767b7e", 0.92),
  mossy_stone_bricks: material("#65745f", 0.94),
  polished_andesite: material("#929699", 0.72),
  dark_oak_fence: material("#3f281b", 0.82),
  cut_sandstone: material("#d6c07d", 0.8),
  mud_bricks: material("#705744", 0.88),
  moss_block: material("#4f8a43", 0.84),
  amethyst_block: material("#8f5cc2", 0.42, { emissive: "#3b1f59", emissiveIntensity: 0.1 }),
  purple_stained_glass: material("#9c6ed1", 0.18, { opacity: 0.42, transparent: true }),
  bookshelf: material("#8a5a32", 0.78),
  white_concrete: material("#deded8", 0.72),
  stripped_dark_oak_log: material("#5c3a27", 0.8),
  bamboo: material("#8aa339", 0.78),
  smooth_stone: material("#b2b5b7", 0.68),
  vine: material("#2f6b35", 0.82, { opacity: 0.72, transparent: true }),
  cobweb: material("#e8ecef", 0.55, { opacity: 0.45, transparent: true })
};

function material(color: string, roughness: number, overrides: Partial<VoxelMaterialStyle> = {}): VoxelMaterialStyle {
  return {
    color,
    metalness: overrides.metalness ?? 0,
    opacity: overrides.opacity ?? 1,
    roughness,
    transparent: overrides.transparent ?? false,
    emissive: overrides.emissive,
    emissiveIntensity: overrides.emissiveIntensity
  };
}
