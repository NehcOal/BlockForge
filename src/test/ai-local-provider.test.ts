import { describe, expect, it } from "vitest";
import { generateWithLocalRuleProvider } from "@/lib/ai";
import { validateVoxelModel } from "@/lib/voxel";

describe("generateWithLocalRuleProvider", () => {
  it("returns a valid VoxelModel and Blueprint v2 without external API calls", async () => {
    const result = await generateWithLocalRuleProvider({
      prompt: "small bridge with wood",
      provider: "local-rule"
    });

    expect(result.provider).toBe("local-rule");
    expect(validateVoxelModel(result.model)).toBe(true);
    expect(result.blueprintV2.schemaVersion).toBe(2);
    expect(result.validationReport.valid).toBe(true);
    expect(result.warnings.join(" ")).toContain("No external AI API is used.");
  });
});
