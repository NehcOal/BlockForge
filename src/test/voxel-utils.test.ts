import { describe, expect, it } from "vitest";
import {
  createBlock,
  getAllPresets,
  getPresetById,
  validateVoxelModel
} from "@/lib/voxel";
import type { VoxelModel } from "@/types/blueprint";

describe("voxel utils", () => {
  it("creates typed voxel blocks", () => {
    expect(createBlock(1, 2, 3, "stone_bricks")).toEqual({
      x: 1,
      y: 2,
      z: 3,
      block: "stone_bricks"
    });
  });

  it("gets presets by id", () => {
    expect(getPresetById("small-cottage").name).toBe("Small Cottage");
  });

  it("rejects an out-of-bounds block", () => {
    const invalidModel: VoxelModel = {
      id: "small-cottage",
      name: "Invalid Cottage",
      description: "A model with an invalid block coordinate.",
      size: {
        width: 2,
        height: 2,
        depth: 2
      },
      blocks: [createBlock(2, 0, 0, "oak_planks")]
    };

    expect(validateVoxelModel(invalidModel)).toBe(false);
  });

  it("rejects duplicate coordinates", () => {
    const invalidModel: VoxelModel = {
      id: "stone-bridge",
      name: "Duplicate Bridge",
      description: "A model with duplicate block coordinates.",
      size: {
        width: 4,
        height: 4,
        depth: 4
      },
      blocks: [
        createBlock(1, 1, 1, "stone_bricks"),
        createBlock(1, 1, 1, "cobblestone")
      ]
    };

    expect(validateVoxelModel(invalidModel)).toBe(false);
  });

  it("exports models with the required JSON shape", () => {
    const [model] = getAllPresets();
    const exported = JSON.parse(JSON.stringify(model)) as VoxelModel;

    expect(exported).toHaveProperty("id");
    expect(exported).toHaveProperty("name");
    expect(exported).toHaveProperty("description");
    expect(exported).toHaveProperty("size");
    expect(exported).toHaveProperty("blocks");
    expect(exported.blocks.length).toBeGreaterThan(0);
  });
});
