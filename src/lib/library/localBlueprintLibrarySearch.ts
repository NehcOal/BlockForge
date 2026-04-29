import type { LocalBlueprintLibraryItem, LocalBlueprintLibrarySource } from "@/lib/library/localBlueprintLibraryTypes";

export type LocalBlueprintLibraryQuery = {
  searchText?: string;
  source?: LocalBlueprintLibrarySource | "all";
  favoriteOnly?: boolean;
  tag?: string;
};

export function searchLocalBlueprintLibrary(
  items: LocalBlueprintLibraryItem[],
  query: LocalBlueprintLibraryQuery
): LocalBlueprintLibraryItem[] {
  const text = query.searchText?.trim().toLowerCase() ?? "";
  return items.filter((item) => {
    if (query.source && query.source !== "all" && item.source !== query.source) return false;
    if (query.favoriteOnly && !item.favorite) return false;
    if (query.tag && !item.tags.includes(query.tag)) return false;
    if (!text) return true;
    return [item.id, item.name, item.description ?? "", item.source, ...item.tags]
      .some((value) => value.toLowerCase().includes(text));
  });
}
