import { describe, expect, it } from "vitest";
import { importLitematicBuffer } from "@/lib/voxel";
import { createMinimalLitematic, encodeLitematicFixture, encodeRealLitematicFixture } from "@/test/litematic-fixtures";

describe("litematic import", () => {
  it("imports a minimal synthetic litematic fixture", async () => {
    const result = await importLitematicBuffer(encodeLitematicFixture(createMinimalLitematic()), "minimal.litematic");
    expect(result.report.status).toBe("success");
    expect(result.imported?.blueprints[0].blocks).toHaveLength(2);
  });

  it("imports a real gzipped NBT litematic fixture", async () => {
    const result = await importLitematicBuffer(await encodeRealLitematicFixture(), "real.litematic");
    expect(result.report.status).toBe("success");
    expect(result.imported?.blueprints[0].name).toBe("Real Minimal Litematic");
    expect(result.imported?.blueprints[0].blocks).toHaveLength(2);
  });

  it("returns an error report for malformed payloads", async () => {
    const result = await importLitematicBuffer(new TextEncoder().encode("not json").buffer, "bad.litematic");
    expect(result.report.status).toBe("error");
  });

  it("reports unknown palette references as validation errors", async () => {
    const fixture = createMinimalLitematic();
    fixture.regions[0].blocks[0].state = "missing";
    const result = await importLitematicBuffer(encodeLitematicFixture(fixture), "missing.litematic");
    expect(result.report.status).toBe("error");
  });

  it("blocks converted blueprints that fail validation", async () => {
    const fixture = createMinimalLitematic();
    fixture.regions[0].position = { x: 0.5, y: 0, z: 0 };
    const result = await importLitematicBuffer(encodeLitematicFixture(fixture), "invalid-conversion.litematic");
    expect(result.report.status).toBe("error");
    expect(result.imported).toBeUndefined();
  });
});
