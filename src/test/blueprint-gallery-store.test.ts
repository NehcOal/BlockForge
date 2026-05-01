import { describe, expect, it } from "vitest";
import { createGalleryItem, InMemoryBlueprintGalleryStore } from "@/lib/gallery/galleryStore";
import { voxelModelToBlueprintV2 } from "@/lib/voxel";
import { createSmallCottage } from "@/lib/voxel/presets";

describe("blueprint gallery store", () => {
  it("saves, lists, loads, duplicates, and deletes gallery items", () => {
    const store = new InMemoryBlueprintGalleryStore();
    const item = createGalleryItem({ blueprintV2: voxelModelToBlueprintV2(createSmallCottage()), source: "preset", favorite: true });
    store.save(item);
    expect(store.list()).toHaveLength(1);
    expect(store.load(item.id)?.favorite).toBe(true);
    expect(store.duplicate(item.id, "copy-id")?.name).toContain("Copy");
    expect(store.delete(item.id)).toBe(true);
  });
});
