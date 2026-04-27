import type { BlockForgeBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";

export const SPONGE_SCHEMATIC_VERSION = 3;
export const DEFAULT_SCHEMATIC_DATA_VERSION = 3955;
export const MAX_SCHEMATIC_VOLUME = 1_000_000;
export const MAX_SCHEMATIC_FILE_BYTES = 10 * 1024 * 1024;

export type SpongeSchematicV3 = {
  version: 3;
  dataVersion: number;
  width: number;
  height: number;
  length: number;
  offset: [number, number, number];
  metadata?: {
    name?: string;
    author?: string;
    date?: number;
    requiredMods?: string[];
  };
  palette: Record<string, number>;
  data: number[];
  warnings: string[];
};

export type SpongeSchematicExportOptions = {
  name?: string;
  author?: string;
  dataVersion?: number;
};

export type ImportedSpongeSchematic = {
  schematic: SpongeSchematicV3;
  blueprint: BlockForgeBlueprintV2;
  warnings: string[];
};

export function validateSchematicSize(width: number, height: number, length: number): void {
  if (width <= 0 || height <= 0 || length <= 0) {
    throw new Error("Schematic dimensions must be positive.");
  }
  if (width * height * length > MAX_SCHEMATIC_VOLUME) {
    throw new Error(`Schematic volume exceeds ${MAX_SCHEMATIC_VOLUME} blocks.`);
  }
}

export function schematicIndex(x: number, y: number, z: number, width: number, length: number): number {
  return x + z * width + y * width * length;
}
