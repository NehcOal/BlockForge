import { describe, expect, it } from "vitest";
import { generateHousePlan, housePlanToVoxelModel } from "@/lib/house";
import { validateBlueprintJson, validateVoxelModel, voxelModelToBlueprintV2 } from "@/lib/voxel";

describe("house blueprint compiler", () => {
  it("compiles house plans to valid voxel models and Blueprint v2", () => {
    const plan = generateHousePlan({ style: "medieval_house" });
    const model = housePlanToVoxelModel(plan);
    const blueprint = voxelModelToBlueprintV2(model);
    const validation = validateBlueprintJson(blueprint);

    expect(validateVoxelModel(model)).toBe(true);
    expect(model.size.width).toBe(plan.footprint.width);
    expect(model.size.height).toBe(plan.dimensions.totalHeight);
    expect(model.blocks.length).toBeGreaterThan(0);
    expect(validation.valid).toBe(true);
  });

  it("keeps compiled blocks inside house bounds", () => {
    const model = housePlanToVoxelModel(generateHousePlan({ style: "watchtower_house" }));

    for (const block of model.blocks) {
      expect(block.x).toBeGreaterThanOrEqual(0);
      expect(block.x).toBeLessThan(model.size.width);
      expect(block.y).toBeGreaterThanOrEqual(0);
      expect(block.y).toBeLessThan(model.size.height);
      expect(block.z).toBeGreaterThanOrEqual(0);
      expect(block.z).toBeLessThan(model.size.depth);
    }
  });
});
