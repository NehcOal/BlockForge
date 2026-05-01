import { BlueprintGalleryCard } from "@/components/gallery/BlueprintGalleryCard";
import { BlueprintGalleryDetails } from "@/components/gallery/BlueprintGalleryDetails";
import { BlueprintGalleryExportDialog } from "@/components/gallery/BlueprintGalleryExportDialog";
import { BlueprintGalleryFilters } from "@/components/gallery/BlueprintGalleryFilters";
import type { BlueprintGalleryItem } from "@/lib/gallery/galleryTypes";

export function BlueprintGalleryPanel({ items }: { items: BlueprintGalleryItem[] }) {
  return (
    <section className="space-y-4 rounded-lg border border-forge/15 bg-stone-950/55 p-4">
      <div>
        <p className="text-xs font-semibold uppercase tracking-wide text-stone-500">Local Blueprint Gallery</p>
        <h2 className="text-lg font-semibold text-stone-100">Gallery</h2>
      </div>
      <BlueprintGalleryFilters />
      <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
        {items.map((item) => <BlueprintGalleryCard item={item} key={item.id} />)}
      </div>
      <BlueprintGalleryDetails item={items[0]} />
      <BlueprintGalleryExportDialog selectedCount={items.length} />
    </section>
  );
}
