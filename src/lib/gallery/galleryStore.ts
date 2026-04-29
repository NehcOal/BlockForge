import type { BlueprintGalleryItem } from "@/lib/gallery/galleryTypes";
import type { BlockForgeBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";
import { createSafeResourcePath } from "@/lib/voxel/exportUtils";

export function createGalleryItem(input: {
  blueprintV2: BlockForgeBlueprintV2;
  source: BlueprintGalleryItem["source"];
  tags?: string[];
  favorite?: boolean;
  rating?: number;
  previewPngBlobId?: string;
  now?: string;
}): BlueprintGalleryItem {
  const now = input.now ?? new Date().toISOString();
  return {
    id: createSafeResourcePath(input.blueprintV2.id || input.blueprintV2.name),
    name: input.blueprintV2.name,
    description: input.blueprintV2.description,
    source: input.source,
    blueprintV2: input.blueprintV2,
    previewPngBlobId: input.previewPngBlobId,
    tags: input.tags ?? [],
    favorite: input.favorite ?? false,
    rating: input.rating,
    blockCount: input.blueprintV2.blocks.length,
    paletteCount: Object.keys(input.blueprintV2.palette).length,
    dimensions: input.blueprintV2.size,
    createdAt: now,
    updatedAt: now
  };
}

export class InMemoryBlueprintGalleryStore {
  private readonly items = new Map<string, BlueprintGalleryItem>();

  save(item: BlueprintGalleryItem): BlueprintGalleryItem {
    const existing = this.items.get(item.id);
    const next = existing ? { ...item, createdAt: existing.createdAt, updatedAt: item.updatedAt } : item;
    this.items.set(item.id, next);
    return next;
  }

  list(): BlueprintGalleryItem[] {
    return Array.from(this.items.values());
  }

  load(id: string): BlueprintGalleryItem | undefined {
    return this.items.get(id);
  }

  delete(id: string): boolean {
    return this.items.delete(id);
  }

  duplicate(id: string, nextId: string): BlueprintGalleryItem | undefined {
    const existing = this.items.get(id);
    if (!existing) return undefined;
    const now = new Date().toISOString();
    const copy = { ...existing, id: createSafeResourcePath(nextId), name: `${existing.name} Copy`, createdAt: now, updatedAt: now };
    this.items.set(copy.id, copy);
    return copy;
  }
}
