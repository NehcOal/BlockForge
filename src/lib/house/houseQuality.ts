import type { HouseModuleType, HousePlan } from "@/lib/house/housePlan";

export type HouseQualityReport = {
  total: number;
  enclosure: number;
  roof: number;
  entrance: number;
  windows: number;
  interior: number;
  buildability: number;
  materials: number;
  warnings: string[];
  suggestions: string[];
};

const rareBlocks = new Set(["gold_block", "amethyst_block"]);

export function analyzeHouseQuality(plan: HousePlan): HouseQualityReport {
  const warnings: string[] = [];
  const suggestions: string[] = [];
  const hasFoundation = hasModule(plan, "foundation") || hasModule(plan, "floor");
  const hasDoor = plan.openings.doors.length > 0 || hasModule(plan, "door");
  const hasWindow = plan.openings.windows.length > 0 || hasModule(plan, "window");
  const hasRoof = hasModule(plan, "roof");
  const hasStair = hasModule(plan, "stair");
  const enclosure = hasFoundation ? 90 : 40;
  const roof = !plan.options.buildRoof ? 80 : hasRoof ? 90 : 20;
  const entrance = hasDoor ? 100 : 10;
  const windows = !plan.options.addWindows ? 85 : hasWindow ? 90 : 30;
  const interior = plan.options.hollowInterior ? 90 : 45;
  const buildability = plan.modules.reduce((sum, houseModule) => sum + houseModule.width * houseModule.height * houseModule.depth, 0) > 5000 ? 45 : 90;
  const materials = plan.options.survivalFriendly && plan.modules.some((houseModule) => rareBlocks.has(houseModule.block)) ? 45 : 92;

  if (!hasFoundation) {
    warnings.push("House has no foundation or floor module.");
    suggestions.push("Enable foundation or interior floor.");
  }
  if (!hasDoor) {
    warnings.push("House has no planned entrance.");
    suggestions.push("Add at least one door.");
  }
  if (plan.options.addWindows && !hasWindow) {
    warnings.push("Windows are enabled but no window modules exist.");
  }
  if (plan.options.buildRoof && !hasRoof) {
    warnings.push("Roof is enabled but no roof module exists.");
  }
  if (plan.dimensions.floors > 1 && !hasStair) {
    warnings.push("Multi-floor house has no stair or ladder plan.");
  }

  return {
    total: clamp(Math.round((enclosure + roof + entrance + windows + interior + buildability + materials) / 7)),
    enclosure,
    roof,
    entrance,
    windows,
    interior,
    buildability,
    materials,
    warnings,
    suggestions
  };
}

export function estimateHouseMaterials(plan: HousePlan) {
  const counts = new Map<string, number>();
  for (const houseModule of plan.modules) {
    const count = houseModule.type === "door" ? 1 : houseModule.type === "window" ? houseModule.width * houseModule.height : Math.max(1, houseModule.width * houseModule.height * houseModule.depth);
    counts.set(houseModule.block, (counts.get(houseModule.block) ?? 0) + count);
  }
  return Array.from(counts.entries())
    .map(([block, count]) => ({ block, count }))
    .sort((a, b) => a.block.localeCompare(b.block));
}

function hasModule(plan: HousePlan, type: HouseModuleType) {
  return plan.modules.some((houseModule) => houseModule.type === type);
}

function clamp(value: number) {
  return Math.max(0, Math.min(100, value));
}
