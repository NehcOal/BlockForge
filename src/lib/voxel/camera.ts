import type { VoxelModel } from "@/types/blueprint";
import type { RenderPosition } from "@/lib/voxel/rendering";

export type ModelBoundingBox = {
  min: RenderPosition;
  max: RenderPosition;
  size: RenderPosition;
};

export function getModelBoundingBox(model: VoxelModel): ModelBoundingBox {
  if (model.blocks.length === 0) {
    return {
      min: [0, 0, 0],
      max: [model.size.width, model.size.height, model.size.depth],
      size: [model.size.width, model.size.height, model.size.depth]
    };
  }
  const xs = model.blocks.map((block) => block.x);
  const ys = model.blocks.map((block) => block.y);
  const zs = model.blocks.map((block) => block.z);
  const min: RenderPosition = [Math.min(...xs), Math.min(...ys), Math.min(...zs)];
  const max: RenderPosition = [Math.max(...xs) + 1, Math.max(...ys) + 1, Math.max(...zs) + 1];
  return { min, max, size: [max[0] - min[0], max[1] - min[1], max[2] - min[2]] };
}

export function getModelCenter(model: VoxelModel): RenderPosition {
  const box = getModelBoundingBox(model);
  return [
    box.min[0] + box.size[0] / 2 - model.size.width / 2,
    box.min[1] + box.size[1] / 2,
    box.min[2] + box.size[2] / 2 - model.size.depth / 2
  ];
}

export function getPreviewScale(model: VoxelModel): number {
  const largest = Math.max(...getModelBoundingBox(model).size);
  if (largest <= 8) return 1.25;
  if (largest >= 48) return 0.72;
  return 1;
}

export function getRecommendedCameraPosition(model: VoxelModel): RenderPosition {
  const largest = Math.max(...getModelBoundingBox(model).size);
  const distance = Math.max(14, largest * 1.9);
  return [distance, Math.max(10, distance * 0.82), distance];
}
