import { defaultWorkbenchActions, searchWorkbenchActions } from "@/lib/workbench/actions";

export function WorkbenchCommandPalette({ query = "" }: { query?: string }) {
  const actions = searchWorkbenchActions(defaultWorkbenchActions, query);
  return (
    <div className="rounded-lg border border-forge/15 bg-stone-950/80 p-3 text-sm text-stone-200">
      <p className="mb-2 text-xs font-semibold uppercase tracking-wide text-stone-500">Command Palette</p>
      <div className="grid gap-1">
        {actions.map((action) => (
          <button
            key={action.id}
            className="flex items-center justify-between rounded-md px-2 py-1.5 text-left hover:bg-stone-900 disabled:cursor-not-allowed disabled:opacity-45"
            disabled={!action.enabled}
            type="button"
          >
            <span>{action.label}</span>
            <span className="text-xs text-stone-500">{action.group}</span>
          </button>
        ))}
      </div>
    </div>
  );
}
