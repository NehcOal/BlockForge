import { describe, expect, it } from "vitest";
import { aiStructurePlanJsonSchema } from "@/lib/ai";

describe("aiStructurePlanJsonSchema", () => {
  it("requires the expected top-level fields", () => {
    expect(aiStructurePlanJsonSchema.required).toContain("schemaVersion");
    expect(aiStructurePlanJsonSchema.required).toContain("palette");
    expect(aiStructurePlanJsonSchema.required).toContain("elements");
    expect(aiStructurePlanJsonSchema.properties.schemaVersion).toEqual({ const: 1 });
  });

  it("requires allowUnsupportedBlocks to be false", () => {
    expect(aiStructurePlanJsonSchema.properties.constraints.properties.allowUnsupportedBlocks).toEqual({ const: false });
  });
});
