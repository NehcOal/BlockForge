import { describe, expect, it } from "vitest";
import { readFileSync } from "node:fs";
import { join } from "node:path";

describe("Blueprint v2 schema", () => {
  it("exists and describes required v2 fields", () => {
    const schema = JSON.parse(
      readFileSync(
        join(process.cwd(), "schemas", "blockforge-blueprint-v2.schema.json"),
        "utf8"
      )
    ) as {
      required: string[];
      properties: Record<string, unknown>;
    };

    expect(schema.required).toContain("schemaVersion");
    expect(schema.required).toContain("palette");
    expect(schema.required).toContain("blocks");
    expect(schema.properties.palette).toBeDefined();
    expect(schema.properties.blocks).toBeDefined();
  });
});
