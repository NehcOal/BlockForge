import type { AiGenerationCandidate } from "@/lib/ai";
import type { AiGenerationHistoryItem } from "@/lib/ai/history/generationHistoryTypes";

export function createGenerationHistoryItem(
  candidate: AiGenerationCandidate,
  tags: string[] = []
): AiGenerationHistoryItem {
  return {
    id: `history-${candidate.id}`,
    createdAt: new Date().toISOString(),
    provider: candidate.provider,
    prompt: candidate.prompt,
    presetId: candidate.presetId,
    candidate,
    tags
  };
}

export function searchGenerationHistory(items: AiGenerationHistoryItem[], query: string): AiGenerationHistoryItem[] {
  const normalized = query.trim().toLowerCase();
  if (!normalized) return items;
  return items.filter((item) => [
    item.prompt,
    item.candidate.name,
    item.provider,
    item.presetId ?? "",
    ...item.tags
  ].some((value) => value.toLowerCase().includes(normalized)));
}
