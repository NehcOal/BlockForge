import JSZip from "jszip";
import { describe, expect, it } from "vitest";
import { voxelModelToBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";
import { importBlueprintPackZip } from "@/lib/voxel/blueprintPackImport";
import { getPresetById } from "@/lib/voxel";

describe("blueprint pack import", () => {
  it("imports a valid blueprint pack zip", async () => {
    const blob = await createPack({
      blueprints: [
        {
          id: "tiny_platform",
          name: "Tiny Platform",
          path: "blueprints/tiny_platform.blueprint.json"
        }
      ]
    });

    const imported = await importBlueprintPackZip(blob);

    expect(imported.manifest.packId).toBe("starter");
    expect(imported.blueprints).toHaveLength(1);
    expect(imported.blueprints[0]?.registryId).toBe("starter/tiny_platform");
  });

  it("rejects a pack missing its manifest", async () => {
    const zip = new JSZip();
    zip.file("blueprints/tiny_platform.blueprint.json", "{}");

    await expect(importBlueprintPackZip(await zip.generateAsync({ type: "blob" }))).rejects.toThrow(/missing blockforge-pack/);
  });

  it("rejects path traversal entries from the manifest", async () => {
    await expect(createPack({
      blueprints: [
        {
          id: "evil",
          name: "Evil",
          path: "blueprints/../evil.blueprint.json"
        }
      ]
    }).then(importBlueprintPackZip)).rejects.toThrow(/traversal/);
  });

  it("rejects unsafe pack ids instead of normalizing them", async () => {
    await expect(createPack({
      packId: "Starter Buildings",
      blueprints: [
        {
          id: "tiny_platform",
          name: "Tiny Platform",
          path: "blueprints/tiny_platform.blueprint.json"
        }
      ]
    }).then(importBlueprintPackZip)).rejects.toThrow(/packId/);
  });

  it("rejects invalid blueprint json", async () => {
    const zip = new JSZip();
    zip.file("blockforge-pack.json", JSON.stringify({
      schemaVersion: 1,
      packId: "starter",
      name: "Starter",
      version: "1.0.0",
      minecraftVersion: "1.21.1",
      blockforgeVersion: "1.4.0-alpha.1",
      blueprints: [
        {
          id: "bad",
          name: "Bad",
          path: "blueprints/bad.blueprint.json"
        }
      ]
    }));
    zip.file("blueprints/bad.blueprint.json", JSON.stringify({ hello: "world" }));

    await expect(importBlueprintPackZip(await zip.generateAsync({ type: "blob" }))).rejects.toThrow(/Invalid blueprint/);
  });
});

async function createPack(manifestPatch: {
  packId?: string;
  blueprints: Array<{ id: string; name: string; path: string }>;
}) {
  const zip = new JSZip();
  zip.file("blockforge-pack.json", JSON.stringify({
    schemaVersion: 1,
    packId: manifestPatch.packId ?? "starter",
    name: "Starter",
    version: "1.0.0",
    minecraftVersion: "1.21.1",
    blockforgeVersion: "1.4.0-alpha.1",
    blueprints: manifestPatch.blueprints
  }));
  zip.file(
    "blueprints/tiny_platform.blueprint.json",
    JSON.stringify(voxelModelToBlueprintV2(getPresetById("small-cottage")))
  );
  return zip.generateAsync({ type: "blob" });
}
