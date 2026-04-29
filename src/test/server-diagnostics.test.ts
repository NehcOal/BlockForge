import { describe, expect, it } from "vitest";
import { createServerDiagnosticsReport, formatServerStatus, validateServerDiagnosticsConfig } from "@/lib/server/diagnostics";

describe("server diagnostics", () => {
  it("formats registry summary and warnings", () => {
    const report = createServerDiagnosticsReport({
      version: "3.5.0-alpha.1",
      loader: "neoforge",
      minecraftVersion: "1.21.1",
      loadedLooseBlueprints: 2,
      loadedPacks: 1,
      loadedSchematics: 1,
      loadedLitematics: 1,
      protectionEnabled: true,
      nearbyContainersEnabled: true,
      permissionMode: "server",
      warnings: ["example"]
    }, "2026-01-01T00:00:00.000Z");
    expect(report.totalLoadedSources).toBe(5);
    expect(formatServerStatus(report)[1]).toContain("litematics");
  });

  it("validates config ranges", () => {
    expect(validateServerDiagnosticsConfig({ maxBlocks: 0, searchRadius: 99, maxFileSizeMb: 101 })).toHaveLength(3);
  });
});
