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
  "construction_core",
  "settlement_core",
  "contract_board",
  "reward_crate",
  "architect_desk",
  "event_board",
  "project_map",
  "emergency_beacon",
  "supply_depot"
] as const;
const gameplayItems = ["builder_wand", ...gameplayBlocks, "architect_ledger", "contract_token", "architect_seal", "event_notice", "project_charter", "emergency_repair_kit", "settlement_seal"] as const;
const sharedLangKeys = [
  "modmenu.nameTranslation.blockforge_connector",
  "item.blockforge_connector.builder_wand",
  "key.categories.blockforge_connector",
  "key.blockforge_connector.open_blueprint_selector",
  "screen.blockforge_connector.blueprint_selector",
  "screen.blockforge_connector.blueprints",
  "screen.blockforge_connector.details",
  "screen.blockforge_connector.no_selection",
  "screen.blockforge_connector.rotation",
  "screen.blockforge_connector.select",
  "screen.blockforge_connector.close",
  "screen.blockforge_connector.loading",
  "screen.blockforge_connector.materials",
  "screen.blockforge_connector.materials_hint",
  "screen.blockforge_connector.enough_materials",
  "screen.blockforge_connector.material_totals",
  "screen.blockforge_connector.blocks",
  "screen.blockforge_connector.size",
  "screen.blockforge_connector.block_states",
  "screen.blockforge_connector.hint",
  "screen.blockforge_connector.empty",
  "command.blockforge_connector.station",
  "command.blockforge_connector.audit",
  "blockforge.command.events",
  "blockforge.command.projects",
  "blockforge.command.emergency"
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

      for (const id of gameplayItems) {
        expect(existsSync(resourcePath(connector, "assets", "blockforge_connector", "models", "item", `${id}.json`)), `${connector} ${id} item model`).toBe(true);
        expect(existsSync(resourcePath(connector, "data", "blockforge_connector", "recipes", `${id}.json`)), `${connector} ${id} recipe`).toBe(true);
        expect(en[`item.blockforge_connector.${id}`], `${connector} ${id} en item lang`).toBeTruthy();
        expect(zh[`item.blockforge_connector.${id}`], `${connector} ${id} zh item lang`).toBeTruthy();
      }

      for (const key of sharedLangKeys) {
        expect(en[key], `${connector} ${key} en lang`).toBeTruthy();
        expect(zh[key], `${connector} ${key} zh lang`).toBeTruthy();
      }
    }
  });
});
