import { getWorkbenchStatusTone, type WorkbenchStatus } from "@/lib/workbench/status";

const toneClass = {
  neutral: "border-stone-700 text-stone-300",
  success: "border-emerald-500/40 text-emerald-200",
  warning: "border-amber-500/40 text-amber-200",
  error: "border-red-500/40 text-red-200"
};

export function WorkbenchStatusBar({ status }: { status: WorkbenchStatus }) {
  const tone = getWorkbenchStatusTone(status);
  return (
    <div className={`flex flex-wrap gap-2 rounded-lg border bg-stone-950/60 px-3 py-2 text-xs ${toneClass[tone]}`}>
      <span>Source: {status.activeSource ?? "preset"}</span>
      <span>Blueprint: {status.activeBlueprintId ?? "none"}</span>
      <span>Validation: {status.validationStatus}</span>
      <span>Warnings: {status.warningCount}</span>
      <span>Errors: {status.errorCount}</span>
      <span>Render: {status.renderMode}</span>
      <span>Import: {status.importJobStatus}</span>
      <span>AI: {status.aiProviderStatus}</span>
    </div>
  );
}
