import { describe, expect, it } from "vitest";
import { gzip, ungzip } from "@/lib/nbt/gzip";
import { decodeVarInts, encodeVarInt, encodeVarInts } from "@/lib/nbt/varint";

describe("NBT VarInt", () => {
  it("roundtrips representative values", () => {
    const values = [0, 1, 2, 127, 128, 255, 16_384, 1_000_000];
    expect(decodeVarInts(encodeVarInts(values))).toEqual(values);
  });

  it("encodes small values as one byte", () => {
    expect(Array.from(encodeVarInt(127))).toEqual([127]);
  });

  it("rejects gzip output above the requested limit", async () => {
    const compressed = await gzip(Uint8Array.from([1, 2, 3, 4, 5]));
    await expect(ungzip(compressed, 4)).rejects.toThrow("GZip output exceeds 4 bytes.");
  });
});
