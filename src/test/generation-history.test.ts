import { describe, expect, it } from "vitest";
import { createGenerationHistoryItem, searchGenerationHistory } from "@/lib/ai/history/generationHistory";
import { InMemoryGenerationHistoryStore } from "@/lib/ai/history/generationHistoryStore";
import { generateAiCandidates } from "@/lib/ai";

describe("generation history", () => {
  it("saves, lists, searches, and deletes items", async () => {
    const [candidate] = await generateAiCandidates({ prompt: "small stone tower", provider: "local-rule" });
    const item = createGenerationHistoryItem(candidate, ["tower"]);
    const store = new InMemoryGenerationHistoryStore();
    store.save(item);
    expect(store.list()).toHaveLength(1);
    expect(searchGenerationHistory(store.list(), "tower")).toHaveLength(1);
    expect(store.delete(item.id)).toBe(true);
    expect(store.list()).toHaveLength(0);
  });
});
