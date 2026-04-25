import { describe, expect, it } from "vitest";
import { createSafeFileName } from "@/lib/voxel";

describe("export utils", () => {
  it("creates safe mcfunction filenames", () => {
    expect(createSafeFileName("Medieval Tower", "mcfunction")).toBe(
      "medieval-tower.mcfunction"
    );
    expect(createSafeFileName("Small Cottage", ".mcfunction")).toBe(
      "small-cottage.mcfunction"
    );
  });

  it("removes special characters and falls back for empty names", () => {
    expect(createSafeFileName("Dungeon Entrance!!!", "json")).toBe(
      "dungeon-entrance.json"
    );
    expect(createSafeFileName(" !!! ", "mcfunction")).toBe(
      "blockforge-blueprint.mcfunction"
    );
  });
});
