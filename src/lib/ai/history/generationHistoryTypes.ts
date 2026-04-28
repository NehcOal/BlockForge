import type { AiGenerationCandidate, AiProviderId } from "@/lib/ai";

export type AiGenerationHistoryItem = {
  id: string;
  createdAt: string;
  provider: AiProviderId;
  prompt: string;
  presetId?: string;
  candidate: AiGenerationCandidate;
  tags: string[];
};
