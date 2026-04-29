import type { BlueprintGalleryItem } from "@/lib/gallery/galleryTypes";

export function BlueprintGalleryDetails({ item }: { item?: BlueprintGalleryItem }) {
  if (!item) {
    return <p className="text-sm text-stone-500">Select a gallery item to inspect metadata, validation, and export options.</p>;
  }
  return (
    <dl className="grid gap-2 text-sm text-stone-300">
      <div><dt className="text-stone-500">Name</dt><dd>{item.name}</dd></div>
      <div><dt className="text-stone-500">Source</dt><dd>{item.source}</dd></div>
      <div><dt className="text-stone-500">Tags</dt><dd>{item.tags.join(", ") || "none"}</dd></div>
      <div><dt className="text-stone-500">Updated</dt><dd>{item.updatedAt}</dd></div>
    </dl>
  );
}
