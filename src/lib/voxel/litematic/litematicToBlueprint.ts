import { validateBlueprintJson } from "@/lib/voxel/blueprintValidation";
import type { BlockForgeBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";
import { createSafeResourcePath } from "@/lib/voxel/exportUtils";
import { formatLitematicBlockState } from "@/lib/voxel/litematic/litematicBlockState";
import type { ParsedLitematic } from "@/lib/voxel/litematic/litematicTypes";

export type LitematicToBlueprintResult = {
  blueprint: BlockForgeBlueprintV2;
  warnings: string[];
};

export function litematicToBlueprintV2(parsed: ParsedLitematic): LitematicToBlueprintResult {
  const warnings: string[] = [];
  const palette: BlockForgeBlueprintV2["palette"] = {};
  const rawBlocks: BlockForgeBlueprintV2["blocks"] = [];
  let minX = 0;
  let minY = 0;
  let minZ = 0;
  const seen = new Map<string, number>();

  for (const region of parsed.regions) {
    const offset = region.position ?? { x: 0, y: 0, z: 0 };
    for (const [key, state] of Object.entries(region.palette)) {
      const paletteKey = createSafeResourcePath(`${region.name}-${key}`);
      const blockName = state.name.startsWith("minecraft:") ? state.name : `minecraft:${state.name}`;
      if (!state.name.startsWith("minecraft:")) {
        warnings.push(`Palette "${key}" did not include a minecraft: namespace; normalized to ${blockName}.`);
      }
      palette[paletteKey] = {
        name: blockName,
        properties: state.properties
      };
    }
    for (const block of region.blocks) {
      const paletteKey = createSafeResourcePath(`${region.name}-${block.state}`);
      if (!(paletteKey in palette)) {
        warnings.push(`Block at ${block.x},${block.y},${block.z} references unknown palette key "${block.state}" and was skipped.`);
        continue;
      }
      const x = block.x + offset.x;
      const y = block.y + offset.y;
      const z = block.z + offset.z;
      const coordinateKey = `${x},${y},${z}`;
      if (seen.has(coordinateKey)) {
        warnings.push(`Duplicate block coordinate ${coordinateKey}; later Litematic block wins.`);
        rawBlocks[seen.get(coordinateKey)!] = { x, y, z, state: paletteKey };
      } else {
        seen.set(coordinateKey, rawBlocks.length);
        rawBlocks.push({ x, y, z, state: paletteKey });
      }
      minX = Math.min(minX, x);
      minY = Math.min(minY, y);
      minZ = Math.min(minZ, z);
    }
    for (const [key, state] of Object.entries(region.palette)) {
      if (!state.name.startsWith("minecraft:")) continue;
      if (state.name.includes("entity") || formatLitematicBlockState(state).includes("waterlogged=true")) {
        warnings.push(`Palette "${key}" contains a state that may be partial in BlockForge alpha import.`);
      }
    }
  }
  const blocks = rawBlocks.map((block) => ({
    ...block,
    x: block.x - minX,
    y: block.y - minY,
    z: block.z - minZ
  }));
  const maxX = blocks.reduce((max, block) => Math.max(max, block.x + 1), 0);
  const maxY = blocks.reduce((max, block) => Math.max(max, block.y + 1), 0);
  const maxZ = blocks.reduce((max, block) => Math.max(max, block.z + 1), 0);
  if (minX < 0 || minY < 0 || minZ < 0) {
    warnings.push(`Litematic region offsets were normalized by ${-minX},${-minY},${-minZ} to fit Blueprint coordinates.`);
  }

  const blueprint: BlockForgeBlueprintV2 = {
    schemaVersion: 2,
    id: createSafeResourcePath(parsed.name || "litematic-import"),
    name: parsed.name || "Imported Litematic",
    description: parsed.description || "Imported from experimental Litematica alpha support.",
    minecraftVersion: "1.21.1",
    generator: "BlockForge",
    size: {
      width: Math.max(1, maxX),
      height: Math.max(1, maxY),
      depth: Math.max(1, maxZ)
    },
    origin: { x: 0, y: 0, z: 0 },
    palette,
    blocks
  };

  const report = validateBlueprintJson(blueprint);
  for (const issue of report.issues) {
    if (issue.severity === "error") {
      warnings.push(`Blueprint validation: ${issue.message}`);
    }
  }

  return { blueprint, warnings };
}
