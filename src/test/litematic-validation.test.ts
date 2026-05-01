import { describe, expect, it } from "vitest";
import { validateParsedLitematic } from "@/lib/voxel";
import { createMinimalLitematic } from "@/test/litematic-fixtures";

describe("litematic validation", () => {
  it("accepts a minimal parsed litematic", () => {
    const result = validateParsedLitematic(createMinimalLitematic());
    expect(result.valid).toBe(true);
    expect(result.report.status).toBe("success");
  });

  it("reports unsupported version and volume limits", () => {
    const fixture = createMinimalLitematic();
    fixture.version = 99;
    fixture.regions[0].size = { width: 100, height: 100, depth: 101 };
    const result = validateParsedLitematic(fixture, {
      maxFileSizeBytes: 1000,
      maxPaletteSize: 4096,
      maxRegionCount: 32,
      maxTotalVolume: 1_000_000
    });
    expect(result.valid).toBe(false);
    expect(result.warnings.some((warning) => warning.includes("Unsupported"))).toBe(true);
    expect(result.errors.some((error) => error.includes("volume"))).toBe(true);
  });

  it("warns on multiple regions", () => {
    const fixture = createMinimalLitematic();
    fixture.regions.push({ ...fixture.regions[0], name: "second", position: { x: 2, y: 0, z: 0 } });
    const result = validateParsedLitematic(fixture);
    expect(result.valid).toBe(true);
    expect(result.warnings.some((warning) => warning.includes("Multiple regions"))).toBe(true);
  });
});
