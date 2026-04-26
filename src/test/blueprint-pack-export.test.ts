import JSZip from "jszip";
import { describe, expect, it } from "vitest";
import { voxelModelsToBlueprintPackZip } from "@/lib/voxel/blueprintPackExport";
import { getPresetById } from "@/lib/voxel";

describe("blueprint pack export", () => {
  it("exports a zip with manifest and blueprint json", async () => {
    const blob = await voxelModelsToBlueprintPackZip([getPresetById("small-cottage")], {
      packId: "starter",
      name: "Starter",
      version: "1.0.0"
    });
    const zip = await JSZip.loadAsync(blob);
    const manifest = JSON.parse(await zip.file("blockforge-pack.json")!.async("string"));

    expect(manifest.packId).toBe("starter");
    expect(zip.file("blueprints/small-cottage.blueprint.json")).toBeTruthy();
    expect(JSON.parse(await zip.file("blueprints/small-cottage.blueprint.json")!.async("string")).schemaVersion).toBe(2);
  });

  it("keeps each manifest entry matched to its source model when names collide", async () => {
    const smallCottage = {
      ...getPresetById("small-cottage"),
      name: "Shared Name"
    };
    const medievalTower = {
      ...getPresetById("medieval-tower"),
      name: "Shared Name"
    };
    const blob = await voxelModelsToBlueprintPackZip([smallCottage, medievalTower], {
      packId: "starter",
      name: "Starter",
      version: "1.0.0"
    });
    const zip = await JSZip.loadAsync(blob);
    const cottageBlueprint = JSON.parse(await zip.file("blueprints/small-cottage.blueprint.json")!.async("string"));
    const towerBlueprint = JSON.parse(await zip.file("blueprints/medieval-tower.blueprint.json")!.async("string"));

    expect(cottageBlueprint.id).toBe("small-cottage");
    expect(towerBlueprint.id).toBe("medieval-tower");
    expect(towerBlueprint.blocks).toHaveLength(getPresetById("medieval-tower").blocks.length);
  });
});
