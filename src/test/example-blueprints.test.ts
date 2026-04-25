import { describe, expect, it } from "vitest";
import { readFileSync } from "node:fs";
import { join } from "node:path";

type ExampleBlueprint = {
  schemaVersion: number;
  id: string;
  minecraftVersion: string;
  generator: string;
  size: {
    width: number;
    height: number;
    depth: number;
  };
  palette: Record<string, string | { name: string; properties?: Record<string, string> }>;
  blocks: Array<{
    x: number;
    y: number;
    z: number;
    block?: string;
    state?: string;
  }>;
};

const exampleFiles = [
  "tiny_platform.blueprint.json",
  "small_test_house.blueprint.json",
  "medieval_tower.blueprint.json",
  "state_test_house.blueprint.json"
];

function readExample(fileName: string): ExampleBlueprint {
  return JSON.parse(
    readFileSync(join(process.cwd(), "examples", "blueprints", fileName), "utf8")
  ) as ExampleBlueprint;
}

describe("example Blueprint files", () => {
  it("ships connector-ready examples", () => {
    expect(exampleFiles).toHaveLength(4);
  });

  it.each(exampleFiles)("%s follows a supported Blueprint contract", (fileName) => {
    const blueprint = readExample(fileName);

    expect([1, 2]).toContain(blueprint.schemaVersion);
    expect(blueprint.id).toMatch(/^[a-z0-9_/-]+$/);
    expect(blueprint.minecraftVersion).toBe("1.21.1");
    expect(blueprint.generator).toBe("BlockForge");
    expect(Object.keys(blueprint.palette).length).toBeGreaterThan(0);
    expect(blueprint.blocks.length).toBeGreaterThan(0);

    for (const entry of Object.values(blueprint.palette)) {
      const minecraftBlockId = typeof entry === "string" ? entry : entry.name;
      expect(minecraftBlockId).toMatch(/^minecraft:/);
    }

    for (const block of blueprint.blocks) {
      const stateKey = blueprint.schemaVersion === 2 ? block.state : block.block;

      expect(Number.isInteger(block.x)).toBe(true);
      expect(Number.isInteger(block.y)).toBe(true);
      expect(Number.isInteger(block.z)).toBe(true);
      expect(block.x).toBeGreaterThanOrEqual(0);
      expect(block.y).toBeGreaterThanOrEqual(0);
      expect(block.z).toBeGreaterThanOrEqual(0);
      expect(block.x).toBeLessThan(blueprint.size.width);
      expect(block.y).toBeLessThan(blueprint.size.height);
      expect(block.z).toBeLessThan(blueprint.size.depth);
      expect(stateKey).toBeDefined();
      expect(blueprint.palette[stateKey ?? ""]).toBeDefined();
    }
  });

  it("includes a v2 state test house with block properties", () => {
    const blueprint = readExample("state_test_house.blueprint.json");

    expect(blueprint.schemaVersion).toBe(2);
    expect(blueprint.palette.door_lower).toEqual({
      name: "minecraft:oak_door",
      properties: {
        facing: "south",
        half: "lower",
        hinge: "left",
        open: "false"
      }
    });
    expect(blueprint.palette.torch_east).toEqual({
      name: "minecraft:wall_torch",
      properties: {
        facing: "east"
      }
    });
  });
});
