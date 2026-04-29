import { describe, expect, it } from "vitest";
import { structurePlanToVoxel } from "@/lib/ai";
import { validateVoxelModel } from "@/lib/voxel";
import { createValidStructurePlan } from "./ai-fixtures";

describe("structurePlanToVoxel", () => {
  it("converts a structure plan into a valid VoxelModel", () => {
    const result = structurePlanToVoxel(createValidStructurePlan());
    expect(validateVoxelModel(result.model)).toBe(true);
    expect(result.model.blocks.length).toBeGreaterThan(0);
  });

  it("skips unknown block keys with a warning", () => {
    const plan = createValidStructurePlan();
    plan.elements.push({
      id: "bad",
      type: "custom",
      blockKey: "missing",
      from: [0, 0, 0],
      to: [0, 0, 0]
    });
    const result = structurePlanToVoxel(plan);
    expect(result.warnings.some((warning) => warning.includes("missing palette key"))).toBe(true);
  });
});
