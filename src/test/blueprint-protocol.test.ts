import { describe, expect, it } from "vitest";
import {
  getAllPresets,
  getUsedBlockTypes,
  voxelModelToBlueprintV1
} from "@/lib/voxel";

describe("BlockForge Blueprint v1 protocol", () => {
  const [model] = getAllPresets();
  const blueprint = voxelModelToBlueprintV1(model);

  it("uses fixed protocol metadata", () => {
    expect(blueprint.schemaVersion).toBe(1);
    expect(blueprint.minecraftVersion).toBe("1.21.1");
    expect(blueprint.generator).toBe("BlockForge");
  });

  it("keeps model size and voxel block count", () => {
    expect(blueprint.size).toEqual(model.size);
    expect(blueprint.blocks).toHaveLength(model.blocks.length);
  });

  it("keeps raw voxel coordinates and block names", () => {
    expect(blueprint.origin).toEqual({ x: 0, y: 0, z: 0 });
    expect(blueprint.blocks[0]).toEqual({
      x: model.blocks[0].x,
      y: model.blocks[0].y,
      z: model.blocks[0].z,
      block: model.blocks[0].block
    });
  });

  it("includes every used block type in the palette", () => {
    const usedBlockTypes = getUsedBlockTypes(model);

    expect(Object.keys(blueprint.palette).sort()).toEqual(usedBlockTypes);

    for (const blockType of usedBlockTypes) {
      expect(blueprint.palette[blockType]).toMatch(/^minecraft:/);
    }
  });
});
