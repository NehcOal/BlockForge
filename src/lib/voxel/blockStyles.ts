import type { BlockType } from "@/types/blueprint";

export type BlockRenderStyle = {
  color: string;
  opacity?: number;
  roughness?: number;
  metalness?: number;
  transparent?: boolean;
};

export const blockStyles: Record<BlockType, BlockRenderStyle> = {
  stone_bricks: {
    color: "#8a8f91",
    roughness: 0.82
  },
  cobblestone: {
    color: "#5f6264",
    roughness: 0.9
  },
  oak_planks: {
    color: "#b8844a",
    roughness: 0.72
  },
  oak_log: {
    color: "#6d4322",
    roughness: 0.78
  },
  glass: {
    color: "#9ad7ff",
    opacity: 0.42,
    roughness: 0.2,
    transparent: true
  },
  torch: {
    color: "#ffb02e",
    roughness: 0.35
  },
  door: {
    color: "#7a4a22",
    roughness: 0.8
  },
  stone: {
    color: "#777b7d",
    roughness: 0.86
  },
  grass: {
    color: "#4f8f3b",
    roughness: 0.75
  },
  water: {
    color: "#2f82d0",
    opacity: 0.52,
    roughness: 0.28,
    transparent: true
  },
  gold_block: {
    color: "#f2c84b",
    metalness: 0.22,
    roughness: 0.34
  },
  wool_red: {
    color: "#c53c38",
    roughness: 0.88
  },
  wool_blue: {
    color: "#315fba",
    roughness: 0.88
  },
  wool_white: {
    color: "#f1eee4",
    roughness: 0.84
  }
};
