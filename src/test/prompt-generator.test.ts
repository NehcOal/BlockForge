import { describe, expect, it } from "vitest";
import { generateVoxelModelFromPrompt, validateVoxelModel, voxelModelToBlueprintV2 } from "@/lib/voxel";

describe("prompt generator", () => {
  it("generates a valid local tower model by default", () => {
    const result = generateVoxelModelFromPrompt("a tall stone tower with glass windows");

    expect(result.kind).toBe("tower");
    expect(result.scale).toBe("medium");
    expect(validateVoxelModel(result.model)).toBe(true);
    expect(result.model.blocks.length).toBeGreaterThan(100);
  });

  it("detects structure kind and scale from prompt keywords", () => {
    const result = generateVoxelModelFromPrompt("large wooden bridge over water");

    expect(result.kind).toBe("bridge");
    expect(result.scale).toBe("large");
    expect(result.materials).toContain("oak_planks");
    expect(validateVoxelModel(result.model)).toBe(true);
  });

  it("keeps generated models deterministic for the same prompt", () => {
    const first = generateVoxelModelFromPrompt("small red pixel statue");
    const second = generateVoxelModelFromPrompt("small red pixel statue");

    expect(first.model).toEqual(second.model);
  });

  it("exports generated models to Blueprint JSON v2", () => {
    const result = generateVoxelModelFromPrompt("large stone dungeon entrance");
    const blueprint = voxelModelToBlueprintV2(result.model);

    expect(blueprint.schemaVersion).toBe(2);
    expect(blueprint.blocks).toHaveLength(result.model.blocks.length);
    expect(Object.keys(blueprint.palette).length).toBeGreaterThan(0);
  });
});
