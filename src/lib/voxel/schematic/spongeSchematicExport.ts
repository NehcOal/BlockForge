import { gzip } from "@/lib/nbt/gzip";
import { encodeVarInts } from "@/lib/nbt/varint";
import { type NbtValue, writeNamedNbt } from "@/lib/nbt/writer";
import type { BlockForgeBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";
import { blockStateToSpongeString } from "@/lib/voxel/schematic/blockStateString";
import {
  DEFAULT_SCHEMATIC_DATA_VERSION,
  schematicIndex,
  SPONGE_SCHEMATIC_VERSION,
  type SpongeSchematicExportOptions,
  type SpongeSchematicV3,
  validateSchematicSize
} from "@/lib/voxel/schematic/spongeSchematic";

export function blueprintV2ToSpongeSchematic(
  blueprint: BlockForgeBlueprintV2,
  options: SpongeSchematicExportOptions = {}
): SpongeSchematicV3 {
  const { width, height, depth } = blueprint.size;
  validateSchematicSize(width, height, depth);

  const paletteStrings = Array.from(
    new Set(blueprint.blocks.map((block) => blockStateToSpongeString(blueprint.palette[block.state])))
  ).sort();
  if (!paletteStrings.includes("minecraft:air")) {
    paletteStrings.unshift("minecraft:air");
  }
  const palette = Object.fromEntries(paletteStrings.map((state, index) => [state, index]));
  const data = Array.from({ length: width * height * depth }, () => palette["minecraft:air"]);

  for (const block of blueprint.blocks) {
    const entry = blueprint.palette[block.state];
    if (!entry) {
      continue;
    }
    data[schematicIndex(block.x, block.y, block.z, width, depth)] = palette[blockStateToSpongeString(entry)];
  }

  return {
    version: SPONGE_SCHEMATIC_VERSION,
    dataVersion: options.dataVersion ?? DEFAULT_SCHEMATIC_DATA_VERSION,
    width,
    height,
    length: depth,
    offset: [0, 0, 0],
    metadata: {
      name: options.name ?? blueprint.name,
      author: options.author,
      date: Date.now()
    },
    palette,
    data,
    warnings: []
  };
}

export async function exportSpongeSchematicBlob(
  blueprint: BlockForgeBlueprintV2,
  options: SpongeSchematicExportOptions = {}
): Promise<Blob> {
  const schematic = blueprintV2ToSpongeSchematic(blueprint, options);
  const nbt = writeNamedNbt("Schematic", schematicToNbt(schematic));
  const bytes = await gzip(nbt);
  const arrayBuffer = bytes.buffer.slice(bytes.byteOffset, bytes.byteOffset + bytes.byteLength) as ArrayBuffer;
  const blob = new Blob([arrayBuffer], { type: "application/octet-stream" });
  if (typeof blob.arrayBuffer !== "function") {
    Object.defineProperty(blob, "arrayBuffer", {
      value: async () => arrayBuffer
    });
  }
  return blob;
}

function schematicToNbt(schematic: SpongeSchematicV3): NbtValue {
  const paletteEntries = Object.fromEntries(
    Object.entries(schematic.palette).map(([state, index]) => [state, { type: "int", value: index } satisfies NbtValue])
  );

  const metadata: Record<string, NbtValue> = {};
  if (schematic.metadata?.name) metadata.Name = { type: "string", value: schematic.metadata.name };
  if (schematic.metadata?.author) metadata.Author = { type: "string", value: schematic.metadata.author };
  if (schematic.metadata?.date) metadata.Date = { type: "long", value: schematic.metadata.date };

  return {
    type: "compound",
    value: {
      Version: { type: "int", value: schematic.version },
      DataVersion: { type: "int", value: schematic.dataVersion },
      Width: { type: "short", value: schematic.width },
      Height: { type: "short", value: schematic.height },
      Length: { type: "short", value: schematic.length },
      Offset: {
        type: "intArray",
        value: schematic.offset
      },
      Metadata: { type: "compound", value: metadata },
      Blocks: {
        type: "compound",
        value: {
          Palette: { type: "compound", value: paletteEntries },
          Data: { type: "byteArray", value: encodeVarInts(schematic.data) }
        }
      }
    }
  };
}
