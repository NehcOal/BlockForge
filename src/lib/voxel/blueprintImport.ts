import type { ImportedBlueprintPack } from "@/lib/voxel/blueprintPack";
import type { BlockForgeBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";
import {
  formatValidationSummary,
  validateBlueprintJson,
  type BlueprintValidationReport
} from "@/lib/voxel/blueprintValidation";
import type { ImportedSpongeSchematic } from "@/lib/voxel/schematic/spongeSchematic";

export type ImportedBlueprintSourceType = "pack" | "schematic" | "blueprint";

export type ImportedBlueprintSummary = {
  id: string;
  name: string;
  size: {
    width: number;
    height: number;
    depth: number;
  };
  paletteCount: number;
  blockCount: number;
  validation: BlueprintValidationReport;
};

export type ImportedBlueprintAsset = {
  sourceType: ImportedBlueprintSourceType;
  id: string;
  name: string;
  summary: {
    blueprintCount: number;
    warningCount: number;
    validationSummary: string;
  };
  blueprints: ImportedBlueprintSummary[];
  warnings: string[];
};

export function createImportedPackAsset(imported: ImportedBlueprintPack): ImportedBlueprintAsset {
  const blueprints = imported.blueprints.map((entry) => createBlueprintSummary(
    entry.registryId,
    entry.name,
    entry.blueprint
  ));

  return {
    sourceType: "pack",
    id: imported.manifest.packId,
    name: imported.manifest.name,
    summary: {
      blueprintCount: blueprints.length,
      warningCount: imported.warnings.length,
      validationSummary: summarizeValidations(blueprints)
    },
    blueprints,
    warnings: imported.warnings
  };
}

export function createImportedSchematicAsset(imported: ImportedSpongeSchematic): ImportedBlueprintAsset {
  const blueprints = [
    createBlueprintSummary(imported.blueprint.id, imported.blueprint.name, imported.blueprint)
  ];

  return {
    sourceType: "schematic",
    id: imported.blueprint.id,
    name: imported.blueprint.name,
    summary: {
      blueprintCount: 1,
      warningCount: imported.warnings.length,
      validationSummary: summarizeValidations(blueprints)
    },
    blueprints,
    warnings: imported.warnings
  };
}

export function createImportedBlueprintAsset(blueprint: unknown, id = "imported_blueprint"): ImportedBlueprintAsset {
  const summary = createBlueprintSummary(id, readBlueprintName(blueprint), blueprint);
  return {
    sourceType: "blueprint",
    id: summary.id,
    name: summary.name,
    summary: {
      blueprintCount: 1,
      warningCount: summary.validation.issues.filter((issue) => issue.severity === "warning").length,
      validationSummary: summarizeValidations([summary])
    },
    blueprints: [summary],
    warnings: summary.validation.issues
      .filter((issue) => issue.severity === "warning")
      .map((issue) => `${issue.field}: ${issue.message}`)
  };
}

function createBlueprintSummary(id: string, name: string, blueprint: unknown): ImportedBlueprintSummary {
  const validation = validateBlueprintJson(blueprint);
  const source = blueprint as Partial<BlockForgeBlueprintV2>;
  const size = source.size ?? { width: 0, height: 0, depth: 0 };
  const palette = source.palette ?? {};
  const blocks = source.blocks ?? [];

  return {
    id,
    name,
    size: {
      width: Number(size.width ?? 0),
      height: Number(size.height ?? 0),
      depth: Number(size.depth ?? 0)
    },
    paletteCount: isRecord(palette) ? Object.keys(palette).length : 0,
    blockCount: Array.isArray(blocks) ? blocks.length : 0,
    validation
  };
}

function readBlueprintName(blueprint: unknown): string {
  return isRecord(blueprint) && typeof blueprint.name === "string" && blueprint.name.trim()
    ? blueprint.name.trim()
    : "Imported Blueprint";
}

function summarizeValidations(blueprints: ImportedBlueprintSummary[]): string {
  const issues = blueprints.flatMap((blueprint) => blueprint.validation.issues);
  return formatValidationSummary({
    valid: issues.every((issue) => issue.severity !== "error"),
    issues
  });
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}
