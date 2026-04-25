import { describe, expect, it } from "vitest";
import {
  getAllPresets,
  getUsedBlockTypes,
  voxelModelToBlueprintV2
} from "@/lib/voxel";

describe("BlockForge Blueprint v2 protocol", () => {
  const [model] = getAllPresets();
  const blueprint = voxelModelToBlueprintV2(model);

  it("uses fixed v2 protocol metadata", () => {
    expect(blueprint.schemaVersion).toBe(2);
    expect(blueprint.minecraftVersion).toBe("1.21.1");
    expect(blueprint.generator).toBe("BlockForge");
  });

  it("uses block state palette entries", () => {
    for (const blockType of getUsedBlockTypes(model)) {
      expect(blueprint.palette[blockType].name).toMatch(/^minecraft:/);
    }
  });

  it("uses state keys in blocks", () => {
    expect(blueprint.blocks).toHaveLength(model.blocks.length);
    expect(blueprint.blocks[0]).toEqual({
      x: model.blocks[0].x,
      y: model.blocks[0].y,
      z: model.blocks[0].z,
      state: model.blocks[0].block
    });
  });
});
