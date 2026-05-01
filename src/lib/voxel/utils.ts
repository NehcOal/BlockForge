import { presetFactories } from "@/lib/voxel/presets";
import type {
  BlockType,
  PresetId,
  VoxelBlock,
  VoxelModel
} from "@/types/blueprint";

export type PresetVoxelModel = VoxelModel & { id: PresetId };

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
  "wool_white",
  "spruce_planks",
  "spruce_log",
  "dark_oak_planks",
  "dark_oak_log",
  "mangrove_planks",
  "mangrove_log",
  "mossy_cobblestone",
  "sandstone",
  "smooth_sandstone",
  "terracotta",
  "snow",
  "mud",
  "deepslate",
  "iron_block",
  "hay_block",
  "leaves",
  "spruce_stairs",
  "dark_oak_stairs",
  "oak_stairs",
  "stripped_oak_log",
  "lantern",
  "trapdoor",
  "glass_pane",
  "fence",
  "barrel",
  "chest",
  "anvil",
  "blast_furnace",
  "iron_bars",
  "chain",
  "ladder",
  "cracked_stone_bricks",
  "mossy_stone_bricks",
  "polished_andesite",
  "dark_oak_fence",
  "cut_sandstone",
  "mud_bricks",
  "moss_block",
  "amethyst_block",
  "purple_stained_glass",
  "bookshelf",
  "white_concrete",
  "stripped_dark_oak_log",
  "bamboo",
  "smooth_stone",
  "vine",
  "cobweb"
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

export function getAllPresets(): PresetVoxelModel[] {
  return Object.values(presetFactories).map((createPreset) => createPreset());
}

export function getPresetById(id: PresetId): PresetVoxelModel {
  return presetFactories[id]();
}

export function getUsedBlockTypes(model: VoxelModel) {
  return Array.from(new Set(model.blocks.map((block) => block.block))).sort();
}
