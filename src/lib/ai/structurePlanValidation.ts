import { blockTypes, minecraftBlockIds } from "@/lib/voxel";
import type { AiStructurePlan } from "@/lib/ai/structurePlan";
import type { BlockType } from "@/types/blueprint";

export type StructurePlanValidationIssue = {
  path: string;
  message: string;
};

export type StructurePlanValidationReport = {
  valid: boolean;
  errors: StructurePlanValidationIssue[];
  warnings: StructurePlanValidationIssue[];
  estimatedBlocks: number;
};

const blockTypeSet = new Set(blockTypes);

export function validateStructurePlan(
  value: unknown
): StructurePlanValidationReport {
  const errors: StructurePlanValidationIssue[] = [];
  const warnings: StructurePlanValidationIssue[] = [];

  if (!isRecord(value)) {
    return report(errors.concat(issue("$", "Structure plan must be a JSON object.")), warnings, 0);
  }

  if (value.schemaVersion !== 1) {
    errors.push(issue("schemaVersion", "Structure plan schemaVersion must be 1."));
  }

  requireString(value, "name", errors);
  requireString(value, "description", errors);

  if (!["tower", "cottage", "bridge", "dungeon", "statue", "custom"].includes(String(value.intent))) {
    errors.push(issue("intent", "Structure plan intent is not supported."));
  }

  const size = validateSize(value.size, errors);
  const palette = validatePalette(value.palette, errors, warnings);
  const maxBlocks = validateConstraints(value.constraints, errors);
  const estimatedBlocks = validateElements(value.elements, size, palette, errors);

  if (estimatedBlocks > maxBlocks) {
    errors.push(issue("elements", `Estimated ${estimatedBlocks} blocks exceeds maxBlocks=${maxBlocks}.`));
  }

  return report(errors, warnings, estimatedBlocks);
}

export function estimateStructurePlanBlocks(plan: AiStructurePlan): number {
  return plan.elements.reduce((sum, element) => {
    return sum + estimateElementBlocks(element.from, element.to, element.hollow === true);
  }, 0);
}

function validateSize(
  value: unknown,
  errors: StructurePlanValidationIssue[]
): AiStructurePlan["size"] | undefined {
  if (!isRecord(value)) {
    errors.push(issue("size", "Structure plan size must be an object."));
    return undefined;
  }

  const size = {
    width: value.width,
    height: value.height,
    depth: value.depth
  };
  for (const [key, item] of Object.entries(size)) {
    if (!Number.isInteger(item) || Number(item) < 1 || Number(item) > 64) {
      errors.push(issue(`size.${key}`, "Size values must be integers from 1 to 64."));
    }
  }

  if (errors.some((item) => item.path.startsWith("size."))) {
    return undefined;
  }

  return {
    width: Number(size.width),
    height: Number(size.height),
    depth: Number(size.depth)
  };
}

function validatePalette(
  value: unknown,
  errors: StructurePlanValidationIssue[],
  warnings: StructurePlanValidationIssue[]
): Set<string> {
  const keys = new Set<string>();
  if (!isRecord(value)) {
    errors.push(issue("palette", "Structure plan palette must be a non-empty object."));
    return keys;
  }

  const entries = Object.entries(value);
  if (entries.length === 0) {
    errors.push(issue("palette", "Structure plan palette must not be empty."));
  }

  for (const [key, entry] of entries) {
    keys.add(key);
    if (!isRecord(entry)) {
      errors.push(issue(`palette.${key}`, "Palette entry must be an object."));
      continue;
    }

    if (typeof entry.name !== "string" || !entry.name.trim()) {
      errors.push(issue(`palette.${key}.name`, "Palette entry name is required."));
    }

    if (!isBlockType(entry.block)) {
      errors.push(issue(`palette.${key}.block`, "Palette entry block is not supported by BlockForge."));
    }

    if (typeof entry.minecraftBlockId !== "string" || !entry.minecraftBlockId.startsWith("minecraft:")) {
      errors.push(issue(`palette.${key}.minecraftBlockId`, "minecraftBlockId must start with minecraft:."));
    } else if (isBlockType(entry.block) && minecraftBlockIds[entry.block] !== entry.minecraftBlockId) {
      warnings.push(issue(`palette.${key}.minecraftBlockId`, "minecraftBlockId differs from BlockForge's default mapping."));
    }
  }

  return keys;
}

function isBlockType(value: unknown): value is BlockType {
  return typeof value === "string" && blockTypeSet.has(value as BlockType);
}

