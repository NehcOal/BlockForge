import type {
  AiProviderId,
  GenerateBlueprintRequest,
  GenerateBlueprintResult
} from "@/lib/ai/types";

export type AiProvider = {
  id: AiProviderId;
  generate(request: GenerateBlueprintRequest): Promise<GenerateBlueprintResult>;
};
