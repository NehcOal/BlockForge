import { describe, expect, it } from "vitest";
import { countImportSummaryIssues } from "@/components/ExportPanel";
import type { ImportedBlueprintAsset } from "@/lib/voxel";

describe("ExportPanel import summary", () => {
  it("counts parser/import warnings and validation warnings together", () => {
    const asset: ImportedBlueprintAsset = {
      sourceType: "schematic",
      id: "schem/warnings",
      name: "Warnings",
      summary: {
        blueprintCount: 1,
        warningCount: 2,
        validationSummary: "0 error(s), 1 warning(s)"
      },
      blueprints: [
        {
          id: "schem/warnings",
          name: "Warnings",
          size: { width: 1, height: 1, depth: 1 },
          paletteCount: 1,
          blockCount: 1,
          validation: {
            valid: true,
            issues: [
              {
                severity: "warning",
                field: "origin",
                message: "Blueprint origin is missing; connectors assume 0,0,0."
              }
            ]
          }
        }
      ],
      warnings: [
        "Entities are ignored in Web schematic import Alpha.",
        "Biomes are ignored in Web schematic import Alpha."
      ]
    };

    expect(countImportSummaryIssues(asset)).toEqual({
      errors: 0,
      warnings: 3
    });
  });
});
