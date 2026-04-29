import { describe, expect, it } from "vitest";
import { generateAiCandidates, selectBestCandidate } from "@/lib/ai";

describe("AI candidate generation", () => {
  it("returns three local candidates with unique ids", async () => {
    const candidates = await generateAiCandidates({
      prompt: "small stone tower",
      provider: "local-rule",
      candidateCount: 3
    });
    expect(candidates).toHaveLength(3);
    expect(new Set(candidates.map((candidate) => candidate.id)).size).toBe(3);
  });

  it("selects the highest quality candidate", async () => {
    const candidates = await generateAiCandidates({
      prompt: "small stone tower",
      provider: "local-rule",
      candidateCount: 3
    });
    const best = selectBestCandidate(candidates);
    expect(best?.qualityScore.total).toBe(Math.max(...candidates.map((candidate) => candidate.qualityScore.total)));
  });
});
