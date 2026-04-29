import { describe, expect, it } from "vitest";
import { formatLitematicBlockState, parseLitematicBlockState } from "@/lib/voxel";

describe("litematic block state codec", () => {
  it("parses and formats stable block state properties", () => {
    const state = parseLitematicBlockState("minecraft:oak_door[half=lower,facing=north]");
    expect(state).toEqual({
      name: "minecraft:oak_door",
      properties: { facing: "north", half: "lower" }
    });
    expect(formatLitematicBlockState(state)).toBe("minecraft:oak_door[facing=north,half=lower]");
  });
});
