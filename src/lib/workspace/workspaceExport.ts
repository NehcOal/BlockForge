import type { BlockForgeWorkspace } from "@/lib/workspace/workspaceTypes";

export function exportWorkspace(workspace: BlockForgeWorkspace): string {
  return JSON.stringify(workspace, null, 2);
}
