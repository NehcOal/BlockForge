import { describe, expect, it } from "vitest";
import { materialStyles } from "@/lib/voxel";
import type { BlockType } from "@/types/blueprint";

const blockTypes: BlockType[] = [
  "stone_bricks", "cobblestone", "oak_planks", "oak_log", "glass", "torch",
  "door", "stone", "grass", "water", "gold_block", "wool_red", "wool_blue", "wool_white"
];

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
