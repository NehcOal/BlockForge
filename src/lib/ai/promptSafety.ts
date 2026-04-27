import type { GenerateBlueprintRequest } from "@/lib/ai/types";

export const MAX_PROMPT_LENGTH = 1000;
export const DEFAULT_MAX_BLOCKS = 2000;
export const MAX_AI_BLOCKS = 5000;

const blockedPromptTerms = [
  "bomb",
  "explosive",
  "weapon",
  "attack plan",
  "现实攻击",
  "爆炸物",
  "武器"
];

export type PromptSafetyResult = {
  valid: boolean;
  sanitizedPrompt: string;
  maxBlocks: number;
  errors: string[];
  warnings: string[];
};

export function validatePromptSafety(
  request: Pick<GenerateBlueprintRequest, "prompt" | "maxBlocks">
): PromptSafetyResult {
  const sanitizedPrompt = request.prompt.trim();
  const errors: string[] = [];
  const warnings: string[] = [];

  if (!sanitizedPrompt) {
    errors.push("Prompt is required.");
  }

  if (sanitizedPrompt.length > MAX_PROMPT_LENGTH) {
    errors.push(`Prompt must be ${MAX_PROMPT_LENGTH} characters or fewer.`);
  }

  const lowered = sanitizedPrompt.toLowerCase();
  if (blockedPromptTerms.some((term) => lowered.includes(term))) {
    errors.push("Prompt asks for unsafe real-world harmful content.");
  }

  const requestedMaxBlocks = request.maxBlocks ?? DEFAULT_MAX_BLOCKS;
  if (!Number.isInteger(requestedMaxBlocks) || requestedMaxBlocks < 1) {
    errors.push("maxBlocks must be a positive integer.");
  }

  const maxBlocks = Math.min(
    Math.max(1, Number.isInteger(requestedMaxBlocks) ? requestedMaxBlocks : DEFAULT_MAX_BLOCKS),
    MAX_AI_BLOCKS
  );

  if (requestedMaxBlocks > MAX_AI_BLOCKS) {
    warnings.push(`maxBlocks was capped at ${MAX_AI_BLOCKS}.`);
  }

  return {
    valid: errors.length === 0,
    sanitizedPrompt,
    maxBlocks,
    errors,
    warnings
  };
}
