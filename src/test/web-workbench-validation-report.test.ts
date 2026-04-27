import { describe, expect, it } from "vitest";
import { validateBlueprintJson } from "@/lib/voxel";

describe("Web Workbench validation report", () => {
  it("returns sectioned issues with severity, path, message, and suggestions", () => {
    const report = validateBlueprintJson({
      schemaVersion: 2,
      id: "bad",
      name: "Bad",
      minecraftVersion: "1.21.1",
      generator: "BlockForge",
      size: { width: 2, height: 2, depth: 2 },
      origin: { x: 0, y: 0, z: 0 },
      palette: { stone: { name: "minecraft:stone" } },
      blocks: [
        { x: 1, y: 0, z: 0, state: "stonee" },
        { x: 20, y: 0, z: 0, state: "stone" },
        { x: 1, y: 0, z: 0, state: "stone" }
      ]
    });

    expect(report.valid).toBe(false);
    expect(report.issues).toEqual(expect.arrayContaining([
      expect.objectContaining({ severity: "error", section: "Missing palette references", field: "blocks[0].state" }),
      expect.objectContaining({ severity: "error", section: "Coordinates", field: "blocks[1]" }),
      expect.objectContaining({ severity: "warning", section: "Duplicate blocks", field: "blocks[2]" }),
      expect.objectContaining({ severity: "info", section: "Blocks", field: "blocks" })
    ]));
    expect(report.issues.find((issue) => issue.suggestion)).toBeDefined();
  });
});
