import { describe, expect, it } from "vitest";
import {
  createImportedPackAsset,
  createImportedBlueprintAsset,
  createImportedSchematicAsset,
  type ImportedBlueprintPack
} from "@/lib/voxel";
import { voxelModelToBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";
import { blueprintV2ToSpongeSchematic } from "@/lib/voxel/schematic/spongeSchematicExport";
import { getPresetById } from "@/lib/voxel";

describe("blueprint import summaries", () => {
  it("summarizes imported blueprint packs with validation state", () => {
    const blueprint = voxelModelToBlueprintV2(getPresetById("small-cottage"));
    const imported: ImportedBlueprintPack = {
      manifest: {
        schemaVersion: 1,
        packId: "starter",
        name: "Starter",
        version: "1.0.0",
        minecraftVersion: "1.21.1",
        blockforgeVersion: "1.7.0-alpha.1",
        blueprints: []
      },
      blueprints: [
        {
          id: "small_cottage",
          registryId: "starter/small_cottage",
          name: "Small Cottage",
          path: "blueprints/small_cottage.blueprint.json",
          blueprint
        }
      ],
      warnings: []
    };

    const asset = createImportedPackAsset(imported);

    expect(asset.sourceType).toBe("pack");
    expect(asset.summary).toEqual({
      blueprintCount: 1,
      warningCount: 0,
      validationSummary: "valid"
    });
    expect(asset.blueprints[0]).toMatchObject({
      id: "starter/small_cottage",
      blockCount: blueprint.blocks.length,
      paletteCount: Object.keys(blueprint.palette).length
    });
  });

  it("summarizes imported schematics with warning count", () => {
    const blueprint = voxelModelToBlueprintV2(getPresetById("small-cottage"));
    const asset = createImportedSchematicAsset({
      blueprint,
      schematic: blueprintV2ToSpongeSchematic(blueprint),
      warnings: ["Entities are ignored in Web schematic import Alpha."]
    });

    expect(asset.sourceType).toBe("schematic");
    expect(asset.summary.warningCount).toBe(1);
    expect(asset.summary.validationSummary).toBe("valid");
    expect(asset.blueprints[0]?.size).toEqual(blueprint.size);
  });

  it("summarizes single Blueprint JSON imports with validation errors", () => {
    const asset = createImportedBlueprintAsset({
      schemaVersion: 2,
      id: "bad",
      name: "Bad",
      minecraftVersion: "1.21.1",
      generator: "BlockForge",
      size: { width: 1, height: 1, depth: 1 },
      origin: { x: 0, y: 0, z: 0 },
      palette: {},
      blocks: [{ x: 0, y: 0, z: 0, state: "missing" }]
    });

    expect(asset.sourceType).toBe("blueprint");
    expect(asset.summary.validationSummary).toBe("1 error(s), 0 warning(s)");
    expect(asset.blueprints[0]?.validation.valid).toBe(false);
  });

  it("summarizes valid single Blueprint JSON imports", () => {
    const blueprint = voxelModelToBlueprintV2(getPresetById("small-cottage"));
    const asset = createImportedBlueprintAsset(blueprint, "small_cottage.blueprint");

    expect(asset.sourceType).toBe("blueprint");
    expect(asset.summary.validationSummary).toBe("valid");
    expect(asset.blueprints[0]).toMatchObject({
      id: "small_cottage.blueprint",
      name: "Small Cottage",
      blockCount: blueprint.blocks.length
    });
  });
});
