import { describe, expect, it } from "vitest";
import {
  blockStyles,
  blockTypes,
  createBlock,
  getModelCenterOffset,
  getUsedBlockTypes,
  toRenderPosition
} from "@/lib/voxel";
import type { VoxelModel } from "@/types/blueprint";

const sampleModel: VoxelModel = {
  id: "small-cottage",
  name: "Render Test Model",
  description: "A small model used for render helper tests.",
  size: {
    width: 10,
    height: 6,
    depth: 4
  },
  blocks: [
    createBlock(0, 0, 0, "stone_bricks"),
    createBlock(5, 2, 1, "glass"),
    createBlock(9, 5, 3, "torch"),
    createBlock(2, 1, 2, "glass")
  ]
};

describe("voxel rendering helpers", () => {
  it("defines render styles for every block type", () => {
    expect(Object.keys(blockStyles).sort()).toEqual([...blockTypes].sort());
  });

  it("marks glass and water as transparent materials", () => {
    expect(blockStyles.glass.transparent).toBe(true);
    expect(blockStyles.water.transparent).toBe(true);
    expect(blockStyles.glass.opacity).toBeLessThan(1);
    expect(blockStyles.water.opacity).toBeLessThan(1);
  });

  it("computes the center offset from model dimensions", () => {
    expect(getModelCenterOffset(sampleModel)).toEqual({
      x: 4.5,
      y: 0,
      z: 1.5
    });
  });

  it("converts voxel coordinates into centered render positions", () => {
    expect(toRenderPosition(createBlock(0, 0, 0, "stone_bricks"), sampleModel)).toEqual([
      -4.5,
      0.5,
      -1.5
    ]);
    expect(toRenderPosition(createBlock(9, 5, 3, "torch"), sampleModel)).toEqual([
      4.5,
      5.5,
      1.5
    ]);
  });

  it("returns sorted block types used by a model", () => {
    expect(getUsedBlockTypes(sampleModel)).toEqual([
      "glass",
      "stone_bricks",
      "torch"
    ]);
  });
});
