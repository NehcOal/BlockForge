import { describe, expect, it } from "vitest";
import { refinePromptLocally } from "@/lib/ai";

describe("refinePromptLocally", () => {
  it("adds taller instruction when size is not locked", () => {
    const refined = refinePromptLocally({
      baseCandidateId: "c1",
      originalPrompt: "small stone tower",
      refinementPrompt: "make it taller"
    });
    expect(refined).toContain("make it taller");
  });
});
