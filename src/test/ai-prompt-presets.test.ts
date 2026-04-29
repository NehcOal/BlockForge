import { describe, expect, it } from "vitest";
import { aiPromptPresets } from "@/lib/ai";

describe("AI prompt presets", () => {
  it("contains at least 12 unique presets", () => {
    const ids = new Set(aiPromptPresets.map((preset) => preset.id));
    expect(aiPromptPresets.length).toBeGreaterThanOrEqual(12);
    expect(ids.size).toBe(aiPromptPresets.length);
  });
});
