import {
  canGenerateStructurePresetAt,
  createStructurePresetIndex,
  createStructurePresetModel,
  structurePresets,
  validateStructurePresetRegistry,
  type StructurePresetCategory,
  type StructureTerrainContext
} from "@/lib/voxel/structurePresets";
import type { PresetId, VoxelModel } from "@/types/blueprint";

export type StructurePresetDebugResult = {
  ok: boolean;
  command: string;
  presetId?: PresetId;
  model?: VoxelModel & { id: PresetId };
  errors: string[];
  logs: string[];
  entries?: ReturnType<typeof createStructurePresetIndex>;
};

export function listStructurePresetDebugCommands(): string[] {
  return [
    "/structurepreset list",
    "/structurepreset list category defense",
    "/structurepreset list category ruin",
    "/structurepreset validate",
    ...structurePresets.flatMap((preset) => [
    `/structurepreset generate ${preset.id}`,
    `/structurepreset generate ${preset.id} --force`
    ])
  ];
}

export function generateStructurePresetFromDebugCommand(command: string, context: StructureTerrainContext = defaultDebugTerrainContext): StructurePresetDebugResult {
  return runStructurePresetDebugCommand(command, context);
}

export function runStructurePresetDebugCommand(command: string, context: StructureTerrainContext = defaultDebugTerrainContext): StructurePresetDebugResult {
  const normalized = command.trim().replace(/\s+/g, " ");
  const listMatch = /^\/structurepreset list(?: category ([a-z_]+))?$/i.exec(normalized);
  if (listMatch) {
    const category = listMatch[1] as StructurePresetCategory | undefined;
    const entries = createStructurePresetIndex({ category });
    return {
      ok: true,
      command,
      errors: [],
      logs: [`preset count: ${entries.length}`, category ? `category filter: ${category}` : "category filter: all"],
      entries
    };
  }

  if (normalized === "/structurepreset validate") {
    const errors = validateStructurePresetRegistry(structurePresets);
    return {
      ok: errors.length === 0,
      command,
      errors,
      logs: [`validation ${errors.length === 0 ? "passed" : "failed"}`, `preset count: ${structurePresets.length}`]
    };
  }

  const match = /^\/structurepreset generate ([a-z0-9_-]+)(?: (--force))?$/i.exec(normalized);
  if (!match) {
    return {
      ok: false,
      command,
      errors: ["Expected command format: /structurepreset generate <preset_id>"],
      logs: []
    };
  }

  const presetId = match[1] as PresetId;
  const force = match[2] === "--force";
  const preset = structurePresets.find((candidate) => candidate.id === presetId);
  if (!preset) {
    return {
      ok: false,
      command,
      presetId,
      errors: [`Unknown structure preset: ${presetId}`],
      logs: []
    };
  }

  const terrainCheck = canGenerateStructurePresetAt(context, preset, { force });
  const terrainLogs = [
    `biome: ${context.biomeId}`,
    `terrain rule check: ${terrainCheck.canGenerate ? "passed" : "blocked"}`,
    `force mode: ${force ? "enabled" : "disabled"}`
  ];

  if (!terrainCheck.canGenerate) {
    return {
      ok: false,
      command,
      presetId,
      errors: terrainCheck.messages,
      logs: terrainLogs
    };
  }

  const model = createStructurePresetModel(preset);

  return {
    ok: true,
    command,
    presetId,
    model,
    errors: [],
    logs: [
      `preset id: ${preset.id}`,
      `footprint: ${preset.footprint.width}x${preset.footprint.depth}x${preset.footprint.height}`,
      `palette: ${preset.generationRules.biome}`,
      "generation position: 0,0,0",
      `terrain adjustment: flatten=${preset.generationRules.flattenFoundation ? "small_foundation" : "none"}, avoidWater=${preset.generationRules.avoidWater ? "true" : "false"}`,
      ...terrainLogs
    ]
  };
}

export const defaultDebugTerrainContext: StructureTerrainContext = {
  biomeId: "plains",
  biomeTags: ["plains"],
  nearWater: false,
  nearTrees: false,
  slopeScore: 0.1,
  groundType: "solid",
  distanceFromWater: 12
};
