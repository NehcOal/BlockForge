import type { BlueprintGalleryItem } from "@/lib/gallery/galleryTypes";

export function BlueprintGalleryCard({ item }: { item: BlueprintGalleryItem }) {
  return (
    <article className="rounded-lg border border-forge/15 bg-stone-950/55 p-3 text-sm text-stone-300">
      <div className="flex items-start justify-between gap-3">
        <div>
          <h3 className="font-semibold text-stone-100">{item.name}</h3>
          <p className="text-xs text-stone-500">{item.source}</p>
        </div>
        <span className="rounded-full border border-forge/25 px-2 py-0.5 text-xs text-forge-light">{item.favorite ? "Favorite" : `${item.rating ?? 0}/5`}</span>
      </div>
      <p className="mt-2 line-clamp-2 text-stone-400">{item.description ?? "No description."}</p>
      <div className="mt-3 flex flex-wrap gap-2 text-xs text-stone-500">
        <span>{item.blockCount} blocks</span>
        <span>{item.paletteCount} palette</span>
        <span>{item.dimensions.width}x{item.dimensions.height}x{item.dimensions.depth}</span>
      </div>
    </article>
  );
}
