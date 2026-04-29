import { describe, expect, it } from "vitest";
import { litematicToBlueprintV2, validateBlueprintJson } from "@/lib/voxel";
import { createMinimalLitematic } from "@/test/litematic-fixtures";

describe("litematic to blueprint", () => {
  it("converts a parsed litematic into a valid Blueprint v2", () => {
    const result = litematicToBlueprintV2(createMinimalLitematic());
    expect(result.blueprint.schemaVersion).toBe(2);
    expect(result.blueprint.blocks).toHaveLength(2);
    expect(validateBlueprintJson(result.blueprint).valid).toBe(true);
  });
});
