import { ungzip } from "@/lib/nbt/gzip";
import { readNamedNbt } from "@/lib/nbt/reader";
import { decodeVarInts } from "@/lib/nbt/varint";
import type { NbtValue } from "@/lib/nbt/writer";
import type { BlockForgeBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";
import { spongeStringToBlockState } from "@/lib/voxel/schematic/blockStateString";
import {
  MAX_SCHEMATIC_FILE_BYTES,
  schematicIndex,
  SPONGE_SCHEMATIC_VERSION,
  type ImportedSpongeSchematic,
  type SpongeSchematicV3,
  validateSchematicSize
} from "@/lib/voxel/schematic/spongeSchematic";

export async function importSpongeSchematicBlob(file: Blob): Promise<ImportedSpongeSchematic> {
  let bytes: Uint8Array;
  try {
    bytes = await ungzip(new Uint8Array(await blobArrayBuffer(file)), MAX_SCHEMATIC_FILE_BYTES);
  } catch {
    throw new Error("Invalid .schem gzip data.");
  }

  const named = readNamedNbt(bytes);
  if (named.name !== "Schematic" || named.value.type !== "compound") {
    throw new Error("Sponge schematic root must be a named Schematic compound.");
  }

  const schematic = parseSchematic(named.value.value);
  return {
    schematic,
    blueprint: spongeSchematicToBlueprintV2(schematic),
    warnings: schematic.warnings
  };
}

async function blobArrayBuffer(file: Blob): Promise<ArrayBuffer> {
  if (typeof file.arrayBuffer === "function") {
    return file.arrayBuffer();
  }
  return new Response(file).arrayBuffer();
}

export function spongeSchematicToBlueprintV2(
  schematic: SpongeSchematicV3,
  options: { blueprintId?: string; name?: string } = {}
): BlockForgeBlueprintV2 {
  const paletteByIndex = Object.fromEntries(
    Object.entries(schematic.palette).map(([state, index]) => [index, state])
  );
  const palette: BlockForgeBlueprintV2["palette"] = {};
  const paletteKeyByState = new Map<string, string>();
  for (const [state, index] of Object.entries(schematic.palette)) {
    if (state === "minecraft:air") {
      continue;
    }
    const key = `s${index}`;
    palette[key] = spongeStringToBlockState(state);
    paletteKeyByState.set(state, key);
  }

  const blocks: BlockForgeBlueprintV2["blocks"] = [];
  for (let y = 0; y < schematic.height; y++) {
    for (let z = 0; z < schematic.length; z++) {
      for (let x = 0; x < schematic.width; x++) {
        const state = paletteByIndex[schematic.data[schematicIndex(x, y, z, schematic.width, schematic.length)]];
        if (!state || state === "minecraft:air") {
          continue;
        }
        const key = paletteKeyByState.get(state);
        if (key) {
          blocks.push({ x, y, z, state: key });
        }
      }
    }
  }

  return {
    schemaVersion: 2,
    id: options.blueprintId ?? "imported_schematic",
    name: options.name ?? schematic.metadata?.name ?? "Imported Schematic",
    description: "Imported from Sponge Schematic v3.",
    minecraftVersion: "1.21.1",
    generator: "BlockForge",
    size: {
      width: schematic.width,
      height: schematic.height,
      depth: schematic.length
    },
    origin: { x: 0, y: 0, z: 0 },
    palette,
    blocks
  };
}

function parseSchematic(root: Record<string, NbtValue>): SpongeSchematicV3 {
  const version = intValue(root.Version, "Version");
  if (version !== SPONGE_SCHEMATIC_VERSION) {
    throw new Error(`Unsupported Sponge schematic Version: ${version}`);
  }
  const width = intValue(root.Width, "Width");
  const height = intValue(root.Height, "Height");
  const length = intValue(root.Length, "Length");
  validateSchematicSize(width, height, length);

  const blocks = compoundValue(root.Blocks, "Blocks");
  const paletteRoot = compoundValue(blocks.Palette, "Blocks.Palette");
  const palette = Object.fromEntries(
    Object.entries(paletteRoot).map(([state, value]) => [state, intValue(value, `Blocks.Palette.${state}`)])
  );
  const data = decodeVarInts(byteArrayValue(blocks.Data, "Blocks.Data"));
  if (data.length !== width * height * length) {
    throw new Error("Blocks.Data length does not match schematic volume.");
  }
  validatePaletteData(palette, data);

  const warnings: string[] = [];
  if (blocks.BlockEntities) warnings.push("BlockEntities are ignored in Web schematic import Alpha.");
  if (root.Entities) warnings.push("Entities are ignored in Web schematic import Alpha.");
  if (root.Biomes) warnings.push("Biomes are ignored in Web schematic import Alpha.");

  return {
    version: SPONGE_SCHEMATIC_VERSION,
    dataVersion: intValue(root.DataVersion, "DataVersion"),
    width,
    height,
    length,
    offset: intArrayValue(root.Offset, "Offset", 3) as [number, number, number],
    metadata: readMetadata(root.Metadata),
    palette,
    data,
    warnings
  };
}

function readMetadata(value: NbtValue | undefined): SpongeSchematicV3["metadata"] {
  if (!value || value.type !== "compound") return undefined;
  return {
    name: optionalString(value.value.Name),
    author: optionalString(value.value.Author),
    date: optionalNumber(value.value.Date)
  };
}

function compoundValue(value: NbtValue | undefined, field: string): Record<string, NbtValue> {
  if (!value || value.type !== "compound") throw new Error(`${field} must be a compound.`);
  return value.value;
}

function byteArrayValue(value: NbtValue | undefined, field: string): Uint8Array {
  if (!value || value.type !== "byteArray") throw new Error(`${field} must be a byte array.`);
  return value.value;
}

function intArrayValue(value: NbtValue | undefined, field: string, length: number): number[] {
  if (!value || value.type !== "intArray" || value.value.length !== length) {
    throw new Error(`${field} must be an int array with ${length} entries.`);
  }
  return value.value;
}

function intValue(value: NbtValue | undefined, field: string): number {
  if (!value || (value.type !== "int" && value.type !== "short" && value.type !== "byte")) {
    throw new Error(`${field} must be an integer.`);
  }
  return value.value;
}

function optionalString(value: NbtValue | undefined): string | undefined {
  return value?.type === "string" ? value.value : undefined;
}

function optionalNumber(value: NbtValue | undefined): number | undefined {
  return value?.type === "long" || value?.type === "int" ? Number(value.value) : undefined;
}

function validatePaletteData(palette: Record<string, number>, data: number[]): void {
  const indexes = new Set(Object.values(palette));
  const missing = data.find((index) => !indexes.has(index));
  if (missing !== undefined) {
    throw new Error(`Blocks.Data references missing palette index: ${missing}.`);
  }
}
