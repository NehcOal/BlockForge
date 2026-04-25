import { describe, expect, it } from "vitest";
import { blockTypes, minecraftBlockIds } from "@/lib/voxel";

describe("minecraft block id mapping", () => {
  it("covers every BlockType", () => {
    expect(Object.keys(minecraftBlockIds).sort()).toEqual([...blockTypes].sort());
  });

  it("maps BlockForge block types to Minecraft Java block ids", () => {
    expect(minecraftBlockIds.stone_bricks).toBe("minecraft:stone_bricks");
    expect(minecraftBlockIds.door).toBe("minecraft:oak_door");
    expect(minecraftBlockIds.grass).toBe("minecraft:grass_block");
    expect(minecraftBlockIds.wool_red).toBe("minecraft:red_wool");
  });
});
