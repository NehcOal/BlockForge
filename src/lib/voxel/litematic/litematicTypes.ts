import type { BlockForgeBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";

export type LitematicBlockState = {
  name: string;
  properties?: Record<string, string>;
};

export type LitematicRegionBlock = {
  x: number;
  y: number;
  z: number;
  state: string;
};

export type LitematicRegion = {
  name: string;
  position?: { x: number; y: number; z: number };
  size: { width: number; height: number; depth: number };
  palette: Record<string, LitematicBlockState>;
  blocks: LitematicRegionBlock[];
};

export type ParsedLitematic = {
  version: number;
  name: string;
  author?: string;
  description?: string;
  regions: LitematicRegion[];
  entitiesIgnored?: number;
  blockEntitiesIgnored?: number;
};

export type LitematicImportLimits = {
  maxFileSizeBytes: number;
  maxRegionCount: number;
  maxTotalVolume: number;
  maxPaletteSize: number;
};

export type ImportedLitematic = {
  sourceFileName?: string;
  parsed: ParsedLitematic;
  blueprints: BlockForgeBlueprintV2[];
  warnings: string[];
};

export const defaultLitematicImportLimits: LitematicImportLimits = {
  maxFileSizeBytes: 10 * 1024 * 1024,
  maxRegionCount: 32,
  maxTotalVolume: 1_000_000,
  maxPaletteSize: 4096
};
