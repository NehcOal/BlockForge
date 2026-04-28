import { describe, expect, it } from "vitest";
import { getModelBoundingBox, getModelCenter, getPreviewScale, getRecommendedCameraPosition } from "@/lib/voxel";
import type { VoxelModel } from "@/types/blueprint";

describe("voxel camera helpers", () => {
  const model: VoxelModel = {
    id: "camera-test",
    name: "Camera Test",
    description: "",
    size: { width: 10, height: 8, depth: 6 },
    blocks: [
      { x: 1, y: 0, z: 2, block: "stone" },
      { x: 4, y: 5, z: 3, block: "glass" }
    ]
  };

  it("computes bounding boxes and center", () => {
    expect(getModelBoundingBox(model)).toEqual({
      min: [1, 0, 2],
      max: [5, 6, 4],
      size: [4, 6, 2]
    });
    expect(getModelCenter(model)).toEqual([-2, 3, 0]);
  });

  it("returns reasonable scale and camera positions", () => {
    expect(getPreviewScale(model)).toBe(1.25);
    const camera = getRecommendedCameraPosition(model);
    expect(camera[0]).toBeGreaterThanOrEqual(14);
    expect(camera[1]).toBeGreaterThan(0);
    expect(camera[2]).toBe(camera[0]);
  });
});
