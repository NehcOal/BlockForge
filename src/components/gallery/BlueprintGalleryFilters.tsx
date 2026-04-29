export function BlueprintGalleryFilters() {
  return (
    <div className="grid gap-2 sm:grid-cols-[minmax(0,1fr)_140px_120px]">
      <input className="rounded-md border border-forge/20 bg-stone-950 px-3 py-2 text-sm text-stone-100" placeholder="Search gallery" />
      <select className="rounded-md border border-forge/20 bg-stone-950 px-3 py-2 text-sm text-stone-100" defaultValue="all">
        <option value="all">All sources</option>
        <option value="generated">Generated</option>
        <option value="imported-litematic">Litematic</option>
        <option value="imported-schem">Schematic</option>
      </select>
      <button className="rounded-md border border-forge/30 px-3 py-2 text-sm text-stone-200" type="button">Favorites</button>
    </div>
  );
}
