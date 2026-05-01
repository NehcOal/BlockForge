import { describe, expect, it } from "vitest";
import { importBlueprintGalleryBundle } from "@/lib/gallery/galleryImport";
import { createGalleryItem } from "@/lib/gallery/galleryStore";
import { exportBlueprintGalleryBundle } from "@/lib/gallery/galleryExport";
import { voxelModelToBlueprintV2 } from "@/lib/voxel";
import { createSmallCottage } from "@/lib/voxel/presets";

describe("blueprint gallery import", () => {
  it("imports a gallery bundle", async () => {
    const item = createGalleryItem({ blueprintV2: voxelModelToBlueprintV2(createSmallCottage()), source: "preset" });
    const bundle = await exportBlueprintGalleryBundle([item]);
    const result = await importBlueprintGalleryBundle(bundle);
    expect(result.report.status).toBe("success");
    expect(result.items).toHaveLength(1);
  });

  it("rejects path traversal ids", async () => {
    const item = createGalleryItem({ blueprintV2: voxelModelToBlueprintV2(createSmallCottage()), source: "preset" });
    const bundle = await exportBlueprintGalleryBundle([{ ...item, id: "../bad" }]);
    const result = await importBlueprintGalleryBundle(bundle);
    expect(result.report.status).toBe("error");
    expect(result.items).toHaveLength(0);
  });
});
