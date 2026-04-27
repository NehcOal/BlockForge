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

  it("imports common blockstate properties and partial-content warnings", async () => {
    const nbt = writeNamedNbt("Schematic", schematicNbt({
      Palette: {
        type: "compound",
        value: {
          "minecraft:air": { type: "int", value: 0 },
          "minecraft:oak_door[facing=north,half=lower,hinge=left,open=false,powered=false]": { type: "int", value: 1 },
          "minecraft:oak_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]": { type: "int", value: 2 }
        }
      },
      Data: { type: "byteArray", value: encodeVarInts([1, 2]) },
      BlockEntities: { type: "list", itemType: 10, value: [] }
    }, {
      Width: { type: "short", value: 2 },
      Entities: { type: "list", itemType: 10, value: [] },
      Biomes: { type: "byteArray", value: new Uint8Array([0, 0]) }
    }));

    const compressed = await gzip(nbt);
    const arrayBuffer = compressed.buffer.slice(compressed.byteOffset, compressed.byteOffset + compressed.byteLength) as ArrayBuffer;
    const file = new Blob([]);
    Object.defineProperty(file, "arrayBuffer", {
      value: async () => arrayBuffer
    });

    const imported = await importSpongeSchematicBlob(file);

    expect(imported.blueprint.size).toEqual({ width: 2, height: 1, depth: 1 });
    expect(imported.blueprint.blocks).toHaveLength(2);
    expect(imported.blueprint.palette.s1).toEqual({
      name: "minecraft:oak_door",
      properties: {
        facing: "north",
        half: "lower",
        hinge: "left",
        open: "false",
        powered: "false"
      }
    });
    expect(imported.blueprint.palette.s2).toEqual({
      name: "minecraft:oak_stairs",
      properties: {
        facing: "east",
        half: "bottom",
        shape: "straight",
        waterlogged: "false"
      }
    });
    expect(imported.warnings).toEqual([
      "BlockEntities are ignored in Web schematic import Alpha.",
      "Entities are ignored in Web schematic import Alpha.",
      "Biomes are ignored in Web schematic import Alpha."
    ]);
  });
});

function schematicNbt(blocks: Record<string, NbtValue>, overrides: Record<string, NbtValue> = {}): NbtValue {
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
      },
      ...overrides
    }
  };
}
