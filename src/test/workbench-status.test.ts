import { describe, expect, it } from "vitest";
import { createWorkbenchStatus, getWorkbenchStatusTone } from "@/lib/workbench/status";

describe("workbench status", () => {
  it("creates default status and resolves tone", () => {
    expect(createWorkbenchStatus().renderMode).toBe("auto");
    expect(getWorkbenchStatusTone(createWorkbenchStatus({ warningCount: 1 }))).toBe("warning");
    expect(getWorkbenchStatusTone(createWorkbenchStatus({ errorCount: 1 }))).toBe("error");
  });
});
