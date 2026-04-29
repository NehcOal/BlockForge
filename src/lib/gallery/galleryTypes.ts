import type { BlockForgeBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";

export type BlueprintGallerySource =
  | "generated"
  | "imported-blueprint"
  | "imported-schem"
  | "imported-litematic"
  | "imported-pack"
  | "preset"
  | "workspace";

export type BlueprintGalleryItem = {
  id: string;
  name: string;
  description?: string;
  source: BlueprintGallerySource;
  blueprintV2: BlockForgeBlueprintV2;
  previewPngBlobId?: string;
  tags: string[];
  favorite: boolean;
  rating?: number;
  blockCount: number;
  paletteCount: number;
  dimensions: {
    width: number;
    height: number;
    depth: number;
  };
  createdAt: string;
  updatedAt: string;
};

export type BlueprintGalleryBundle = {
  schemaVersion: 1;
  exportedAt: string;
  items: BlueprintGalleryItem[];
};

export type BlueprintGalleryQuery = {
  searchText?: string;
  source?: BlueprintGallerySource | "all";
  favoriteOnly?: boolean;
  tags?: string[];
  sortMode?: "updated-desc" | "name-asc" | "blocks-desc" | "rating-desc";
};
