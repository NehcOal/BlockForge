import { describe, expect, it } from "vitest";
import {
  getAllPresets,
  getBlockKey,
  getUsedBlockTypes,
  validateVoxelModel
} from "@/lib/voxel";
import type { PresetId } from "@/types/blueprint";

const minimumBlockCounts: Record<PresetId, number> = {
  "medieval-tower": 250,
  "small-cottage": 180,
  "dungeon-entrance": 160,
  "stone-bridge": 130,
  "pixel-statue": 100
};

describe("voxel presets", () => {
  const presets = getAllPresets();

  it("returns exactly five preset models", () => {
    expect(presets).toHaveLength(5);
  });

  it("generates non-empty block lists that meet the MVP minimums", () => {
    for (const preset of presets) {
      expect(preset.blocks.length).toBeGreaterThan(0);
      expect(preset.blocks.length).toBeGreaterThanOrEqual(
        minimumBlockCounts[preset.id]
      );
    }
  });

  it("generates blocks with required fields", () => {
    for (const preset of presets) {
      for (const block of preset.blocks) {
        expect(block).toHaveProperty("x");
        expect(block).toHaveProperty("y");
        expect(block).toHaveProperty("z");
        expect(block).toHaveProperty("block");
      }
    }
  });

  it("keeps every block coordinate inside its model size", () => {
    for (const preset of presets) {
      for (const block of preset.blocks) {
        expect(block.x).toBeGreaterThanOrEqual(0);
        expect(block.x).toBeLessThan(preset.size.width);
        expect(block.y).toBeGreaterThanOrEqual(0);
        expect(block.y).toBeLessThan(preset.size.height);
        expect(block.z).toBeGreaterThanOrEqual(0);
        expect(block.z).toBeLessThan(preset.size.depth);
      }
    }
  });

  it("does not generate duplicate coordinates", () => {
    for (const preset of presets) {
      const keys = preset.blocks.map(getBlockKey);
      expect(new Set(keys).size).toBe(keys.length);
    }
  });

  it("returns valid voxel models", () => {
    for (const preset of presets) {
      expect(validateVoxelModel(preset)).toBe(true);
    }
  });

  it("tracks at least one block type per preset", () => {
    for (const preset of presets) {
      expect(getUsedBlockTypes(preset).length).toBeGreaterThan(0);
    }
  });
});
