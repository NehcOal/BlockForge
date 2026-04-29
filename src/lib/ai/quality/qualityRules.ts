import { getUsedBlockTypes } from "@/lib/voxel";
import type { BlueprintValidationReport } from "@/lib/voxel";
import type { AiStructurePlanV1 } from "@/lib/ai/structurePlan";
import { clampScore, type AiQualityScore } from "@/lib/ai/quality/qualityScore";
import type { VoxelModel } from "@/types/blueprint";

export function scoreAiCandidate(input: {
  model: VoxelModel;
  validationReport: BlueprintValidationReport;
  structurePlan?: AiStructurePlanV1;
  maxBlocks?: number;
}): AiQualityScore {
  const notes: string[] = [];
  const warnings: string[] = [];
  const errors = input.validationReport.issues.filter((issue) => issue.severity === "error").length;
  const validation = clampScore(100 - errors * 25);
  if (errors > 0) warnings.push(`${errors} validation error(s).`);

  const blockCount = input.model.blocks.length;
  const maxBlocks = input.maxBlocks ?? input.structurePlan?.constraints.maxBlocks ?? 2000;
  let buildability = blockCount > 0 ? 85 : 5;
  if (blockCount > maxBlocks) {
    buildability -= 40;
    warnings.push(`Block count exceeds maxBlocks=${maxBlocks}.`);
  }

  const usedTypes = getUsedBlockTypes(input.model);
  const materialDiversity = clampScore(usedTypes.length <= 1 ? 35 : Math.min(100, 45 + usedTypes.length * 12));
  if (usedTypes.length <= 1) warnings.push("Palette is very narrow.");

  const size = input.model.size;
  const extremeRatio = Math.max(size.width, size.height, size.depth) / Math.max(1, Math.min(size.width, size.height, size.depth));
  let structure = clampScore(80 - Math.max(0, extremeRatio - 3) * 10);
  const elementTypes = new Set(input.structurePlan?.elements.map((element) => element.type) ?? []);
  for (const bonus of ["window", "door", "roof", "pillar", "arch"]) {
    if (elementTypes.has(bonus as never)) structure += 4;
  }
  structure = clampScore(structure);

  const symmetry = estimateSymmetry(input.model);
  const total = clampScore(validation * 0.35 + buildability * 0.25 + materialDiversity * 0.15 + structure * 0.15 + symmetry * 0.1);
  if (total >= 80) notes.push("Strong candidate for preview/export.");

  return {
    total,
    structure,
    buildability: clampScore(buildability),
    materialDiversity,
    symmetry,
    validation,
    notes,
    warnings
  };
}

function estimateSymmetry(model: VoxelModel): number {
  if (model.blocks.length === 0) return 0;
  const keys = new Set(model.blocks.map((block) => `${block.x}:${block.y}:${block.z}:${block.block}`));
  let mirrored = 0;
  for (const block of model.blocks) {
    const mirrorX = model.size.width - 1 - block.x;
    if (keys.has(`${mirrorX}:${block.y}:${block.z}:${block.block}`)) mirrored += 1;
  }
  return clampScore((mirrored / model.blocks.length) * 100);
}
