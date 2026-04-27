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
