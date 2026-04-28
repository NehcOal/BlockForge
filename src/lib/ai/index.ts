export type {
  AiStructureElement,
  AiStructureElementType,
  AiStructurePlan,
  AiStructurePlanIntent,
  AiStructurePlanPaletteEntry,
  AiStructurePlanV1
} from "@/lib/ai/structurePlan";
export type {
  AiGenerationMode,
  AiProviderId,
  AiProviderStatus,
  GenerateBlueprintRequest,
  GenerateBlueprintResult
} from "@/lib/ai/types";
export { aiStructurePlanJsonSchema } from "@/lib/ai/structurePlanSchema";
export {
  DEFAULT_MAX_BLOCKS,
  MAX_AI_BLOCKS,
  MAX_PROMPT_LENGTH,
  validatePromptSafety
} from "@/lib/ai/promptSafety";
export {
  estimateStructurePlanBlocks,
  validateStructurePlan,
  type StructurePlanValidationReport
} from "@/lib/ai/structurePlanValidation";
export {
  structurePlanToVoxel,
  type StructurePlanToVoxelResult
} from "@/lib/ai/structurePlanToVoxel";
export { generateWithLocalRuleProvider } from "@/lib/ai/providers/localRuleProvider";
export {
  extractOutputText,
  generateOpenAiStructurePlan,
  isOpenAiConfigured
} from "@/lib/ai/providers/openaiProvider";
export { generateBlueprint, getAiStatus } from "@/lib/ai/generationService";
export {
  aiPromptPresets,
  getAiPromptPresetById,
  getAiPromptPresets
} from "@/lib/ai/presets/promptPresets";
export type {
  AiPromptPreset,
  AiPromptPresetCategory
} from "@/lib/ai/presets/promptPresetTypes";
export {
  createCandidate,
  generateAiCandidates,
  selectBestCandidate,
  type AiGenerationCandidate
} from "@/lib/ai/candidateGeneration";
export {
  scoreAiCandidate
} from "@/lib/ai/quality/qualityRules";
export type { AiQualityScore } from "@/lib/ai/quality/qualityScore";
export {
  refinePromptLocally,
  type AiRefinementRequest
} from "@/lib/ai/refinement";
export {
  createAiFriendlyError,
  getAiFriendlyErrorMessage,
  mapUnknownAiError,
  type AiErrorCode,
  type AiFriendlyError
} from "@/lib/ai/errorMessages";
