import { describe, expect, it } from "vitest";
import {
  blockStateToSpongeString,
  spongeStringToBlockState
} from "@/lib/voxel/schematic/blockStateString";

describe("Sponge block state strings", () => {
  it("writes block ids without properties", () => {
    expect(blockStateToSpongeString({ name: "minecraft:stone_bricks" })).toBe("minecraft:stone_bricks");
  });

  it("writes sorted properties", () => {
    expect(blockStateToSpongeString({
      name: "minecraft:oak_door",
      properties: {
        open: "false",
        facing: "north",
        half: "lower"
      }
    })).toBe("minecraft:oak_door[facing=north,half=lower,open=false]");
  });

  it("parses properties in any order", () => {
    expect(spongeStringToBlockState("minecraft:oak_door[open=false,facing=north]")).toEqual({
      name: "minecraft:oak_door",
      properties: {
        open: "false",
        facing: "north"
      }
    });
  });

  it("round trips common Minecraft blockstate samples", () => {
    const samples = [
      "minecraft:oak_door[facing=north,half=lower,hinge=left,open=false,powered=false]",
      "minecraft:oak_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
      "minecraft:wall_torch[facing=south]"
    ];

    for (const sample of samples) {
      expect(blockStateToSpongeString(spongeStringToBlockState(sample))).toBe(sample);
    }
  });
});
