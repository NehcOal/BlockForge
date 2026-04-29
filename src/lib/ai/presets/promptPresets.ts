import type { AiPromptPreset, AiPromptPresetCategory } from "@/lib/ai/presets/promptPresetTypes";

export const aiPromptPresets = [
  preset("medieval-watchtower", "Medieval Watchtower", "medieval", "A medium medieval stone watchtower with windows, battlements, and a small entry door.", ["tower", "stone", "medieval"], { width: 13, height: 18, depth: 13 }),
  preset("starter-cottage", "Starter Cottage", "starter", "A small survival starter cottage with wood walls, stone foundation, windows, and a simple roof.", ["cottage", "starter", "wood"], { width: 14, height: 9, depth: 11 }),
  preset("stone-bridge", "Stone Bridge", "bridge", "A stone bridge over water with railings and support pillars.", ["bridge", "stone"], { width: 19, height: 7, depth: 9 }),
  preset("dungeon-entrance", "Dungeon Entrance", "dungeon", "A compact dungeon entrance with stone bricks, torches, and an archway.", ["dungeon", "entrance"], { width: 15, height: 10, depth: 10 }),
  preset("pixel-statue", "Pixel Statue", "statue", "A simple pixel statue with wool accents on a stone base.", ["statue", "pixel"], { width: 11, height: 16, depth: 7 }),
  preset("survival-storage-shed", "Survival Storage Shed", "survival", "A survival-friendly storage shed with a wide floor plan and simple materials.", ["survival", "storage", "utility"], { width: 12, height: 8, depth: 10 }),
  preset("wizard-tower", "Wizard Tower", "medieval", "A tall wizard tower with more windows, a strong stone shell, and a small roof cap.", ["tower", "wizard", "stone"], { width: 15, height: 24, depth: 15 }),
  preset("small-market-stall", "Small Market Stall", "utility", "A small market stall with wood supports, open sides, and a compact roof.", ["market", "utility", "wood"], { width: 9, height: 7, depth: 7 }),
  preset("nether-shrine", "Nether Shrine", "custom", "A small shrine-like structure using dark stone shapes and gold accents.", ["shrine", "gold", "custom"], { width: 11, height: 10, depth: 11 }),
  preset("mine-entrance", "Mine Entrance", "survival", "A mine entrance with stone supports, torches, and a simple front arch.", ["mine", "entrance", "survival"], { width: 13, height: 9, depth: 9 }),
  preset("castle-gate", "Castle Gate", "medieval", "A castle gate facade with stone walls, door opening, and two side pillars.", ["castle", "gate", "stone"], { width: 17, height: 14, depth: 7 }),
  preset("garden-fountain", "Garden Fountain", "utility", "A small garden fountain with water basin, stone trim, and a central accent.", ["garden", "fountain", "water"], { width: 9, height: 6, depth: 9 })
] as const satisfies AiPromptPreset[];

export function getAiPromptPresets(category?: AiPromptPresetCategory): AiPromptPreset[] {
  return category ? aiPromptPresets.filter((presetItem) => presetItem.category === category) : [...aiPromptPresets];
}

export function getAiPromptPresetById(id: string): AiPromptPreset | undefined {
  return aiPromptPresets.find((presetItem) => presetItem.id === id);
}

function preset(
  id: string,
  name: string,
  category: AiPromptPresetCategory,
  prompt: string,
  tags: string[],
  sizeHint?: AiPromptPreset["sizeHint"]
): AiPromptPreset {
  return {
    id,
    name,
    description: prompt,
    category,
    prompt,
    sizeHint,
    maxBlocks: 2000,
    tags
  };
}
