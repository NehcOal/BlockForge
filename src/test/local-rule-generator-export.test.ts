import { describe, expect, it } from "vitest";
import { generateVoxelModelFromPrompt, validateBlueprintJson, voxelModelToBlueprintV2 } from "@/lib/voxel";

describe("Local Rule Generator export", () => {
  it("produces valid Blueprint v2 output", () => {
    const result = generateVoxelModelFromPrompt("build a stone tower", "tower", "medium");
    const blueprint = voxelModelToBlueprintV2(result.model);
    expect(validateBlueprintJson(blueprint).valid).toBe(true);
  });
});
