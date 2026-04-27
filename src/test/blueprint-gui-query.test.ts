import { describe, expect, it } from "vitest";
import { queryBlueprintSummaries, type BlueprintGuiSummary } from "@/lib/voxel";

const fixtures: BlueprintGuiSummary[] = [
  { id: "loose_house", name: "Small House", blockCount: 20, sourceType: "loose", sourceId: "", warningCount: 0, tags: ["starter", "小屋"] },
  { id: "starter/tower", name: "Medieval Tower", blockCount: 120, sourceType: "pack", sourceId: "starter", warningCount: 2, tags: ["tower"] },
  { id: "schem/bridge", name: "Stone Bridge", blockCount: 64, sourceType: "schematic", sourceId: "bridge.schem", warningCount: 1, tags: ["bridge"] },
  { id: "starter/cottage", name: "Cottage", blockCount: 32, sourceType: "pack", sourceId: "starter", warningCount: 0, tags: ["cottage"] }
];

describe("queryBlueprintSummaries", () => {
  it("searches by id, name, source id, and tags", () => {
    expect(queryBlueprintSummaries(fixtures, { searchText: "tower" }).items.map((item) => item.id)).toEqual(["starter/tower"]);
    expect(queryBlueprintSummaries(fixtures, { searchText: "Small" }).items.map((item) => item.id)).toEqual(["loose_house"]);
    expect(queryBlueprintSummaries(fixtures, { searchText: "bridge.schem" }).items.map((item) => item.id)).toEqual(["schem/bridge"]);
    expect(queryBlueprintSummaries(fixtures, { searchText: "小屋" }).items.map((item) => item.id)).toEqual(["loose_house"]);
  });

  it("filters by source and warning state", () => {
    expect(queryBlueprintSummaries(fixtures, { sourceFilter: "loose" }).items.map((item) => item.id)).toEqual(["loose_house"]);
    expect(queryBlueprintSummaries(fixtures, { sourceFilter: "pack" }).totalItems).toBe(2);
    expect(queryBlueprintSummaries(fixtures, { sourceFilter: "schematic" }).items.map((item) => item.id)).toEqual(["schem/bridge"]);
    expect(queryBlueprintSummaries(fixtures, { warningFilter: "with_warnings" }).items.map((item) => item.id)).toEqual(["starter/tower", "schem/bridge"]);
    expect(queryBlueprintSummaries(fixtures, { warningFilter: "without_warnings" }).totalItems).toBe(2);
  });

  it("sorts by name and block count", () => {
    expect(queryBlueprintSummaries(fixtures, { sortMode: "name_asc" }).items[0].name).toBe("Cottage");
    expect(queryBlueprintSummaries(fixtures, { sortMode: "blocks_desc" }).items[0].id).toBe("starter/tower");
  });

  it("paginates and normalizes invalid pages", () => {
    const first = queryBlueprintSummaries(fixtures, { pageSize: 2, page: 0 });
    expect(first.items).toHaveLength(2);
    expect(first.hasNext).toBe(true);
    const last = queryBlueprintSummaries(fixtures, { pageSize: 2, page: 99 });
    expect(last.page).toBe(1);
    expect(last.hasPrevious).toBe(true);
    expect(queryBlueprintSummaries(fixtures, { searchText: "missing", page: -4 }).page).toBe(0);
  });
});
