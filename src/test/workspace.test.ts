import { describe, expect, it } from "vitest";
import { createWorkspace, renameWorkspace } from "@/lib/workspace/workspaceStore";
import { exportWorkspace } from "@/lib/workspace/workspaceExport";
import { importWorkspace } from "@/lib/workspace/workspaceImport";

describe("workspace", () => {
  it("exports and imports a workspace", () => {
    const workspace = renameWorkspace(createWorkspace("Alpha"), "Beta");
    const imported = importWorkspace(exportWorkspace(workspace));
    expect(imported.name).toBe("Beta");
    expect(imported.schemaVersion).toBe(1);
  });
});
