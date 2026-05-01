import { generateBlueprint } from "@/lib/ai/generationService";
import { scoreAiCandidate } from "@/lib/ai/quality/qualityRules";
import type { AiQualityScore } from "@/lib/ai/quality/qualityScore";
import type { AiProviderId, GenerateBlueprintRequest, GenerateBlueprintResult } from "@/lib/ai/types";
import type { AiStructurePlanV1 } from "@/lib/ai/structurePlan";
import type { BlockForgeBlueprintV2, BlueprintValidationReport } from "@/lib/voxel";
import type { VoxelModel } from "@/types/blueprint";

export type AiGenerationCandidate = {
  id: string;
  name: string;
  provider: AiProviderId;
  prompt: string;
  presetId?: string;
  structurePlan?: AiStructurePlanV1;
  model: VoxelModel;
  blueprintV2: BlockForgeBlueprintV2;
  validationReport: BlueprintValidationReport;
  qualityScore: AiQualityScore;
  warnings: string[];
  createdAt: string;
};

export async function generateAiCandidates(
  request: GenerateBlueprintRequest,
  options: { presetId?: string; fetchImpl?: typeof fetch; openAiApiKey?: string } = {}
): Promise<AiGenerationCandidate[]> {
  const maxCandidates = request.provider === "local-rule" ? 3 : 3;
  const requestedCount = Math.max(1, Math.min(maxCandidates, request.candidateCount ?? 1));
  const candidates: AiGenerationCandidate[] = [];
  for (let index = 0; index < requestedCount; index += 1) {
    const variantPrompt = index === 0 ? request.prompt : `${request.prompt} variant ${index + 1}`;
    const result = await generateBlueprint({ ...request, prompt: variantPrompt }, options);
    candidates.push(createCandidate(result, request.prompt, options.presetId, index));
  }
  return candidates;
}

export function createCandidate(
  result: GenerateBlueprintResult,
  prompt: string,
  presetId?: string,
  index = 0
): AiGenerationCandidate {
  const qualityScore = scoreAiCandidate({
    model: result.model,
    validationReport: result.validationReport,
    structurePlan: result.structurePlan,
    maxBlocks: result.structurePlan?.constraints.maxBlocks
  });
  return {
    id: `${result.provider}-${Date.now()}-${index}-${result.model.id}`,
    name: result.model.name,
    provider: result.provider,
    prompt,
    presetId,
    structurePlan: result.structurePlan,
    model: result.model,
    blueprintV2: result.blueprintV2,
    validationReport: result.validationReport,
    qualityScore,
    warnings: [...result.warnings, ...qualityScore.warnings],
    createdAt: new Date().toISOString()
  };
}

export function selectBestCandidate(candidates: AiGenerationCandidate[]): AiGenerationCandidate | undefined {
  return [...candidates].sort((a, b) => b.qualityScore.total - a.qualityScore.total)[0];
}
