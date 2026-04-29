import { validatePromptSafety } from "@/lib/ai/promptSafety";
import type {
  GenerateBlueprintRequest,
  GenerateBlueprintResult
} from "@/lib/ai/types";
import { generateVoxelModelFromPrompt, validateBlueprintJson, voxelModelToBlueprintV2 } from "@/lib/voxel";

export async function generateWithLocalRuleProvider(
  request: GenerateBlueprintRequest
): Promise<GenerateBlueprintResult> {
  const safety = validatePromptSafety(request);
  if (!safety.valid) {
    throw new Error(safety.errors.join(" "));
  }

  const localResult = generateVoxelModelFromPrompt(safety.sanitizedPrompt);
  const blueprintV2 = voxelModelToBlueprintV2(localResult.model);
  const validationReport = validateBlueprintJson(blueprintV2);

  return {
    model: localResult.model,
    blueprintV2,
    validationReport,
    warnings: [...safety.warnings, ...localResult.notes],
    provider: "local-rule"
  };
}
