import type { BlockForgeBlueprintV2, BlueprintValidationReport } from "@/lib/voxel";
import type { VoxelModel } from "@/types/blueprint";
import type { AiStructurePlan } from "@/lib/ai/structurePlan";

export type AiProviderId = "local-rule" | "openai";

export type AiGenerationMode = "local" | "external-ai";

export type GenerateBlueprintRequest = {
  prompt: string;
  sizeHint?: {
    width?: number;
    height?: number;
    depth?: number;
  };
  styleHint?: string;
  maxBlocks?: number;
  candidateCount?: number;
  provider: AiProviderId;
};

export type GenerateBlueprintResult = {
  model: VoxelModel;
  blueprintV2: BlockForgeBlueprintV2;
  structurePlan?: AiStructurePlan;
  validationReport: BlueprintValidationReport;
  warnings: string[];
  provider: AiProviderId;
};

export type AiProviderStatus = {
  openaiConfigured: boolean;
  providers: AiProviderId[];
  defaultProvider: AiProviderId;
};
