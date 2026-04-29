import { describe, expect, it } from "vitest";
import { queryBlueprintGallery } from "@/lib/gallery/gallerySearch";
import { createGalleryItem } from "@/lib/gallery/galleryStore";
import { voxelModelToBlueprintV2 } from "@/lib/voxel";
import { createMedievalTower, createSmallCottage } from "@/lib/voxel/presets";

describe("blueprint gallery search", () => {
  const tower = createGalleryItem({ blueprintV2: voxelModelToBlueprintV2(createMedievalTower()), source: "generated", tags: ["stone"], rating: 5 });
  const cottage = createGalleryItem({ blueprintV2: voxelModelToBlueprintV2(createSmallCottage()), source: "imported-litematic", tags: ["wood"], favorite: true, rating: 3 });

  it("searches by name and filters source, favorite, and tags", () => {
    expect(queryBlueprintGallery([tower, cottage], { searchText: "tower" })).toEqual([tower]);
    expect(queryBlueprintGallery([tower, cottage], { source: "imported-litematic" })).toEqual([cottage]);
    expect(queryBlueprintGallery([tower, cottage], { favoriteOnly: true })).toEqual([cottage]);
    expect(queryBlueprintGallery([tower, cottage], { tags: ["stone"] })).toEqual([tower]);
  });

  it("sorts by rating", () => {
    expect(queryBlueprintGallery([cottage, tower], { sortMode: "rating-desc" })[0].id).toBe(tower.id);
  });
});