function validateConstraints(value: unknown, errors: StructurePlanValidationIssue[]): number {
  if (!isRecord(value)) {
    errors.push(issue("constraints", "Structure plan constraints must be an object."));
    return 2000;
  }

  const maxBlocks = value.maxBlocks;
  if (!Number.isInteger(maxBlocks) || Number(maxBlocks) < 1 || Number(maxBlocks) > 5000) {
    errors.push(issue("constraints.maxBlocks", "maxBlocks must be an integer from 1 to 5000."));
  }

  if (value.allowUnsupportedBlocks !== false) {
    errors.push(issue("constraints.allowUnsupportedBlocks", "allowUnsupportedBlocks must be false."));
  }

  return Number.isInteger(maxBlocks) ? Number(maxBlocks) : 2000;
}

function validateElements(
  value: unknown,
  size: AiStructurePlan["size"] | undefined,
  paletteKeys: Set<string>,
  errors: StructurePlanValidationIssue[]
): number {
  if (!Array.isArray(value)) {
    errors.push(issue("elements", "Structure plan elements must be a non-empty array."));
    return 0;
  }

  if (value.length === 0) {
    errors.push(issue("elements", "Structure plan elements must not be empty."));
    return 0;
  }

  let estimatedBlocks = 0;
  const elementIds = new Set<string>();
  value.forEach((element, index) => {
    const path = `elements[${index}]`;
    if (!isRecord(element)) {
      errors.push(issue(path, "Element must be an object."));
      return;
    }

    if (typeof element.id !== "string" || !element.id.trim()) {
      errors.push(issue(`${path}.id`, "id is required."));
    } else if (elementIds.has(element.id)) {
      errors.push(issue(`${path}.id`, `Element id "${element.id}" must be unique.`));
    } else {
      elementIds.add(element.id);
    }
    if (!["floor", "wall", "roof", "window", "door", "pillar", "bridge_deck", "arch", "decoration", "custom"].includes(String(element.type))) {
      errors.push(issue(`${path}.type`, "Element type is not supported."));
    }

    if (typeof element.blockKey !== "string" || !element.blockKey.trim()) {
      errors.push(issue(`${path}.blockKey`, "Element blockKey is required."));
    } else if (!paletteKeys.has(element.blockKey)) {
      errors.push(issue(`${path}.blockKey`, `Element references missing palette key "${element.blockKey}".`));
    }

    const from = readTuple(element.from, `${path}.from`, errors);
    const to = readTuple(element.to, `${path}.to`, errors);
    if (!from || !to) return;

    for (let axis = 0; axis < 3; axis += 1) {
      if (from[axis] > to[axis]) {
        errors.push(issue(`${path}.from`, "Element from coordinates must be less than or equal to to coordinates."));
      }
    }

    if (size) {
      const bounds = [size.width, size.height, size.depth];
      for (let axis = 0; axis < 3; axis += 1) {
        if (from[axis] < 0 || to[axis] >= bounds[axis]) {
          errors.push(issue(path, "Element coordinates must stay inside the declared size."));
          break;
        }
      }
    }

    estimatedBlocks += estimateElementBlocks(from, to, element.hollow === true);
  });

  return estimatedBlocks;
}

function estimateElementBlocks(
  from: [number, number, number],
  to: [number, number, number],
  hollow: boolean
): number {
  const width = to[0] - from[0] + 1;
  const height = to[1] - from[1] + 1;
  const depth = to[2] - from[2] + 1;
  const volume = width * height * depth;
  if (!hollow || width < 3 || height < 3 || depth < 3) {
    return volume;
  }
  return volume - (width - 2) * (height - 2) * (depth - 2);
}

function readTuple(
  value: unknown,
  path: string,
  errors: StructurePlanValidationIssue[]
): [number, number, number] | undefined {
  if (!Array.isArray(value) || value.length !== 3 || !value.every(Number.isInteger)) {
    errors.push(issue(path, "Coordinate must be an integer tuple [x, y, z]."));
    return undefined;
  }
  return [Number(value[0]), Number(value[1]), Number(value[2])];
}

function requireString(
  value: Record<string, unknown>,
  key: string,
  errors: StructurePlanValidationIssue[],
  prefix = ""
): void {
  const path = prefix ? `${prefix}.${key}` : key;
  if (typeof value[key] !== "string" || !String(value[key]).trim()) {
    errors.push(issue(path, `${key} is required.`));
  }
}

function issue(path: string, message: string): StructurePlanValidationIssue {
  return { path, message };
}

function report(
  errors: StructurePlanValidationIssue[],
  warnings: StructurePlanValidationIssue[],
  estimatedBlocks: number
): StructurePlanValidationReport {
  return {
    valid: errors.length === 0,
    errors,
    warnings,
    estimatedBlocks
  };
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}
