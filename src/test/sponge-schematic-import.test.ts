import { describe, expect, it } from "vitest";
import { gzip } from "@/lib/nbt/gzip";
import { encodeVarInts } from "@/lib/nbt/varint";
import { writeNamedNbt, type NbtValue } from "@/lib/nbt/writer";
import { exportSpongeSchematicBlob } from "@/lib/voxel/schematic/spongeSchematicExport";
import { importSpongeSchematicBlob } from "@/lib/voxel/schematic/spongeSchematicImport";
import { voxelModelToBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";
import { getPresetById } from "@/lib/voxel";

describe("Sponge schematic import", () => {
  it("imports an exported schematic back into Blueprint v2", async () => {
    const blueprint = voxelModelToBlueprintV2(getPresetById("small-cottage"));
    const imported = await importSpongeSchematicBlob(await exportSpongeSchematicBlob(blueprint));

    expect(imported.schematic.version).toBe(3);
    expect(imported.blueprint.size).toEqual(blueprint.size);
    expect(imported.blueprint.blocks).toHaveLength(blueprint.blocks.length);
  });

  it("rejects invalid gzip data", async () => {
    await expect(importSpongeSchematicBlob(new Blob([new Uint8Array([1, 2, 3])]))).rejects.toThrow("Invalid .schem gzip data");
  });

  it("rejects Blocks.Data palette indexes missing from the palette", async () => {
    const nbt = writeNamedNbt("Schematic", schematicNbt({
      Palette: {
        type: "compound",
        value: {
          "minecraft:air": { type: "int", value: 0 }
        }
      },
      Data: { type: "byteArray", value: encodeVarInts([1]) }
    }));

    const compressed = await gzip(nbt);
    const arrayBuffer = compressed.buffer.slice(compressed.byteOffset, compressed.byteOffset + compressed.byteLength) as ArrayBuffer;
    const file = new Blob([]);
    Object.defineProperty(file, "arrayBuffer", {
      value: async () => arrayBuffer
    });
    await expect(importSpongeSchematicBlob(file)).rejects.toThrow(
      "Blocks.Data references missing palette index: 1."
    );
  });
});

function schematicNbt(blocks: Record<string, NbtValue>): NbtValue {
  return {
    type: "compound",
    value: {
      Version: { type: "int", value: 3 },
      DataVersion: { type: "int", value: 3955 },
      Width: { type: "short", value: 1 },
      Height: { type: "short", value: 1 },
      Length: { type: "short", value: 1 },
      Offset: { type: "intArray", value: [0, 0, 0] },
      Metadata: { type: "compound", value: {} },
      Blocks: {
        type: "compound",
        value: blocks
      }
    }
  };
}
