import { describe, expect, it } from "vitest";
import {
  createPackManifestFromModels,
  createPackRegistryId,
  validatePackBlueprintPath,
  validatePackManifest
} from "@/lib/voxel/blueprintPack";
import { getPresetById } from "@/lib/voxel";

describe("blueprint pack protocol", () => {
  it("creates a valid v1 manifest from voxel models", () => {
    const manifest = createPackManifestFromModels([getPresetById("small-cottage")], {
      packId: "Starter Buildings",
      name: "Starter Buildings",
      version: "1.0.0"
    });

    expect(manifest.schemaVersion).toBe(1);
    expect(manifest.packId).toBe("starter_buildings");
    expect(manifest.blueprints).toHaveLength(1);
    expect(manifest.blueprints[0]?.path).toBe("blueprints/small-cottage.blueprint.json");
  });

  it("resolves pack blueprint registry ids", () => {
    expect(createPackRegistryId("starter_buildings", "tiny_platform")).toBe(
      "starter_buildings/tiny_platform"
    );
  });

  it("rejects unsafe blueprint paths", () => {
    expect(() => validatePackBlueprintPath("../evil.json")).toThrow(/blueprints/);
    expect(() => validatePackBlueprintPath("blueprints/../evil.json")).toThrow(/traversal/);
    expect(() => validatePackBlueprintPath("/blueprints/evil.json")).toThrow(/relative/);
    expect(() => validatePackBlueprintPath("blueprints\\evil.json")).toThrow(/forward slashes/);
  });

  it("rejects duplicate blueprint ids", () => {
    expect(() => validatePackManifest({
      schemaVersion: 1,
      packId: "starter",
      name: "Starter",
      version: "1.0.0",
      minecraftVersion: "1.21.1",
      blockforgeVersion: "1.4.0-alpha.1",
      blueprints: [
        { id: "tiny_platform", name: "Tiny", path: "blueprints/tiny.blueprint.json" },
        { id: "tiny_platform", name: "Tiny 2", path: "blueprints/tiny2.blueprint.json" }
      ]
    })).toThrow(/Duplicate blueprint id/);
  });

  it("rejects unsafe external pack and blueprint ids instead of normalizing them", () => {
    expect(() => validatePackManifest({
      schemaVersion: 1,
      packId: "Starter Buildings",
      name: "Starter",
      version: "1.0.0",
      minecraftVersion: "1.21.1",
      blockforgeVersion: "1.4.0-alpha.1",
      blueprints: [
        { id: "tiny_platform", name: "Tiny", path: "blueprints/tiny.blueprint.json" }
      ]
    })).toThrow(/packId/);

    expect(() => validatePackManifest({
      schemaVersion: 1,
      packId: "starter",
      name: "Starter",
      version: "1.0.0",
      minecraftVersion: "1.21.1",
      blockforgeVersion: "1.4.0-alpha.1",
      blueprints: [
        { id: "Tiny Platform", name: "Tiny", path: "blueprints/tiny.blueprint.json" }
      ]
    })).toThrow(/blueprints\[0\]\.id/);
  });
});
