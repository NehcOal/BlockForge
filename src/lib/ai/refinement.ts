export type AiRefinementRequest = {
  baseCandidateId: string;
  originalPrompt: string;
  refinementPrompt: string;
  keepStyle?: boolean;
  keepSize?: boolean;
  keepPalette?: boolean;
};

export function refinePromptLocally(request: AiRefinementRequest): string {
  const instructions: string[] = [request.originalPrompt.trim(), request.refinementPrompt.trim()].filter(Boolean);
  if (request.keepStyle) instructions.push("keep the original style");
  if (request.keepSize) instructions.push("keep the original size");
  if (request.keepPalette) instructions.push("keep the original palette");
  const text = instructions.join("; ");
  if (/taller|height|更高/i.test(request.refinementPrompt) && !request.keepSize) {
    return `${text}; make it taller`;
  }
  if (/window|窗/i.test(request.refinementPrompt)) {
    return `${text}; add more windows`;
  }
  if (/stone|石/i.test(request.refinementPrompt) && !request.keepPalette) {
    return `${text}; use more stone bricks`;
  }
  return text;
}
