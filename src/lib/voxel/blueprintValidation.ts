import type { BlockForgeBlueprintV1 } from "@/lib/voxel/blueprintProtocol";
import type { BlockForgeBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";

export type ValidationSeverity = "error" | "warning";

export type BlueprintValidationIssue = {
  severity: ValidationSeverity;
  field: string;
  message: string;
};

export type BlueprintValidationReport = {
  valid: boolean;
  issues: BlueprintValidationIssue[];
};

type BlueprintLike = BlockForgeBlueprintV1 | BlockForgeBlueprintV2;

export function validateBlueprintJson(value: unknown): BlueprintValidationReport {
  const issues: BlueprintValidationIssue[] = [];
  if (!isRecord(value)) {
    return report([error("$", "Blueprint must be a JSON object.")]);
  }

  if (value.schemaVersion !== 1 && value.schemaVersion !== 2) {
    issues.push(error("schemaVersion", "Blueprint schemaVersion must be 1 or 2."));
    return report(issues);
  }

  requireString(value, "id", issues);
  requireString(value, "name", issues);
  requireExactString(value, "minecraftVersion", "1.21.1", issues);
  requireExactString(value, "generator", "BlockForge", issues);
  validateSize(value.size, issues);
  validateOrigin(value.origin, issues);

  if (!isRecord(value.palette)) {
    issues.push(error("palette", "Blueprint palette must be an object."));
  } else if (value.schemaVersion === 1) {
    validatePaletteV1(value.palette, issues);
  } else {
    validatePaletteV2(value.palette, issues);
  }

  if (!Array.isArray(value.blocks)) {
    issues.push(error("blocks", "Blueprint blocks must be an array."));
  } else if (isSize(value.size) && isRecord(value.palette)) {
    validateBlocks(value as BlueprintLike, issues);
  }

  return report(issues);
}

export function formatValidationSummary(report: BlueprintValidationReport): string {
  const errors = report.issues.filter((issue) => issue.severity === "error").length;
  const warnings = report.issues.filter((issue) => issue.severity === "warning").length;
  if (errors === 0 && warnings === 0) {
    return "valid";
  }
  return `${errors} error(s), ${warnings} warning(s)`;
}

function validatePaletteV1(palette: Record<string, unknown>, issues: BlueprintValidationIssue[]): void {
  for (const [key, value] of Object.entries(palette)) {
    if (typeof value !== "string" || !value.trim()) {
      issues.push(error(`palette.${key}`, "Blueprint v1 palette entries must be non-empty block id strings."));
    }
  }
}

function validatePaletteV2(palette: Record<string, unknown>, issues: BlueprintValidationIssue[]): void {
  for (const [key, value] of Object.entries(palette)) {
    if (!isRecord(value)) {
      issues.push(error(`palette.${key}`, "Blueprint v2 palette entries must be objects."));
      continue;
    }
    if (typeof value.name !== "string" || !value.name.trim()) {
      issues.push(error(`palette.${key}.name`, "Blueprint v2 palette entry name must be a non-empty string."));
    }
    if (value.properties !== undefined && !isStringRecord(value.properties)) {
      issues.push(error(`palette.${key}.properties`, "Blueprint v2 palette properties must be string key/value pairs."));
    }
  }
}

function validateBlocks(blueprint: BlueprintLike, issues: BlueprintValidationIssue[]): void {
  const seen = new Set<string>();
  blueprint.blocks.forEach((block, index) => {
    const field = `blocks[${index}]`;
    if (!isRecord(block)) {
      issues.push(error(field, "Blueprint block must be an object."));
      return;
    }

    const x = readInteger(block.x, `${field}.x`, issues);
    const y = readInteger(block.y, `${field}.y`, issues);
    const z = readInteger(block.z, `${field}.z`, issues);
    if (x === undefined || y === undefined || z === undefined) {
      return;
    }

    if (x < 0 || x >= blueprint.size.width || y < 0 || y >= blueprint.size.height || z < 0 || z >= blueprint.size.depth) {
      issues.push(error(field, "Blueprint block coordinate is outside declared size."));
    }

    const coordinateKey = `${x},${y},${z}`;
    if (seen.has(coordinateKey)) {
      issues.push(error(field, `Duplicate blueprint block coordinate: ${coordinateKey}.`));
    }
    seen.add(coordinateKey);

    const paletteKey = blueprint.schemaVersion === 1
      ? readPaletteReference(block, "block")
      : readPaletteReference(block, "state");
    const paletteField = blueprint.schemaVersion === 1 ? `${field}.block` : `${field}.state`;
    if (typeof paletteKey !== "string" || !paletteKey.trim()) {
      issues.push(error(paletteField, "Blueprint block palette reference must be a non-empty string."));
      return;
    }
    if (!(paletteKey in blueprint.palette)) {
      issues.push(error(paletteField, `Blueprint block references missing palette entry: ${paletteKey}.`));
    }
  });
}

function readPaletteReference(block: Record<string, unknown>, field: "block" | "state"): unknown {
  return block[field];
}

function validateSize(value: unknown, issues: BlueprintValidationIssue[]): void {
  if (!isSize(value)) {
    issues.push(error("size", "Blueprint size must include positive integer width, height, and depth."));
  }
}

function validateOrigin(value: unknown, issues: BlueprintValidationIssue[]): void {
  if (value === undefined) {
    issues.push(warning("origin", "Blueprint origin is missing; connectors assume 0,0,0."));
    return;
  }
  if (!isRecord(value) || !Number.isInteger(value.x) || !Number.isInteger(value.y) || !Number.isInteger(value.z)) {
    issues.push(error("origin", "Blueprint origin must include integer x, y, and z."));
  }
}

function requireString(value: Record<string, unknown>, field: string, issues: BlueprintValidationIssue[]): void {
  if (typeof value[field] !== "string" || !value[field].trim()) {
    issues.push(error(field, `Blueprint ${field} must be a non-empty string.`));
  }
}

function requireExactString(
  value: Record<string, unknown>,
  field: string,
  expected: string,
  issues: BlueprintValidationIssue[]
): void {
  if (value[field] !== expected) {
    issues.push(error(field, `Blueprint ${field} must be ${expected}.`));
  }
}

function readInteger(value: unknown, field: string, issues: BlueprintValidationIssue[]): number | undefined {
  if (!Number.isInteger(value)) {
    issues.push(error(field, "Blueprint block coordinate must be an integer."));
    return undefined;
  }
  return Number(value);
}

function report(issues: BlueprintValidationIssue[]): BlueprintValidationReport {
  return {
    valid: issues.every((issue) => issue.severity !== "error"),
    issues
  };
}

function error(field: string, message: string): BlueprintValidationIssue {
  return { severity: "error", field, message };
}

function warning(field: string, message: string): BlueprintValidationIssue {
  return { severity: "warning", field, message };
}

function isSize(value: unknown): value is BlueprintLike["size"] {
  if (!isRecord(value)) {
    return false;
  }
  const width = value.width;
  const height = value.height;
  const depth = value.depth;
  return isRecord(value)
    && Number.isInteger(width)
    && Number.isInteger(height)
    && Number.isInteger(depth)
    && Number(width) > 0
    && Number(height) > 0
    && Number(depth) > 0;
}

function isStringRecord(value: unknown): value is Record<string, string> {
  return isRecord(value) && Object.values(value).every((item) => typeof item === "string");
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}
