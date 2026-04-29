import { createImportReport, type ImportReport } from "@/lib/import/importReport";
import { ungzip } from "@/lib/nbt/gzip";
import { readNamedNbt } from "@/lib/nbt/reader";
import type { NbtValue } from "@/lib/nbt/writer";
import { parseLitematicBlockState } from "@/lib/voxel/litematic/litematicBlockState";
import { litematicToBlueprintV2 } from "@/lib/voxel/litematic/litematicToBlueprint";
import {
  defaultLitematicImportLimits,
  type ImportedLitematic,
  type LitematicImportLimits,
  type ParsedLitematic
} from "@/lib/voxel/litematic/litematicTypes";
import { validateParsedLitematic } from "@/lib/voxel/litematic/litematicValidation";
import { validateBlueprintJson } from "@/lib/voxel/blueprintValidation";

export type LitematicImportResult = {
  imported?: ImportedLitematic;
  report: ImportReport;
};

export async function importLitematicBuffer(
  buffer: ArrayBuffer,
  sourceFileName = "import.litematic",
  limits: LitematicImportLimits = defaultLitematicImportLimits
): Promise<LitematicImportResult> {
  if (buffer.byteLength > limits.maxFileSizeBytes) {
    return errorReport(sourceFileName, `Litematic file exceeds ${limits.maxFileSizeBytes} byte limit.`);
  }
  try {
    const parsed = await parseLitematicBuffer(buffer, limits);
    const validation = validateParsedLitematic(parsed, limits, sourceFileName);
    if (!validation.valid) {
      return { report: validation.report };
    }
    const converted = litematicToBlueprintV2(parsed);
    const blueprintReport = validateBlueprintJson(converted.blueprint);
    const blueprintErrors = blueprintReport.issues.filter((issue) => issue.severity === "error");
    if (blueprintErrors.length > 0) {
      return {
        report: createImportReport({
          id: parsed.name || sourceFileName,
          sourceType: "litematic",
          sourceFileName,
          messages: [
            ...validation.report.messages,
            ...blueprintErrors.map((issue) => ({
              severity: "error" as const,
              path: issue.field,
              message: `Converted Blueprint is invalid: ${issue.message}`,
              suggestion: issue.suggestion
            }))
          ]
        })
      };
    }
    const messages = [
      ...validation.report.messages,
      ...validation.warnings.map((message) => ({ severity: "warning" as const, message })),
      ...converted.warnings.map((message) => ({ severity: "warning" as const, message })),
      { severity: "info" as const, message: `Imported ${converted.blueprint.blocks.length} Litematic blocks.` }
    ];
    return {
      imported: {
        sourceFileName,
        parsed,
        blueprints: [converted.blueprint],
        warnings: [...validation.warnings, ...converted.warnings]
      },
      report: createImportReport({
        id: parsed.name || sourceFileName,
        sourceType: "litematic",
        sourceFileName,
        messages
      })
    };
  } catch (error) {
    return errorReport(sourceFileName, friendlyParseError(error));
  }
}

async function parseLitematicBuffer(
  buffer: ArrayBuffer,
  limits: LitematicImportLimits
): Promise<ParsedLitematic> {
  const bytes = new Uint8Array(buffer);
  if (looksLikeJson(bytes)) {
    const text = new TextDecoder().decode(buffer).trim();
    if (!text) {
      throw new Error("Litematic file is empty or not readable.");
    }
    return normalizeSyntheticLitematic(JSON.parse(text));
  }
  let nbtBytes: Uint8Array;
  try {
    nbtBytes = await ungzip(bytes, limits.maxFileSizeBytes * 8);
  } catch {
    throw new Error("Invalid .litematic gzip data.");
  }
  const named = readNamedNbt(nbtBytes);
  if (named.value.type !== "compound") {
    throw new Error("Litematic root must be a compound.");
  }
  return parseLitematicNbt(named.value.value);
}

function parseLitematicNbt(root: Record<string, NbtValue>): ParsedLitematic {
  const metadata = optionalCompound(root.Metadata);
  const regionsRoot = compoundValue(root.Regions, "Regions");
  const regionEntries = Object.entries(regionsRoot);
  return {
    version: intValue(root.Version, "Version"),
    name: optionalString(metadata?.Name) ?? "Imported Litematic",
    author: optionalString(metadata?.Author),
    description: optionalString(metadata?.Description),
    entitiesIgnored: countIgnored(root.Entities),
    blockEntitiesIgnored: 0,
    regions: regionEntries.map(([name, value]) => parseLitematicRegionNbt(name, value))
  };
}

