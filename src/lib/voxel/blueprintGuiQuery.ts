export type BlueprintSourceFilter = "all" | "loose" | "pack" | "schematic";
export type BlueprintWarningFilter = "all" | "with_warnings" | "without_warnings";
export type BlueprintSortMode = "name_asc" | "name_desc" | "blocks_asc" | "blocks_desc" | "source_asc" | "source_desc";

export type BlueprintGuiSummary = {
  id: string;
  name: string;
  blockCount: number;
  sourceType: "loose" | "pack" | "schematic";
  sourceId: string;
  warningCount: number;
  tags: string[];
};

export type BlueprintGuiQuery = {
  searchText?: string;
  sourceFilter?: BlueprintSourceFilter;
  warningFilter?: BlueprintWarningFilter;
  sortMode?: BlueprintSortMode;
  page?: number;
  pageSize?: number;
};

export function queryBlueprintSummaries(input: BlueprintGuiSummary[], query: BlueprintGuiQuery = {}) {
  const pageSize = query.pageSize && query.pageSize > 0 ? query.pageSize : 8;
  const searchText = query.searchText?.trim().toLowerCase() ?? "";
  const sourceFilter = query.sourceFilter ?? "all";
  const warningFilter = query.warningFilter ?? "all";
  const sortMode = query.sortMode ?? "name_asc";
  const filtered = input
    .filter((summary) => sourceFilter === "all" || summary.sourceType === sourceFilter)
    .filter((summary) => warningFilter === "all"
      || (warningFilter === "with_warnings" ? summary.warningCount > 0 : summary.warningCount === 0))
    .filter((summary) => !searchText || [summary.id, summary.name, summary.sourceId, ...summary.tags]
      .some((value) => value.toLowerCase().includes(searchText)))
    .sort(comparator(sortMode));

  const totalItems = filtered.length;
  const totalPages = totalItems === 0 ? 0 : Math.ceil(totalItems / pageSize);
  const page = normalizePage(query.page ?? 0, totalPages);
  const start = totalItems === 0 ? 0 : page * pageSize;
  return {
    items: filtered.slice(start, start + pageSize),
    page,
    pageSize,
    totalItems,
    totalPages,
    hasPrevious: page > 0,
    hasNext: totalPages > 0 && page < totalPages - 1
  };
}

function comparator(sortMode: BlueprintSortMode) {
  const byName = (left: BlueprintGuiSummary, right: BlueprintGuiSummary) =>
    left.name.localeCompare(right.name) || left.id.localeCompare(right.id);
  switch (sortMode) {
    case "name_desc":
      return (left: BlueprintGuiSummary, right: BlueprintGuiSummary) => byName(right, left);
    case "blocks_asc":
      return (left: BlueprintGuiSummary, right: BlueprintGuiSummary) => left.blockCount - right.blockCount || byName(left, right);
    case "blocks_desc":
      return (left: BlueprintGuiSummary, right: BlueprintGuiSummary) => right.blockCount - left.blockCount || byName(left, right);
    case "source_asc":
      return (left: BlueprintGuiSummary, right: BlueprintGuiSummary) => left.sourceType.localeCompare(right.sourceType) || byName(left, right);
    case "source_desc":
      return (left: BlueprintGuiSummary, right: BlueprintGuiSummary) => right.sourceType.localeCompare(left.sourceType) || byName(left, right);
    case "name_asc":
    default:
      return byName;
  }
}

function normalizePage(page: number, totalPages: number): number {
  if (totalPages <= 0 || page < 0) return 0;
  return Math.min(page, totalPages - 1);
}
