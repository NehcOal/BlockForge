import type { BlockForgeWorkspace } from "@/lib/workspace/workspaceTypes";

export function createWorkspace(name: string): BlockForgeWorkspace {
  const now = new Date().toISOString();
  return {
    schemaVersion: 1,
    id: `workspace-${name.trim().toLowerCase().replace(/[^a-z0-9]+/g, "-") || "untitled"}`,
    name,
    libraryItemIds: [],
    generationHistoryIds: [],
    createdAt: now,
    updatedAt: now
  };
}

export function renameWorkspace(workspace: BlockForgeWorkspace, name: string): BlockForgeWorkspace {
  return { ...workspace, name, updatedAt: new Date().toISOString() };
}
