import type { BlockType, VoxelBlock, VoxelModel } from "@/types/blueprint";

export type RenderPosition = [number, number, number];

export type ModelCenterOffset = {
  x: number;
  y: number;
  z: number;
};

export type RenderMode = "auto" | "mesh" | "instanced";
export type ResolvedRenderMode = "mesh" | "instanced";
export const INSTANCED_RENDER_THRESHOLD = 300;

export type VoxelRenderGroup = {
  blockType: BlockType;
  blocks: VoxelBlock[];
};

export function getModelCenterOffset(model: VoxelModel): ModelCenterOffset {
  return {
    x: model.size.width / 2 - 0.5,
    y: 0,
    z: model.size.depth / 2 - 0.5
  };
}

export function toRenderPosition(
  block: Pick<VoxelBlock, "x" | "y" | "z">,
  model: VoxelModel
): RenderPosition {
  const offset = getModelCenterOffset(model);

  return [block.x - offset.x, block.y + 0.5 - offset.y, block.z - offset.z];
}

export function getCameraPosition(model: VoxelModel): RenderPosition {
  const largestDimension = Math.max(
    model.size.width,
    model.size.height,
    model.size.depth
  );
  const distance = Math.max(18, largestDimension * 1.5);

  return [distance, distance * 0.9, distance];
}

export function resolveRenderMode(blockCount: number, renderMode: RenderMode): ResolvedRenderMode {
  if (renderMode === "mesh") return "mesh";
  if (renderMode === "instanced") return "instanced";
  return blockCount >= INSTANCED_RENDER_THRESHOLD ? "instanced" : "mesh";
}

export function groupBlocksByType(blocks: VoxelBlock[]): VoxelRenderGroup[] {
  const groups = new Map<BlockType, VoxelBlock[]>();
  for (const block of blocks) {
    groups.set(block.block, [...(groups.get(block.block) ?? []), block]);
  }
  return [...groups.entries()].map(([blockType, groupedBlocks]) => ({ blockType, blocks: groupedBlocks }));
}

export function estimateDrawGroups(model: VoxelModel, renderMode: RenderMode): number {
  const resolved = resolveRenderMode(model.blocks.length, renderMode);
  return resolved === "mesh" ? model.blocks.length : groupBlocksByType(model.blocks).length;
}
