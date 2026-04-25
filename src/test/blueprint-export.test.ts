import { describe, expect, it } from "vitest";
import {
  createBlueprintJsonFileName,
  createBlueprintV2JsonFileName,
  getAllPresets,
  voxelModelToBlueprintV2Json,
  voxelModelToBlueprintJson
} from "@/lib/voxel";

describe("BlockForge Blueprint JSON export", () => {
  const [model] = getAllPresets();

  it("creates a safe blueprint file name", () => {
    expect(createBlueprintJsonFileName(model)).toBe(
      "blockforge-medieval_tower.v1.blueprint.json"
    );
    expect(createBlueprintV2JsonFileName(model)).toBe(
      "blockforge-medieval_tower.v2.blueprint.json"
    );
  });

  it("exports valid Blueprint v1 JSON", () => {
    const json = voxelModelToBlueprintJson(model);
    const parsed = JSON.parse(json) as {
      schemaVersion: number;
      id: string;
      blocks: unknown[];
    };

    expect(parsed.schemaVersion).toBe(1);
    expect(parsed.id).toBe("medieval-tower");
    expect(parsed.blocks).toHaveLength(model.blocks.length);
  });

  it("exports valid Blueprint v2 JSON", () => {
    const json = voxelModelToBlueprintV2Json(model);
    const parsed = JSON.parse(json) as {
      schemaVersion: number;
      palette: Record<string, { name: string }>;
      blocks: Array<{ state: string; block?: string }>;
    };

    expect(parsed.schemaVersion).toBe(2);
    expect(parsed.palette.stone_bricks.name).toBe("minecraft:stone_bricks");
    expect(parsed.blocks[0].state).toBeDefined();
    expect(parsed.blocks[0].block).toBeUndefined();
  });
});
