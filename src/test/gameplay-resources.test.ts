import { existsSync, readFileSync } from "node:fs";
import path from "node:path";
import { describe, expect, it } from "vitest";

const repoRoot = process.cwd();
const connectors = ["neoforge-connector", "fabric-connector", "forge-connector"] as const;
const gameplayBlocks = [
  "blueprint_table",
  "material_cache",
  "builder_anchor",
  "builder_station",
  "material_link",
  "construction_core"
] as const;

function resourcePath(connector: string, ...segments: string[]) {
  return path.join(repoRoot, "mod", connector, "src", "main", "resources", ...segments);
}

describe("gameplay block resources", () => {
  it("has models, blockstates, textures, loot tables, recipes, and lang keys across all loaders", () => {
    for (const connector of connectors) {
      const langBase = resourcePath(connector, "assets", "blockforge_connector", "lang");
      const en = JSON.parse(readFileSync(path.join(langBase, "en_us.json"), "utf8")) as Record<string, string>;
      const zh = JSON.parse(readFileSync(path.join(langBase, "zh_cn.json"), "utf8")) as Record<string, string>;

      for (const id of gameplayBlocks) {
        expect(existsSync(resourcePath(connector, "assets", "blockforge_connector", "blockstates", `${id}.json`)), `${connector} ${id} blockstate`).toBe(true);
        expect(existsSync(resourcePath(connector, "assets", "blockforge_connector", "models", "block", `${id}.json`)), `${connector} ${id} block model`).toBe(true);
        expect(existsSync(resourcePath(connector, "assets", "blockforge_connector", "models", "item", `${id}.json`)), `${connector} ${id} item model`).toBe(true);
        expect(existsSync(resourcePath(connector, "assets", "blockforge_connector", "textures", "block", `${id}.png`)), `${connector} ${id} texture`).toBe(true);
        expect(existsSync(resourcePath(connector, "data", "blockforge_connector", "loot_tables", "blocks", `${id}.json`)), `${connector} ${id} loot table`).toBe(true);
        expect(existsSync(resourcePath(connector, "data", "blockforge_connector", "recipes", `${id}.json`)), `${connector} ${id} recipe`).toBe(true);
        expect(en[`block.blockforge_connector.${id}`], `${connector} ${id} en block lang`).toBeTruthy();
        expect(zh[`block.blockforge_connector.${id}`], `${connector} ${id} zh block lang`).toBeTruthy();
      }
    }
  });
});
