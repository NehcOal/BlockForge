import { createSafeResourcePath } from "@/lib/voxel/exportUtils";
import { minecraftBlockIds } from "@/lib/voxel/minecraftBlocks";
import { getUsedBlockTypes } from "@/lib/voxel/utils";
import type { VoxelModel } from "@/types/blueprint";

export type BlockForgeBlueprintV1 = {
  schemaVersion: 1;
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
  palette: Record<string, string>;
  blocks: Array<{
    x: number;
    y: number;
    z: number;
    block: string;
  }>;
};

export function voxelModelToBlueprintV1(
  model: VoxelModel
): BlockForgeBlueprintV1 {
  const usedBlockTypes = getUsedBlockTypes(model);
  const palette = Object.fromEntries(
    usedBlockTypes.map((blockType) => [blockType, minecraftBlockIds[blockType]])
  );

  return {
    schemaVersion: 1,
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
      block: block.block
    }))
  };
}
