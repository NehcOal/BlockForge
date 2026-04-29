export function WorkbenchEmptyState({ onGenerateLocal }: { onGenerateLocal?: () => void }) {
  return (
    <div className="rounded-lg border border-dashed border-forge/25 bg-stone-950/50 p-5 text-stone-300">
      <h3 className="text-base font-semibold text-stone-100">No active blueprint</h3>
      <p className="mt-1 text-sm text-stone-400">Start with a local generation, import a Blueprint, or open a saved Gallery item.</p>
      <div className="mt-4 flex flex-wrap gap-2">
        <button className="rounded-md bg-forge px-3 py-2 text-sm font-semibold text-stone-950" onClick={onGenerateLocal} type="button">
          Generate locally
        </button>
        <button className="rounded-md border border-forge/30 px-3 py-2 text-sm text-stone-200" type="button">
          Import Blueprint
        </button>
        <button className="rounded-md border border-forge/30 px-3 py-2 text-sm text-stone-200" type="button">
          Open Gallery
        </button>
      </div>
    </div>
  );
}
