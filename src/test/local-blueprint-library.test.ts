import { describe, expect, it } from "vitest";
import { createLibraryItem } from "@/lib/library/localBlueprintLibrary";
import { searchLocalBlueprintLibrary } from "@/lib/library/localBlueprintLibrarySearch";
import { createLibraryPackSummary } from "@/lib/library/localBlueprintLibraryExport";
import { createSmallCottage, voxelModelToBlueprintV2 } from "@/lib/voxel";

describe("local blueprint library", () => {
  it("saves, searches, favorites, and exports a pack summary", () => {
    const item = createLibraryItem({
      blueprintV2: voxelModelToBlueprintV2(createSmallCottage()),
      source: "preset",
      tags: ["cottage"],
      favorite: true
    });
    expect(searchLocalBlueprintLibrary([item], { searchText: "cottage" })).toHaveLength(1);
    expect(searchLocalBlueprintLibrary([item], { favoriteOnly: true })).toHaveLength(1);
    expect(createLibraryPackSummary([item]).blueprints).toHaveLength(1);
  });
});
