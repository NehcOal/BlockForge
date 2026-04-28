import { describe, expect, it } from "vitest";
import { MAX_AI_BLOCKS, MAX_PROMPT_LENGTH, validatePromptSafety } from "@/lib/ai";

describe("validatePromptSafety", () => {
  it("rejects empty prompts", () => {
    expect(validatePromptSafety({ prompt: "   " }).valid).toBe(false);
  });

  it("rejects prompts that are too long", () => {
    const report = validatePromptSafety({ prompt: "x".repeat(MAX_PROMPT_LENGTH + 1) });
    expect(report.valid).toBe(false);
  });

  it("caps maxBlocks", () => {
    const report = validatePromptSafety({ prompt: "tower", maxBlocks: MAX_AI_BLOCKS + 100 });
    expect(report.maxBlocks).toBe(MAX_AI_BLOCKS);
    expect(report.warnings.length).toBeGreaterThan(0);
  });

  it("rejects unrelated prompts", () => {
    const report = validatePromptSafety({ prompt: "write a grocery list" });
    expect(report.valid).toBe(false);
    expect(report.errors.join(" ")).toContain("voxel structure");
  });

  it("rejects unreasonable size hints", () => {
    const report = validatePromptSafety({
      prompt: "tower",
      sizeHint: { width: 999 }
    });
    expect(report.valid).toBe(false);
    expect(report.errors.join(" ")).toContain("sizeHint.width");
  });
});
