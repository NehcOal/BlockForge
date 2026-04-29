import type { LitematicBlockState } from "@/lib/voxel/litematic/litematicTypes";

export function parseLitematicBlockState(value: string): LitematicBlockState {
  const trimmed = value.trim();
  const match = /^([a-z0-9_:./-]+)(?:\[(.*)\])?$/i.exec(trimmed);
  if (!match) {
    return { name: trimmed };
  }
  const [, name, rawProperties] = match;
  if (!rawProperties) {
    return { name };
  }
  const properties = Object.fromEntries(
    rawProperties
      .split(",")
      .map((entry) => entry.trim())
      .filter(Boolean)
      .map((entry) => {
        const [key, ...rest] = entry.split("=");
        return [key.trim(), rest.join("=").trim()];
      })
      .filter(([key, value]) => key && value)
      .sort(([left], [right]) => left.localeCompare(right))
  );
  return Object.keys(properties).length ? { name, properties } : { name };
}

export function formatLitematicBlockState(state: LitematicBlockState): string {
  const properties = state.properties ?? {};
  const entries = Object.entries(properties).sort(([left], [right]) => left.localeCompare(right));
  if (entries.length === 0) {
    return state.name;
  }
  return `${state.name}[${entries.map(([key, value]) => `${key}=${value}`).join(",")}]`;
}
