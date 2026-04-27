import { describe, expect, it } from "vitest";
import { voxelModelToBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";
import { validateBlueprintJson } from "@/lib/voxel/blueprintValidation";
import { getPresetById } from "@/lib/voxel";

describe("blueprint validation", () => {
  it("accepts exported Blueprint JSON v2", () => {
    const report = validateBlueprintJson(voxelModelToBlueprintV2(getPresetById("small-cottage")));

    expect(report.valid).toBe(true);
    expect(report.issues).toEqual([]);
  });

  it("reports field-level errors for unsafe block references", () => {
    const blueprint = voxelModelToBlueprintV2(getPresetById("small-cottage"));
    blueprint.blocks = [
      { x: 0, y: 0, z: 0, state: "stone" },
      { x: 0, y: 0, z: 0, state: "missing" },
      { x: blueprint.size.width, y: 0, z: 0, state: "stone" }
    ];

    const report = validateBlueprintJson(blueprint);

    expect(report.valid).toBe(false);
    expect(report.issues).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          field: "blocks[1]",
          severity: "warning",
          message: "duplicate block coordinate 0,0,0; later block wins"
        }),
        expect.objectContaining({
          field: "blocks[1].state",
          message: "blocks[1] references missing palette key \"missing\""
        }),
        expect.objectContaining({
          field: "blocks[2]",
          message: `block at x=${blueprint.size.width} is outside width=${blueprint.size.width}`
        })
      ])
    );
  });

  it("warns when origin is missing", () => {
    const blueprint = voxelModelToBlueprintV2(getPresetById("small-cottage"));
    const withoutOrigin: Record<string, unknown> = { ...blueprint };
    delete withoutOrigin.origin;

    const report = validateBlueprintJson(withoutOrigin);

    expect(report.valid).toBe(true);
    expect(report.issues).toEqual([
      {
        severity: "warning",
        section: "Origin",
        field: "origin",
        message: "Blueprint origin is missing; connectors assume 0,0,0.",
        suggestion: undefined
      }
    ]);
  });
});
