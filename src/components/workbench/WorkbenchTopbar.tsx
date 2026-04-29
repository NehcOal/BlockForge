import type { WorkbenchSection } from "@/components/workbench/WorkbenchShell";

export function WorkbenchTopbar({ activeSection }: { activeSection: WorkbenchSection }) {
  return (
    <div className="flex flex-col gap-2 rounded-lg border border-forge/15 bg-stone-950/55 px-4 py-3 sm:flex-row sm:items-center sm:justify-between">
      <div>
        <p className="text-xs font-semibold uppercase tracking-wide text-stone-500">Unified Product Workbench</p>
        <h2 className="text-lg font-semibold text-stone-100">{activeSection}</h2>
      </div>
      <p className="text-sm text-stone-400">Local-first voxel workflow with validated imports and alpha AI tooling.</p>
    </div>
  );
}