function parseLitematicRegionNbt(name: string, value: NbtValue): ParsedLitematic["regions"][number] {
  const root = compoundValue(value, `Regions.${name}`);
  const size = vec3Value(root.Size, `Regions.${name}.Size`);
  const position = vec3Value(root.Position, `Regions.${name}.Position`, { x: 0, y: 0, z: 0 });
  const dimensions = {
    width: Math.abs(size.x),
    height: Math.abs(size.y),
    depth: Math.abs(size.z)
  };
  const palette = parseBlockStatePalette(listValue(root.BlockStatePalette, `Regions.${name}.BlockStatePalette`));
  const blockStates = longArrayValue(root.BlockStates, `Regions.${name}.BlockStates`);
  const paletteKeys = Object.keys(palette);
  const bitsPerBlock = Math.max(2, Math.ceil(Math.log2(Math.max(1, paletteKeys.length))));
  const volume = dimensions.width * dimensions.height * dimensions.depth;
  const blocks: ParsedLitematic["regions"][number]["blocks"] = [];
  for (let index = 0; index < volume; index++) {
    const paletteIndex = readPackedPaletteIndex(blockStates, index, bitsPerBlock);
    const state = paletteKeys[paletteIndex];
    if (!state || palette[state]?.name === "minecraft:air") {
      continue;
    }
    const x = index % dimensions.width;
    const z = Math.floor(index / dimensions.width) % dimensions.depth;
    const y = Math.floor(index / (dimensions.width * dimensions.depth));
    blocks.push({ x, y, z, state });
  }
  return {
    name,
    position,
    size: dimensions,
    palette,
    blocks
  };
}

function parseBlockStatePalette(values: NbtValue[]): ParsedLitematic["regions"][number]["palette"] {
  const palette: ParsedLitematic["regions"][number]["palette"] = {};
  values.forEach((value, index) => {
    const compound = compoundValue(value, `BlockStatePalette[${index}]`);
    const name = stringValue(compound.Name, `BlockStatePalette[${index}].Name`);
    const properties = optionalStringProperties(compound.Properties);
    palette[`s${index}`] = { name, properties };
  });
  return palette;
}

function readPackedPaletteIndex(values: Array<number | bigint>, index: number, bitsPerBlock: number): number {
  const bitIndex = BigInt(index * bitsPerBlock);
  const longIndex = Number(bitIndex / BigInt(64));
  const bitOffset = Number(bitIndex % BigInt(64));
  const mask = (BigInt(1) << BigInt(bitsPerBlock)) - BigInt(1);
  const first = unsignedLong(values[longIndex] ?? BigInt(0));
  let value = (first >> BigInt(bitOffset)) & mask;
  const bitsInFirst = 64 - bitOffset;
  if (bitsInFirst < bitsPerBlock) {
    const second = unsignedLong(values[longIndex + 1] ?? BigInt(0));
    value |= (second & ((BigInt(1) << BigInt(bitsPerBlock - bitsInFirst)) - BigInt(1))) << BigInt(bitsInFirst);
  }
  return Number(value);
}

function unsignedLong(value: number | bigint): bigint {
  const big = typeof value === "bigint" ? value : BigInt(Math.trunc(value));
  return big < 0 ? big + (BigInt(1) << BigInt(64)) : big;
}

export function normalizeSyntheticLitematic(value: unknown): ParsedLitematic {
  if (!isRecord(value)) {
    throw new Error("Invalid NBT or JSON root.");
  }
  const regionsInput = Array.isArray(value.regions) ? value.regions : [];
  return {
    version: Number(value.version ?? value.Version ?? 1),
    name: String(value.name ?? value.Name ?? "Imported Litematic"),
    author: typeof value.author === "string" ? value.author : undefined,
    description: typeof value.description === "string" ? value.description : undefined,
    entitiesIgnored: Number(value.entitiesIgnored ?? 0),
    blockEntitiesIgnored: Number(value.blockEntitiesIgnored ?? 0),
    regions: regionsInput.map((region, index) => normalizeRegion(region, index))
  };
}

function normalizeRegion(value: unknown, index: number): ParsedLitematic["regions"][number] {
  if (!isRecord(value)) {
    throw new Error(`Invalid region at index ${index}.`);
  }
  const paletteInput = isRecord(value.palette) ? value.palette : {};
  const palette = Object.fromEntries(
    Object.entries(paletteInput).map(([key, state]) => [
      key,
      typeof state === "string" ? parseLitematicBlockState(state) : state
    ])
  ) as ParsedLitematic["regions"][number]["palette"];
  return {
    name: String(value.name ?? `region-${index + 1}`),
    position: normalizePosition(value.position),
    size: normalizeSize(value.size),
    palette,
    blocks: Array.isArray(value.blocks)
      ? value.blocks.map((block) => normalizeBlock(block))
      : []
  };
}

