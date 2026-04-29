import type { BlockForgeBlueprintV2 } from "@/lib/voxel";

export type LocalBlueprintLibrarySource =
  | "generated"
  | "imported-blueprint"
  | "imported-schem"
  | "imported-pack"
  | "preset";

export type LocalBlueprintLibraryItem = {
  id: string;
  name: string;
  description?: string;
  source: LocalBlueprintLibrarySource;
  blueprintV2: BlockForgeBlueprintV2;
  tags: string[];
  favorite: boolean;
  createdAt: string;
  updatedAt: string;
};
