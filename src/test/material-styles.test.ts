import { describe, expect, it } from "vitest";
import { blockTypes, materialStyles } from "@/lib/voxel";

describe("material styles", () => {
  it("covers every block type", () => {
    expect(Object.keys(materialStyles).sort()).toEqual([...blockTypes].sort());
  });

  it("marks transparent and emissive blocks", () => {
    expect(materialStyles.glass.transparent).toBe(true);
    expect(materialStyles.water.transparent).toBe(true);
    expect(materialStyles.torch.emissiveIntensity).toBeGreaterThan(0);
    expect(materialStyles.gold_block.emissiveIntensity).toBeGreaterThan(0);
  });
});
