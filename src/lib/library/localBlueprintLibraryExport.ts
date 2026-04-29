import { createPackManifestFromModels } from "@/lib/voxel";
import type { LocalBlueprintLibraryItem } from "@/lib/library/localBlueprintLibraryTypes";

export function createLibraryPackSummary(items: LocalBlueprintLibraryItem[]) {
  return createPackManifestFromModels(
    items.map((item) => ({
      id: item.blueprintV2.id,
      name: item.blueprintV2.name,
      description: item.blueprintV2.description,
      size: item.blueprintV2.size,
      blocks: item.blueprintV2.blocks.map((block) => ({
        x: block.x,
        y: block.y,
        z: block.z,
        block: block.state as never
      }))
    })),
    {
      packId: "local_library",
      name: "Local Blueprint Library",
      version: "3.0.0-alpha.1",
      description: "Exported from BlockForge Local Blueprint Library",
      blockforgeVersion: "3.0.0-alpha.1"
    }
  );
}
