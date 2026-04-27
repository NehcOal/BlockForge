export function encodeVarInt(value: number): Uint8Array {
  if (!Number.isInteger(value) || value < 0) {
    throw new Error("VarInt value must be a non-negative integer.");
  }

  const bytes: number[] = [];
  let current = value;
  do {
    let byte = current & 0x7f;
    current >>>= 7;
    if (current !== 0) {
      byte |= 0x80;
    }
    bytes.push(byte);
  } while (current !== 0);

  return Uint8Array.from(bytes);
}

export function encodeVarInts(values: number[]): Uint8Array {
  const chunks = values.map(encodeVarInt);
  const length = chunks.reduce((sum, chunk) => sum + chunk.length, 0);
  const output = new Uint8Array(length);
  let offset = 0;
  for (const chunk of chunks) {
    output.set(chunk, offset);
    offset += chunk.length;
  }
  return output;
}

export function decodeVarInts(bytes: Uint8Array): number[] {
  const values: number[] = [];
  let value = 0;
  let shift = 0;

  for (const byte of bytes) {
    value |= (byte & 0x7f) << shift;
    if ((byte & 0x80) === 0) {
      values.push(value >>> 0);
      value = 0;
      shift = 0;
      continue;
    }
    shift += 7;
    if (shift > 28) {
      throw new Error("VarInt is too long.");
    }
  }

  if (shift !== 0) {
    throw new Error("Truncated VarInt data.");
  }
  return values;
}
