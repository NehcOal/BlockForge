import { createSafeResourcePath } from "@/lib/voxel/exportUtils";
import type { VoxelModel } from "@/types/blueprint";

export const BLOCKFORGE_PACK_SCHEMA_VERSION = 1;
export const BLOCKFORGE_PACK_EXTENSION = ".blockforgepack.zip";
export const BLOCKFORGE_PACK_MANIFEST = "blockforge-pack.json";
export const BLOCKFORGE_PACK_BLUEPRINT_DIR = "blueprints/";
export const MAX_BLUEPRINTS_PER_PACK = 256;
const SAFE_RESOURCE_ID_PATTERN = /^[a-z0-9_-]+$/;

export type BlockForgePackManifestV1 = {
  schemaVersion: 1;
  packId: string;
  name: string;
  version: string;
  description?: string;
  author?: string;
  license?: string;
  minecraftVersion: "1.21.1";
  blockforgeVersion: string;
  tags?: string[];
  blueprints: BlockForgePackBlueprintEntryV1[];
};

export type BlockForgePackBlueprintEntryV1 = {
  id: string;
  name: string;
  path: string;
  description?: string;
  tags?: string[];
  previewImage?: string;
};

export type PackExportOptions = {
  packId: string;
  name: string;
  version: string;
  description?: string;
  author?: string;
  license?: string;
  tags?: string[];
  blockforgeVersion?: string;
};

export type ImportedBlueprintPack = {
  manifest: BlockForgePackManifestV1;
  blueprints: ImportedBlueprintPackBlueprint[];
  warnings: string[];
};

export type ImportedBlueprintPackBlueprint = {
  id: string;
  registryId: string;
  name: string;
  path: string;
  blueprint: unknown;
};

export function createPackManifestFromModels(
  models: VoxelModel[],
  options: PackExportOptions
): BlockForgePackManifestV1 {
  if (models.length === 0) {
    throw new Error("Blueprint pack requires at least one model.");
  }

  const packId = normalizePackId(options.packId);
  const seen = new Set<string>();
  const blueprints = models.map((model) => {
    const id = normalizePackId(createSafeResourcePath(model.id || model.name));
    if (seen.has(id)) {
      throw new Error(`Duplicate blueprint id in pack: ${id}`);
    }
    seen.add(id);

    return {
      id,
      name: model.name,
      path: `${BLOCKFORGE_PACK_BLUEPRINT_DIR}${id}.blueprint.json`,
      description: model.description
    };
  });

  return validatePackManifest({
    schemaVersion: BLOCKFORGE_PACK_SCHEMA_VERSION,
    packId,
    name: requiredText(options.name, "name"),
    version: requiredText(options.version, "version"),
    description: optionalText(options.description),
    author: optionalText(options.author),
    license: optionalText(options.license),
    minecraftVersion: "1.21.1",
    blockforgeVersion: options.blockforgeVersion ?? "1.4.0-alpha.1",
    tags: normalizeTags(options.tags),
    blueprints
  });
}

export function validatePackManifest(
  value: unknown
): BlockForgePackManifestV1 {
  if (!isRecord(value)) {
    throw new Error("Pack manifest must be a JSON object.");
  }

  if (value.schemaVersion !== BLOCKFORGE_PACK_SCHEMA_VERSION) {
    throw new Error("Unsupported pack schemaVersion.");
  }

  const blueprints = value.blueprints;
  if (!Array.isArray(blueprints) || blueprints.length === 0) {
    throw new Error("Pack manifest requires at least one blueprint.");
  }

  if (blueprints.length > MAX_BLUEPRINTS_PER_PACK) {
    throw new Error(`Pack exceeds ${MAX_BLUEPRINTS_PER_PACK} blueprints.`);
  }

  const packId = requireSafeResourceId(readString(value, "packId"), "packId");
  const seen = new Set<string>();
  const entries = blueprints.map((entry, index) => {
    if (!isRecord(entry)) {
      throw new Error(`Blueprint entry ${index} must be an object.`);
    }

    const id = requireSafeResourceId(readString(entry, "id"), `blueprints[${index}].id`);
    if (seen.has(id)) {
      throw new Error(`Duplicate blueprint id in pack: ${id}`);
    }
    seen.add(id);

    const path = validatePackBlueprintPath(readString(entry, "path"));

    return {
      id,
      name: readString(entry, "name"),
      path,
      description: optionalText(readOptionalString(entry, "description")),
      tags: normalizeTags(readOptionalStringArray(entry, "tags")),
      previewImage: optionalText(readOptionalString(entry, "previewImage"))
    };
  });

  return {
    schemaVersion: BLOCKFORGE_PACK_SCHEMA_VERSION,
    packId,
    name: readString(value, "name"),
    version: readString(value, "version"),
    description: optionalText(readOptionalString(value, "description")),
    author: optionalText(readOptionalString(value, "author")),
    license: optionalText(readOptionalString(value, "license")),
    minecraftVersion: readMinecraftVersion(value),
    blockforgeVersion: readString(value, "blockforgeVersion"),
    tags: normalizeTags(readOptionalStringArray(value, "tags")),
    blueprints: entries
  };
}

