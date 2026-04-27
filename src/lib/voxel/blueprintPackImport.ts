import JSZip from "jszip";
import {
  BLOCKFORGE_PACK_MANIFEST,
  createPackRegistryId,
  isBlueprintJson,
  validatePackBlueprintPath,
  validatePackManifest,
  type ImportedBlueprintPack
} from "@/lib/voxel/blueprintPack";

export async function importBlueprintPackZip(
  file: File | Blob
): Promise<ImportedBlueprintPack> {
  const zip = await JSZip.loadAsync(file);
  const manifestFile = zip.file(BLOCKFORGE_PACK_MANIFEST);

  if (!manifestFile) {
    throw new Error("Blueprint pack is missing blockforge-pack.json.");
  }

  const manifest = validatePackManifest(JSON.parse(await manifestFile.async("string")));
  const blueprints = [];
  const warnings: string[] = [];

  for (const entry of manifest.blueprints) {
    const path = validatePackBlueprintPath(entry.path);
    const blueprintFile = zip.file(path);

    if (!blueprintFile) {
      throw new Error(`Blueprint pack is missing ${path}.`);
    }

    const blueprint = JSON.parse(await blueprintFile.async("string"));
    if (!isBlueprintJson(blueprint)) {
      throw new Error(`Invalid blueprint JSON in ${path}.`);
    }

    blueprints.push({
      id: entry.id,
      registryId: createPackRegistryId(manifest.packId, entry.id),
      name: entry.name,
      path,
      blueprint
    });
  }

  return {
    manifest,
    blueprints,
    warnings
  };
}
