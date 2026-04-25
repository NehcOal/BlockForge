import { presetFactories } from "@/lib/voxel/presets";
import type {
  BlockType,
  PresetId,
  VoxelBlock,
  VoxelModel
} from "@/types/blueprint";

export const blockTypes = [
  "stone_bricks",
  "cobblestone",
  "oak_planks",
  "oak_log",
  "glass",
  "torch",
  "door",
  "stone",
  "grass",
  "water",
  "gold_block",
  "wool_red",
  "wool_blue",
  "wool_white"
] as const satisfies readonly BlockType[];

const blockTypeSet = new Set<BlockType>(blockTypes);

export function createBlock(
  x: number,
  y: number,
  z: number,
  block: BlockType
): VoxelBlock {
  return { x, y, z, block };
}

export function getBlockKey({ x, y, z }: Pick<VoxelBlock, "x" | "y" | "z">) {
  return `${x}:${y}:${z}`;
}

export function setBlock(
  store: Map<string, VoxelBlock>,
  x: number,
  y: number,
  z: number,
  block: BlockType
) {
  const voxelBlock = createBlock(x, y, z, block);
  store.set(getBlockKey(voxelBlock), voxelBlock);
}

export function blocksFromStore(store: Map<string, VoxelBlock>) {
  return Array.from(store.values()).sort((a, b) => {
    if (a.y !== b.y) {
      return a.y - b.y;
    }

    if (a.z !== b.z) {
      return a.z - b.z;
    }

    return a.x - b.x;
  });
}

export function validateVoxelModel(model: VoxelModel) {
  if (!model.id || !model.name || !model.description) {
    return false;
  }

  const { width, height, depth } = model.size;
  const validSize = [width, height, depth].every(
    (value) => Number.isInteger(value) && value > 0
  );

  if (!validSize || !Array.isArray(model.blocks)) {
    return false;
  }

  const seen = new Set<string>();

  for (const block of model.blocks) {
    const hasFields =
      Number.isInteger(block.x) &&
      Number.isInteger(block.y) &&
      Number.isInteger(block.z) &&
      blockTypeSet.has(block.block);

    const inBounds =
      block.x >= 0 &&
      block.x < width &&
      block.y >= 0 &&
      block.y < height &&
      block.z >= 0 &&
      block.z < depth;

    const key = getBlockKey(block);

    if (!hasFields || !inBounds || seen.has(key)) {
      return false;
    }

    seen.add(key);
  }

  return true;
}

export function getAllPresets() {
  return Object.values(presetFactories).map((createPreset) => createPreset());
}

export function getPresetById(id: PresetId) {
  return presetFactories[id]();
}

export function getUsedBlockTypes(model: VoxelModel) {
  return Array.from(new Set(model.blocks.map((block) => block.block))).sort();
}
