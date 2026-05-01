import { describe, expect, it } from "vitest";
import { defaultWorkbenchActions, searchWorkbenchActions } from "@/lib/workbench/actions";

describe("workbench actions", () => {
  it("finds command palette actions", () => {
    expect(searchWorkbenchActions(defaultWorkbenchActions, "litematic")[0].id).toBe("import-litematic");
    expect(searchWorkbenchActions(defaultWorkbenchActions, "")).toHaveLength(defaultWorkbenchActions.length);
  });
});
