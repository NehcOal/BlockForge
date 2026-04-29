export function BlueprintGalleryExportDialog({ selectedCount }: { selectedCount: number }) {
  return (
    <div className="rounded-lg border border-forge/15 bg-stone-950/55 p-3 text-sm text-stone-300">
      <h3 className="font-semibold text-stone-100">Gallery bundle</h3>
      <p className="mt-1 text-stone-400">Export {selectedCount} selected item(s) as a .blockforgegallery.zip alpha bundle.</p>
      <button className="mt-3 rounded-md bg-forge px-3 py-2 font-semibold text-stone-950" type="button">Export selected</button>
    </div>
  );
}
