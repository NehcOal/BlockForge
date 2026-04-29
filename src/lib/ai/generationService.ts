import { generateWithLocalRuleProvider } from "@/lib/ai/providers/localRuleProvider";
import { generateOpenAiStructurePlan, isOpenAiConfigured } from "@/lib/ai/providers/openaiProvider";
import { structurePlanToVoxel } from "@/lib/ai/structurePlanToVoxel";
import { validateStructurePlan } from "@/lib/ai/structurePlanValidation";
import type {
  AiProviderStatus,
  GenerateBlueprintRequest,
  GenerateBlueprintResult
} from "@/lib/ai/types";
import { validateBlueprintJson, voxelModelToBlueprintV2 } from "@/lib/voxel";

export function getAiStatus(apiKey = process.env.OPENAI_API_KEY): AiProviderStatus {
  return {
    openaiConfigured: isOpenAiConfigured(apiKey),
    providers: ["local-rule", "openai"],
    defaultProvider: "local-rule"
  };
}

export async function generateBlueprint(
  request: GenerateBlueprintRequest,
  options: {
    openAiApiKey?: string;
    fetchImpl?: typeof fetch;
  } = {}
): Promise<GenerateBlueprintResult> {
  if (request.provider === "local-rule") {
    return generateWithLocalRuleProvider(request);
  }

  const structurePlan = await generateOpenAiStructurePlan(request, {
    apiKey: options.openAiApiKey,
    fetchImpl: options.fetchImpl
  });
  const planReport = validateStructurePlan(structurePlan);
  if (!planReport.valid) {
    throw new Error(
      `AI structure plan failed validation: ${planReport.errors.map((item) => `${item.path}: ${item.message}`).join("; ")}`
    );
  }

  const converted = structurePlanToVoxel(structurePlan);
  const blueprintV2 = voxelModelToBlueprintV2(converted.model);
  const validationReport = validateBlueprintJson(blueprintV2);
  if (!validationReport.valid) {
    throw new Error("AI generated blueprint failed BlockForge validation.");
  }

  return {
    model: converted.model,
    blueprintV2,
    structurePlan,
    validationReport,
    warnings: [
      ...planReport.warnings.map((item) => `${item.path}: ${item.message}`),
      ...converted.warnings
    ],
    provider: "openai"
  };
}
