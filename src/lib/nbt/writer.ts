export type NbtValue =
  | { type: "byte"; value: number }
  | { type: "short"; value: number }
  | { type: "int"; value: number }
  | { type: "long"; value: number | bigint }
  | { type: "string"; value: string }
  | { type: "byteArray"; value: Uint8Array }
  | { type: "intArray"; value: number[] }
  | { type: "list"; itemType: NbtTagType; value: NbtValue[] }
  | { type: "compound"; value: Record<string, NbtValue> };

export type NbtTagType = 1 | 2 | 3 | 4 | 7 | 8 | 9 | 10 | 11;

const tagByType: Record<NbtValue["type"], NbtTagType> = {
  byte: 1,
  short: 2,
  int: 3,
  long: 4,
  byteArray: 7,
  string: 8,
  list: 9,
  compound: 10,
  intArray: 11
};

export function writeNamedNbt(name: string, value: NbtValue): Uint8Array {
  const writer = new BinaryWriter();
  writer.u8(tagByType[value.type]);
  writer.string(name);
  writePayload(writer, value);
  return writer.bytes();
}

function writePayload(writer: BinaryWriter, value: NbtValue): void {
  switch (value.type) {
    case "byte":
      writer.i8(value.value);
      return;
    case "short":
      writer.i16(value.value);
      return;
    case "int":
      writer.i32(value.value);
      return;
    case "long":
      writer.i64(value.value);
      return;
    case "string":
      writer.string(value.value);
      return;
    case "byteArray":
      writer.i32(value.value.length);
      writer.raw(value.value);
      return;
    case "intArray":
      writer.i32(value.value.length);
      value.value.forEach((item) => writer.i32(item));
      return;
    case "list":
      writer.u8(value.itemType);
      writer.i32(value.value.length);
      value.value.forEach((item) => writePayload(writer, item));
      return;
    case "compound":
      for (const [key, child] of Object.entries(value.value)) {
        writer.u8(tagByType[child.type]);
        writer.string(key);
        writePayload(writer, child);
      }
      writer.u8(0);
      return;
  }
}

class BinaryWriter {
  private data: number[] = [];

  bytes(): Uint8Array {
    return Uint8Array.from(this.data);
  }

  raw(bytes: Uint8Array): void {
    this.data.push(...bytes);
  }

  u8(value: number): void {
    this.data.push(value & 0xff);
  }

  i8(value: number): void {
    this.u8(value);
  }

  i16(value: number): void {
    this.data.push((value >> 8) & 0xff, value & 0xff);
  }

  i32(value: number): void {
    this.data.push((value >> 24) & 0xff, (value >> 16) & 0xff, (value >> 8) & 0xff, value & 0xff);
  }

  i64(value: number | bigint): void {
    let big = typeof value === "bigint" ? value : BigInt(Math.trunc(value));
    if (big < 0) {
      big = (BigInt(1) << BigInt(64)) + big;
    }
    for (let shift = 56; shift >= 0; shift -= 8) {
      this.u8(Number((big >> BigInt(shift)) & BigInt(0xff)));
    }
  }

  string(value: string): void {
    const encoded = new TextEncoder().encode(value);
    this.i16(encoded.length);
    this.raw(encoded);
  }
}
