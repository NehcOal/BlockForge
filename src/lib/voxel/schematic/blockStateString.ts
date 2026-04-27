import type { BlockForgeBlockStateV2 } from "@/lib/voxel/blueprintProtocolV2";

const blockStatePattern = /^([a-z0-9_.-]+:[a-z0-9_./-]+)(?:\[(.*)])?$/;

export function blockStateToSpongeString(entry: BlockForgeBlockStateV2): string {
  const properties = entry.properties ?? {};
  const keys = Object.keys(properties).sort();
  if (keys.length === 0) {
    return entry.name;
  }
  return `${entry.name}[${keys.map((key) => `${key}=${properties[key]}`).join(",")}]`;
}

export function spongeStringToBlockState(value: string): BlockForgeBlockStateV2 {
  const trimmed = value.trim();
  const match = blockStatePattern.exec(trimmed);
  if (!match) {
    throw new Error(`Invalid Sponge blockstate string: ${value}`);
  }

  const propertiesText = match[2];
  if (!propertiesText) {
    return { name: match[1] };
  }

  const properties: Record<string, string> = {};
  for (const pair of propertiesText.split(",")) {
    const [key, propertyValue, ...rest] = pair.split("=");
    if (!key || !propertyValue || rest.length > 0) {
      throw new Error(`Invalid Sponge blockstate property: ${pair}`);
    }
    properties[key.trim()] = propertyValue.trim();
  }
  return { name: match[1], properties };
}
