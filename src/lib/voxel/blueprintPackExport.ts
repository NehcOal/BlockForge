import JSZip from "jszip";
import {
  BLOCKFORGE_PACK_MANIFEST,
  createPackManifestFromModels,
  type BlockForgePackManifestV1,
  type PackExportOptions
} from "@/lib/voxel/blueprintPack";
import { voxelModelToBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";
import type { VoxelModel } from "@/types/blueprint";

export async function voxelModelsToBlueprintPackZip(
  models: VoxelModel[],
  options: PackExportOptions
): Promise<Blob> {
  const zip = new JSZip();
  const manifest = createPackManifestFromModels(models, options);

  zip.file(BLOCKFORGE_PACK_MANIFEST, JSON.stringify(manifest, null, 2));
  zip.file("README.md", createPackReadme(manifest));

  for (const [index, entry] of manifest.blueprints.entries()) {
    const model = models[index];
    if (!model) {
      throw new Error(`Missing model for blueprint entry: ${entry.id}`);
    }

    zip.file(entry.path, JSON.stringify(voxelModelToBlueprintV2(model), null, 2));
  }

  return zip.generateAsync({
    type: "blob",
    mimeType: "application/zip",
    compression: "DEFLATE"
  });
}

function createPackReadme(manifest: BlockForgePackManifestV1): string {
  return [
    `# ${manifest.name}`,
    "",
    manifest.description ?? "BlockForge Blueprint Pack.",
    "",
    `Pack ID: ${manifest.packId}`,
    `Version: ${manifest.version}`,
    `Minecraft: ${manifest.minecraftVersion}`,
    "",
    "## Blueprints",
    "",
    ...manifest.blueprints.map((entry) => `- ${entry.id}: ${entry.name}`)
  ].join("\n");
}
