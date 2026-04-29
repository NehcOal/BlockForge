import { materialStyles } from "@/lib/voxel/materialStyles";
import type { BlockType } from "@/types/blueprint";

export type BlockRenderStyle = {
  color: string;
  emissive?: string;
  emissiveIntensity?: number;
  opacity?: number;
  roughness?: number;
  metalness?: number;
  transparent?: boolean;
};

export const blockStyles: Record<BlockType, BlockRenderStyle> = materialStyles;
