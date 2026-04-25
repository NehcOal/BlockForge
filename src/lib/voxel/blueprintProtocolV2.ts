import { createSafeResourcePath } from "@/lib/voxel/exportUtils";
import { minecraftBlockIds } from "@/lib/voxel/minecraftBlocks";
import { getUsedBlockTypes } from "@/lib/voxel/utils";
import type { VoxelModel } from "@/types/blueprint";

export type BlockForgeBlockStateV2 = {
  name: string;
  properties?: Record<string, string>;
};

export type BlockForgeBlueprintBlockV2 = {
  x: number;
  y: number;
  z: number;
  state: string;
};

export type BlockForgeBlueprintV2 = {
  schemaVersion: 2;
  id: string;
  name: string;
  description: string;
  minecraftVersion: "1.21.1";
  generator: "BlockForge";
  size: {
    width: number;
    height: number;
    depth: number;
  };
  origin: {
    x: number;
    y: number;
    z: number;
  };
  palette: Record<string, BlockForgeBlockStateV2>;
  blocks: BlockForgeBlueprintBlockV2[];
};

export function voxelModelToBlueprintV2(
  model: VoxelModel
): BlockForgeBlueprintV2 {
  const usedBlockTypes = getUsedBlockTypes(model);
  const palette = Object.fromEntries(
    usedBlockTypes.map((blockType) => [
      blockType,
      {
        name: minecraftBlockIds[blockType]
      }
    ])
  );

  return {
    schemaVersion: 2,
    id: createSafeResourcePath(model.id || model.name),
    name: model.name,
    description: model.description,
    minecraftVersion: "1.21.1",
    generator: "BlockForge",
    size: model.size,
    origin: {
      x: 0,
      y: 0,
      z: 0
    },
    palette,
    blocks: model.blocks.map((block) => ({
      x: block.x,
      y: block.y,
      z: block.z,
      state: block.block
    }))
  };
}
