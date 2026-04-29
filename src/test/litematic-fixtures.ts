import type { ParsedLitematic } from "@/lib/voxel/litematic/litematicTypes";
import { gzip } from "@/lib/nbt/gzip";
import { writeNamedNbt, type NbtValue } from "@/lib/nbt/writer";

export function createMinimalLitematic(): ParsedLitematic {
  return {
    version: 6,
    name: "Minimal Litematic",
    description: "Synthetic fixture for alpha import tests.",
    regions: [
      {
        name: "main",
        size: { width: 2, height: 2, depth: 2 },
        palette: {
          stone: { name: "minecraft:stone" },
          glass: { name: "minecraft:glass" }
        },
        blocks: [
          { x: 0, y: 0, z: 0, state: "stone" },
          { x: 1, y: 0, z: 0, state: "glass" }
        ]
      }
    ]
  };
}

export function encodeLitematicFixture(value: unknown): ArrayBuffer {
  return new TextEncoder().encode(JSON.stringify(value)).buffer;
}

export async function encodeRealLitematicFixture(): Promise<ArrayBuffer> {
  const root: NbtValue = {
    type: "compound",
    value: {
      Version: { type: "int", value: 6 },
      Metadata: {
        type: "compound",
        value: {
          Name: { type: "string", value: "Real Minimal Litematic" },
          Author: { type: "string", value: "Vitest" },
          Description: { type: "string", value: "Gzipped NBT fixture." }
        }
      },
      Regions: {
        type: "compound",
        value: {
          main: {
            type: "compound",
            value: {
              Position: vec3(0, 0, 0),
              Size: vec3(2, 2, 2),
              BlockStatePalette: {
                type: "list",
                itemType: 10,
                value: [
                  blockState("minecraft:air"),
                  blockState("minecraft:stone"),
                  blockState("minecraft:glass")
                ]
              },
              BlockStates: {
                type: "longArray",
                value: [packPaletteIndexes([1, 2, 0, 0, 0, 0, 0, 0], 2)]
              }
            }
          }
        }
      }
    }
  };
  const bytes = await gzip(writeNamedNbt("Litematica", root));
  return bytes.buffer.slice(bytes.byteOffset, bytes.byteOffset + bytes.byteLength) as ArrayBuffer;
}

function vec3(x: number, y: number, z: number): NbtValue {
  return {
    type: "compound",
    value: {
      x: { type: "int", value: x },
      y: { type: "int", value: y },
      z: { type: "int", value: z }
    }
  };
}

function blockState(name: string): NbtValue {
  return {
    type: "compound",
    value: {
      Name: { type: "string", value: name }
    }
  };
}

function packPaletteIndexes(indexes: number[], bitsPerBlock: number): bigint {
  return indexes.reduce((value, index, offset) => value | (BigInt(index) << BigInt(offset * bitsPerBlock)), BigInt(0));
}
