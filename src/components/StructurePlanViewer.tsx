import { useState } from "react";
import type { AiStructurePlanV1, StructurePlanValidationReport } from "@/lib/ai";

export function StructurePlanViewer({
  plan,
  report
}: {
  plan?: AiStructurePlanV1;
  report?: StructurePlanValidationReport;
}) {
  const [tab, setTab] = useState<"summary" | "json">("summary");
  if (!plan) return null;
  const elementTypes = Array.from(new Set(plan.elements.map((element) => element.type)));
  return (
    <section className="rounded border border-sky-400/15 bg-black/20 p-3 text-xs text-stone-300">
      <div className="flex gap-2">
        <button className="forge-secondary-button px-2 py-1" onClick={() => setTab("summary")} type="button">Summary</button>
        <button className="forge-secondary-button px-2 py-1" onClick={() => setTab("json")} type="button">Raw JSON</button>
      </div>
      {tab === "summary" ? (
        <div className="mt-3 space-y-1">
          <p className="font-bold text-stone-100">{plan.name}</p>
          <p>{plan.intent} · {plan.size.width}x{plan.size.height}x{plan.size.depth}</p>
          <p>Palette: {Object.keys(plan.palette).length} · Elements: {plan.elements.length}</p>
          <p>Types: {elementTypes.join(", ")}</p>
          {report?.errors.length ? <p className="text-red-200">Errors: {report.errors.length}</p> : null}
          {report?.warnings.length ? <p className="text-amber-200">Warnings: {report.warnings.length}</p> : null}
        </div>
      ) : (
        <pre className="mt-3 max-h-72 overflow-auto whitespace-pre-wrap break-words">{JSON.stringify(plan, null, 2)}</pre>
      )}
    </section>
  );
}