export function validatePackBlueprintPath(path: string): string {
  const normalized = path.trim().replaceAll("\\", "/");

  if (normalized.length === 0) {
    throw new Error("Blueprint path must not be blank.");
  }
  if (path.includes("\\")) {
    throw new Error("Blueprint path must use forward slashes.");
  }
  if (normalized.startsWith("/") || /^[a-zA-Z]:\//.test(normalized)) {
    throw new Error("Blueprint path must be relative.");
  }
  if (!normalized.startsWith(BLOCKFORGE_PACK_BLUEPRINT_DIR)) {
    throw new Error("Blueprint path must be inside blueprints/.");
  }
  if (normalized.split("/").includes("..")) {
    throw new Error("Blueprint path must not contain path traversal.");
  }
  if (!normalized.endsWith(".blueprint.json") && !normalized.endsWith(".json")) {
    throw new Error("Blueprint path must point to a JSON blueprint.");
  }

  return normalized;
}

export function createPackRegistryId(packId: string, blueprintId: string): string {
  return `${requireSafeResourceId(packId, "packId")}/${requireSafeResourceId(blueprintId, "blueprintId")}`;
}

export function normalizePackId(value: string): string {
  const normalized = value
    .trim()
    .toLowerCase()
    .replace(/\s+/g, "_")
    .replace(/[^a-z0-9_-]/g, "")
    .replace(/_+/g, "_")
    .replace(/-+/g, "-")
    .replace(/^[_-]+|[_-]+$/g, "");

  if (!normalized) {
    throw new Error("Pack id must contain a-z, 0-9, underscore, or hyphen.");
  }

  return normalized;
}

export function requireSafeResourceId(value: string, field: string): string {
  const trimmed = value.trim();
  if (!SAFE_RESOURCE_ID_PATTERN.test(trimmed)) {
    throw new Error(`${field} must match ${SAFE_RESOURCE_ID_PATTERN.source}.`);
  }
  return trimmed;
}

export function isBlueprintJson(value: unknown): boolean {
  if (!isRecord(value)) {
    return false;
  }

  const schemaVersion = value.schemaVersion;
  return (schemaVersion === 1 || schemaVersion === 2)
    && typeof value.id === "string"
    && isRecord(value.size)
    && isRecord(value.palette)
    && Array.isArray(value.blocks);
}

function readMinecraftVersion(value: Record<string, unknown>): "1.21.1" {
  const minecraftVersion = readString(value, "minecraftVersion");
  if (minecraftVersion !== "1.21.1") {
    throw new Error("Blueprint pack minecraftVersion must be 1.21.1.");
  }
  return minecraftVersion;
}

function readString(value: Record<string, unknown>, field: string): string {
  const fieldValue = value[field];
  if (typeof fieldValue !== "string" || fieldValue.trim().length === 0) {
    throw new Error(`Pack manifest field ${field} must be a non-empty string.`);
  }
  return fieldValue.trim();
}

function readOptionalString(
  value: Record<string, unknown>,
  field: string
): string | undefined {
  const fieldValue = value[field];
  if (fieldValue === undefined || fieldValue === null) {
    return undefined;
  }
  if (typeof fieldValue !== "string") {
    throw new Error(`Pack manifest field ${field} must be a string.`);
  }
  return fieldValue;
}

function readOptionalStringArray(
  value: Record<string, unknown>,
  field: string
): string[] | undefined {
  const fieldValue = value[field];
  if (fieldValue === undefined || fieldValue === null) {
    return undefined;
  }
  if (!Array.isArray(fieldValue) || fieldValue.some((item) => typeof item !== "string")) {
    throw new Error(`Pack manifest field ${field} must be a string array.`);
  }
  return fieldValue;
}

function normalizeTags(tags: string[] | undefined): string[] | undefined {
  if (!tags || tags.length === 0) {
    return undefined;
  }

  const normalized = tags
    .map((tag) => tag.trim())
    .filter(Boolean);

  return normalized.length === 0 ? undefined : Array.from(new Set(normalized));
}

function optionalText(value: string | undefined): string | undefined {
  const text = value?.trim();
  return text ? text : undefined;
}

function requiredText(value: string, field: string): string {
  const text = value.trim();
  if (!text) {
    throw new Error(`Pack ${field} is required.`);
  }
  return text;
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}
