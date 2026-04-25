import type { VoxelBlock, VoxelModel } from "@/types/blueprint";

export type RenderPosition = [number, number, number];

export type ModelCenterOffset = {
  x: number;
  y: number;
  z: number;
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
