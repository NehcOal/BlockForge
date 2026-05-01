import { describe, expect, it } from "vitest";
import { estimateDrawGroups, groupBlocksByType, resolveRenderMode } from "@/lib/voxel";
import type { VoxelModel } from "@/types/blueprint";

describe("voxel rendering groups", () => {
  const model: VoxelModel = {
    id: "render-test",
    name: "Render Test",
    description: "",
    size: { width: 20, height: 20, depth: 20 },
    blocks: [
      { x: 0, y: 0, z: 0, block: "stone" },
      { x: 1, y: 0, z: 0, block: "stone" },
      { x: 2, y: 0, z: 0, block: "glass" }
    ]
  };

  it("groups blocks by block type", () => {
    const groups = groupBlocksByType(model.blocks);
    expect(groups).toHaveLength(2);
    expect(groups.find((group) => group.blockType === "stone")?.blocks).toHaveLength(2);
  });

  it("selects instanced rendering for large auto models", () => {
    expect(resolveRenderMode(299, "auto")).toBe("mesh");
    expect(resolveRenderMode(300, "auto")).toBe("instanced");
    expect(resolveRenderMode(10, "instanced")).toBe("instanced");
  });

  it("estimates draw groups", () => {
    expect(estimateDrawGroups(model, "mesh")).toBe(3);
    expect(estimateDrawGroups(model, "instanced")).toBe(2);
  });
});
