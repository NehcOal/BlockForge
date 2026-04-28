import { describe, expect, it } from "vitest";
import { scoreAiCandidate } from "@/lib/ai";
import { validateBlueprintJson, voxelModelToBlueprintV2 } from "@/lib/voxel";
import { createMedievalTower } from "@/lib/voxel/presets";

describe("AI quality score", () => {
  it("keeps total score between 0 and 100", () => {
    const model = createMedievalTower();
    const blueprint = voxelModelToBlueprintV2(model);
    const score = scoreAiCandidate({
      model,
      validationReport: validateBlueprintJson(blueprint)
    });
    expect(score.total).toBeGreaterThanOrEqual(0);
    expect(score.total).toBeLessThanOrEqual(100);
  });
});
