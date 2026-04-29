import { createImportReport, type ImportReport } from "@/lib/import/importReport";
import type {
  LitematicImportLimits,
  ParsedLitematic
} from "@/lib/voxel/litematic/litematicTypes";
import { defaultLitematicImportLimits } from "@/lib/voxel/litematic/litematicTypes";

export type LitematicValidationResult = {
  valid: boolean;
  warnings: string[];
  errors: string[];
  report: ImportReport;
};

export function validateParsedLitematic(
  parsed: ParsedLitematic,
  limits: LitematicImportLimits = defaultLitematicImportLimits,
  sourceFileName?: string
): LitematicValidationResult {
  const warnings: string[] = [];
  const errors: string[] = [];
  const messages: ImportReport["messages"] = [];

  if (!Number.isInteger(parsed.version) || parsed.version <= 0) {
    errors.push("Litematic version must be a positive integer.");
    messages.push({ severity: "error", path: "version", message: "Litematic version must be a positive integer." });
  } else if (parsed.version > 6) {
    warnings.push(`Unsupported or newer Litematic version ${parsed.version}; import is best-effort.`);
    messages.push({ severity: "warning", path: "version", message: `Unsupported or newer Litematic version ${parsed.version}; import is best-effort.` });
  }

  if (!Array.isArray(parsed.regions) || parsed.regions.length === 0) {
    errors.push("Litematic must contain at least one region.");
    messages.push({ severity: "error", path: "regions", message: "Litematic must contain at least one region." });
  } else if (parsed.regions.length > limits.maxRegionCount) {
    errors.push(`Litematic has ${parsed.regions.length} regions; limit is ${limits.maxRegionCount}.`);
    messages.push({ severity: "error", path: "regions", message: `Litematic has ${parsed.regions.length} regions; limit is ${limits.maxRegionCount}.` });
  } else if (parsed.regions.length > 1) {
    warnings.push("Multiple regions are merged into one Blueprint using relative offsets.");
    messages.push({ severity: "warning", path: "regions", message: "Multiple regions are merged into one Blueprint using relative offsets." });
  }

  let totalVolume = 0;
  parsed.regions?.forEach((region, index) => {
    const path = `regions[${index}]`;
    const size = region.size;
    if (!size || !isPositiveInteger(size.width) || !isPositiveInteger(size.height) || !isPositiveInteger(size.depth)) {
      errors.push(`${path} has invalid size.`);
      messages.push({ severity: "error", path: `${path}.size`, message: "Region size must include positive integer width, height, and depth." });
      return;
    }
    totalVolume += size.width * size.height * size.depth;
    const paletteSize = Object.keys(region.palette ?? {}).length;
    if (paletteSize === 0) {
      errors.push(`${path} palette is empty.`);
      messages.push({ severity: "error", path: `${path}.palette`, message: "Region palette must not be empty." });
    }
    if (paletteSize > limits.maxPaletteSize) {
      errors.push(`${path} palette has ${paletteSize} entries; limit is ${limits.maxPaletteSize}.`);
      messages.push({ severity: "error", path: `${path}.palette`, message: `Palette has ${paletteSize} entries; limit is ${limits.maxPaletteSize}.` });
    }
    region.blocks?.forEach((block, blockIndex) => {
      if (!Number.isInteger(block.x) || !Number.isInteger(block.y) || !Number.isInteger(block.z)) {
        errors.push(`${path}.blocks[${blockIndex}] has invalid coordinates.`);
        messages.push({ severity: "error", path: `${path}.blocks[${blockIndex}]`, message: "Block coordinates must be integers." });
        return;
      }
      if (block.x < 0 || block.x >= size.width || block.y < 0 || block.y >= size.height || block.z < 0 || block.z >= size.depth) {
        errors.push(`${path}.blocks[${blockIndex}] is outside region bounds.`);
        messages.push({ severity: "error", path: `${path}.blocks[${blockIndex}]`, message: "Block is outside the declared region bounds." });
      }
      if (!(block.state in region.palette)) {
        errors.push(`${path}.blocks[${blockIndex}] references missing palette key "${block.state}".`);
        messages.push({ severity: "error", path: `${path}.blocks[${blockIndex}].state`, message: `Block references missing palette key "${block.state}".` });
      }
    });
  });

  if (totalVolume > limits.maxTotalVolume) {
    errors.push(`Litematic volume ${totalVolume} exceeds limit ${limits.maxTotalVolume}.`);
    messages.push({ severity: "error", path: "regions", message: `Litematic volume ${totalVolume} exceeds limit ${limits.maxTotalVolume}.` });
  }
  if ((parsed.entitiesIgnored ?? 0) > 0) {
    warnings.push(`${parsed.entitiesIgnored} entities ignored.`);
    messages.push({ severity: "warning", path: "entities", message: `${parsed.entitiesIgnored} entities ignored.` });
  }
  if ((parsed.blockEntitiesIgnored ?? 0) > 0) {
    warnings.push(`${parsed.blockEntitiesIgnored} block entities ignored.`);
    messages.push({ severity: "warning", path: "blockEntities", message: `${parsed.blockEntitiesIgnored} block entities ignored.` });
  }

  return {
    valid: errors.length === 0,
    warnings,
    errors,
    report: createImportReport({
      id: parsed.name || "litematic-import",
      sourceType: "litematic",
      sourceFileName,
      messages: messages.length ? messages : [{ severity: "info", message: "Litematic import validated." }]
    })
  };
}

function isPositiveInteger(value: unknown): value is number {
  return Number.isInteger(value) && Number(value) > 0;
}