function normalizePosition(value: unknown): { x: number; y: number; z: number } | undefined {
  if (!isRecord(value)) return undefined;
  return {
    x: Number(value.x ?? 0),
    y: Number(value.y ?? 0),
    z: Number(value.z ?? 0)
  };
}

function normalizeSize(value: unknown): { width: number; height: number; depth: number } {
  if (!isRecord(value)) {
    return { width: 0, height: 0, depth: 0 };
  }
  return {
    width: Number(value.width ?? value.x ?? 0),
    height: Number(value.height ?? value.y ?? 0),
    depth: Number(value.depth ?? value.z ?? 0)
  };
}

function normalizeBlock(value: unknown): ParsedLitematic["regions"][number]["blocks"][number] {
  if (!isRecord(value)) {
    return { x: 0, y: 0, z: 0, state: "" };
  }
  return {
    x: Number(value.x ?? 0),
    y: Number(value.y ?? 0),
    z: Number(value.z ?? 0),
    state: String(value.state ?? value.block ?? "")
  };
}

function errorReport(sourceFileName: string, message: string): LitematicImportResult {
  return {
    report: createImportReport({
      id: sourceFileName,
      sourceType: "litematic",
      sourceFileName,
      messages: [{ severity: "error", message, suggestion: "Import a valid .litematic file within the documented alpha limits." }]
    })
  };
}

function friendlyParseError(error: unknown): string {
  const message = error instanceof Error ? error.message : String(error);
  if (message.includes("JSON")) {
    return "Invalid NBT or JSON payload. Binary .litematic parsing is experimental and malformed files are rejected safely.";
  }
  return message;
}

function looksLikeJson(bytes: Uint8Array): boolean {
  for (const byte of bytes) {
    if (byte === 32 || byte === 9 || byte === 10 || byte === 13) continue;
    return byte === 123;
  }
  return false;
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}

function compoundValue(value: NbtValue | undefined, field: string): Record<string, NbtValue> {
  if (!value || value.type !== "compound") throw new Error(`${field} must be a compound.`);
  return value.value;
}

function optionalCompound(value: NbtValue | undefined): Record<string, NbtValue> | undefined {
  return value?.type === "compound" ? value.value : undefined;
}

function listValue(value: NbtValue | undefined, field: string): NbtValue[] {
  if (!value || value.type !== "list") throw new Error(`${field} must be a list.`);
  return value.value;
}

function longArrayValue(value: NbtValue | undefined, field: string): Array<number | bigint> {
  if (!value || value.type !== "longArray") throw new Error(`${field} must be a long array.`);
  return value.value;
}

function intValue(value: NbtValue | undefined, field: string): number {
  if (!value || (value.type !== "int" && value.type !== "short" && value.type !== "byte")) {
    throw new Error(`${field} must be an integer.`);
  }
  return Number(value.value);
}

function stringValue(value: NbtValue | undefined, field: string): string {
  if (!value || value.type !== "string") throw new Error(`${field} must be a string.`);
  return value.value;
}

function optionalString(value: NbtValue | undefined): string | undefined {
  return value?.type === "string" ? value.value : undefined;
}

function optionalStringProperties(value: NbtValue | undefined): Record<string, string> | undefined {
  const compound = optionalCompound(value);
  if (!compound) return undefined;
  const entries = Object.entries(compound)
    .filter((entry): entry is [string, Extract<NbtValue, { type: "string" }>] => entry[1].type === "string")
    .map(([key, child]) => [key, child.value]);
  return entries.length ? Object.fromEntries(entries) : undefined;
}

function vec3Value(value: NbtValue | undefined, field: string, fallback?: { x: number; y: number; z: number }): { x: number; y: number; z: number } {
  if (!value && fallback) return fallback;
  const compound = compoundValue(value, field);
  return {
    x: intValue(compound.x ?? compound.X, `${field}.x`),
    y: intValue(compound.y ?? compound.Y, `${field}.y`),
    z: intValue(compound.z ?? compound.Z, `${field}.z`)
  };
}

function countIgnored(value: NbtValue | undefined): number {
  return value?.type === "list" ? value.value.length : 0;
}
