import type { WorkbenchSection } from "@/components/workbench/WorkbenchShell";

const sections: WorkbenchSection[] = ["Generate", "Import", "Library", "Gallery", "Preview", "Export", "AI", "Diagnostics", "Docs"];

export function WorkbenchSidebar({ activeSection }: { activeSection: WorkbenchSection }) {
  return (
    <nav className="rounded-lg border border-forge/15 bg-stone-950/55 p-2 text-sm text-stone-300">
      <div className="mb-2 px-2 text-xs font-semibold uppercase tracking-wide text-stone-500">Workbench</div>
      <div className="space-y-1">
        {sections.map((section) => (
          <button
            key={section}
            className={`w-full rounded-md px-3 py-2 text-left transition ${section === activeSection ? "bg-forge/20 text-forge-light" : "hover:bg-stone-900"}`}
            type="button"
          >
            {section}
          </button>
        ))}
      </div>
    </nav>
  );
}
