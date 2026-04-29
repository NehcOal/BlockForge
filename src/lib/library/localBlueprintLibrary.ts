import type { AiGenerationCandidate } from "@/lib/ai";
import type { BlockForgeBlueprintV2 } from "@/lib/voxel";
import type { LocalBlueprintLibraryItem, LocalBlueprintLibrarySource } from "@/lib/library/localBlueprintLibraryTypes";

export function createLibraryItem(input: {
  blueprintV2: BlockForgeBlueprintV2;
  source: LocalBlueprintLibrarySource;
  tags?: string[];
  favorite?: boolean;
}): LocalBlueprintLibraryItem {
  const now = new Date().toISOString();
  return {
    id: `library-${input.source}-${input.blueprintV2.id}`,
    name: input.blueprintV2.name,
    description: input.blueprintV2.description,
    source: input.source,
    blueprintV2: input.blueprintV2,
    tags: input.tags ?? [],
    favorite: input.favorite ?? false,
    createdAt: now,
    updatedAt: now
  };
}

export function createLibraryItemFromCandidate(candidate: AiGenerationCandidate): LocalBlueprintLibraryItem {
  return createLibraryItem({
    blueprintV2: candidate.blueprintV2,
    source: "generated",
    tags: [candidate.provider, candidate.presetId ?? "prompt"].filter(Boolean)
  });
}
