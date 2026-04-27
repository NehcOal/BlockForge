export type PresetId =
  | "medieval-tower"
  | "small-cottage"
  | "dungeon-entrance"
  | "stone-bridge"
  | "pixel-statue";

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
  | "wool_white";

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
