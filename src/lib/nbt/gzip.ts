export async function gzip(bytes: Uint8Array): Promise<Uint8Array> {
  return transform("gzip", bytes);
}

export async function ungzip(bytes: Uint8Array, maxOutputBytes?: number): Promise<Uint8Array> {
  return transform("gzip", bytes, true, maxOutputBytes);
}

async function transform(
  format: CompressionFormat,
  bytes: Uint8Array,
  decompress = false,
  maxOutputBytes?: number
): Promise<Uint8Array> {
  type ByteStreamPair = ReadableWritablePair<Uint8Array, Uint8Array>;
  const source = new ReadableStream<Uint8Array>({
    start(controller) {
      controller.enqueue(bytes);
      controller.close();
    }
  });
  const transformer = (decompress
    ? new DecompressionStream(format)
    : new CompressionStream(format)) as unknown as ByteStreamPair;
  const stream = source.pipeThrough(transformer);
  if (maxOutputBytes !== undefined) {
    return readBounded(stream, maxOutputBytes);
  }
  return new Uint8Array(await new Response(stream).arrayBuffer());
}

async function readBounded(stream: ReadableStream<Uint8Array>, maxBytes: number): Promise<Uint8Array> {
  const reader = stream.getReader();
  const chunks: Uint8Array[] = [];
  let total = 0;
  while (true) {
    const { done, value } = await reader.read();
    if (done) {
      break;
    }
    total += value.byteLength;
    if (total > maxBytes) {
      await reader.cancel();
      throw new Error(`GZip output exceeds ${maxBytes} bytes.`);
    }
    chunks.push(value);
  }
  const output = new Uint8Array(total);
  let offset = 0;
  for (const chunk of chunks) {
    output.set(chunk, offset);
    offset += chunk.byteLength;
  }
  return output;
}
