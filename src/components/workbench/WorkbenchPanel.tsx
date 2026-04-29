import type { ReactNode } from "react";

export function WorkbenchPanel({ title, children }: { title: string; children: ReactNode }) {
  return (
    <section className="rounded-lg border border-forge/15 bg-stone-950/55 p-4">
      <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-stone-400">{title}</h3>
      {children}
    </section>
  );
}
