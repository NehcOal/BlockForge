import type { BlueprintGalleryItem, BlueprintGalleryQuery } from "@/lib/gallery/galleryTypes";

export function queryBlueprintGallery(
  items: BlueprintGalleryItem[],
  query: BlueprintGalleryQuery = {}
): BlueprintGalleryItem[] {
  const text = query.searchText?.trim().toLowerCase() ?? "";
  const tags = query.tags ?? [];
  const filtered = items.filter((item) => {
    if (query.source && query.source !== "all" && item.source !== query.source) return false;
    if (query.favoriteOnly && !item.favorite) return false;
    if (tags.length && !tags.every((tag) => item.tags.includes(tag))) return false;
    if (!text) return true;
    return [item.id, item.name, item.description ?? "", item.source, ...item.tags]
      .some((value) => value.toLowerCase().includes(text));
  });
  return sortGalleryItems(filtered, query.sortMode ?? "updated-desc");
}

function sortGalleryItems(items: BlueprintGalleryItem[], sortMode: NonNullable<BlueprintGalleryQuery["sortMode"]>): BlueprintGalleryItem[] {
  return [...items].sort((left, right) => {
    if (sortMode === "name-asc") return left.name.localeCompare(right.name);
    if (sortMode === "blocks-desc") return right.blockCount - left.blockCount || left.name.localeCompare(right.name);
    if (sortMode === "rating-desc") return (right.rating ?? 0) - (left.rating ?? 0) || left.name.localeCompare(right.name);
    return right.updatedAt.localeCompare(left.updatedAt);
  });
}
