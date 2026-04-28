import type { RenderMode } from "@/lib/voxel";

export type BlockForgeWorkspace = {
  schemaVersion: 1;
  id: string;
  name: string;
  activeBlueprintId?: string;
  libraryItemIds: string[];
  generationHistoryIds: string[];
  uiState?: {
    selectedTab?: string;
    renderMode?: RenderMode;
  };
  createdAt: string;
  updatedAt: string;
};
