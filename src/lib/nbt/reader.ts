import type { NbtValue } from "@/lib/nbt/writer";

export type NamedNbt = {
  name: string;
  value: NbtValue;
};

export function readNamedNbt(bytes: Uint8Array): NamedNbt {
  const reader = new BinaryReader(bytes);
  const tag = reader.u8();
  if (tag !== 10) {
    throw new Error("Root NBT tag must be a compound.");
  }
  const name = reader.string();
  return { name, value: readPayload(reader, tag) };
}

function readPayload(reader: BinaryReader, tag: number): NbtValue {
  switch (tag) {
    case 1:
      return { type: "byte", value: reader.i8() };
    case 2:
      return { type: "short", value: reader.i16() };
    case 3:
      return { type: "int", value: reader.i32() };
    case 4:
      return { type: "long", value: reader.i64() };
    case 7:
      return { type: "byteArray", value: reader.byteArray() };
    case 8:
      return { type: "string", value: reader.string() };
    case 9: {
      const itemType = reader.u8();
      const length = reader.i32();
      const value: NbtValue[] = [];
      for (let i = 0; i < length; i++) {
        value.push(readPayload(reader, itemType));
      }
      return { type: "list", itemType: itemType as 1 | 2 | 3 | 4 | 7 | 8 | 9 | 10 | 11, value };
    }
    case 10: {
      const value: Record<string, NbtValue> = {};
      while (true) {
        const childTag = reader.u8();
        if (childTag === 0) {
          break;
        }
        value[reader.string()] = readPayload(reader, childTag);
      }
      return { type: "compound", value };
    }
    case 11:
      return { type: "intArray", value: reader.intArray() };
    default:
      throw new Error(`Unsupported NBT tag: ${tag}`);
  }
}

class BinaryReader {
  private offset = 0;

  constructor(private readonly data: Uint8Array) {}

  u8(): number {
    this.ensure(1);
    return this.data[this.offset++];
  }

  i8(): number {
    const value = this.u8();
    return value > 127 ? value - 256 : value;
  }

  i16(): number {
    const value = (this.u8() << 8) | this.u8();
    return value > 32767 ? value - 65536 : value;
  }

  i32(): number {
    return (this.u8() << 24) | (this.u8() << 16) | (this.u8() << 8) | this.u8();
  }

  i64(): number {
    let value = BigInt(0);
    for (let i = 0; i < 8; i++) {
      value = (value << BigInt(8)) | BigInt(this.u8());
    }
    if (value & (BigInt(1) << BigInt(63))) {
      value -= BigInt(1) << BigInt(64);
    }
    return Number(value);
  }

  string(): string {
    const length = this.i16();
    this.ensure(length);
    const bytes = this.data.slice(this.offset, this.offset + length);
    this.offset += length;
    return new TextDecoder().decode(bytes);
  }

  byteArray(): Uint8Array {
    const length = this.i32();
    this.ensure(length);
    const bytes = this.data.slice(this.offset, this.offset + length);
    this.offset += length;
    return bytes;
  }

  intArray(): number[] {
    const length = this.i32();
    return Array.from({ length }, () => this.i32());
  }

  private ensure(length: number): void {
    if (this.offset + length > this.data.length) {
      throw new Error("Unexpected end of NBT data.");
    }
  }
}
