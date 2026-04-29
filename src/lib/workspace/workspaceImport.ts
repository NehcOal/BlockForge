import type { BlockForgeWorkspace } from "@/lib/workspace/workspaceTypes";

export function importWorkspace(json: string): BlockForgeWorkspace {
  const value = JSON.parse(json) as BlockForgeWorkspace;
  if (value.schemaVersion !== 1 || !value.id || !value.name) {
    throw new Error("Invalid BlockForge workspace.");
  }
  return value;
}
