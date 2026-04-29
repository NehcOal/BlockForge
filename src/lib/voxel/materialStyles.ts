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
  wool_white: material("#eeeae0", 0.86)
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
