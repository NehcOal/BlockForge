import type { ReactNode } from "react";
import type { WorkbenchStatus } from "@/lib/workbench/status";
import { WorkbenchSidebar } from "@/components/workbench/WorkbenchSidebar";
import { WorkbenchTopbar } from "@/components/workbench/WorkbenchTopbar";
import { WorkbenchStatusBar } from "@/components/workbench/WorkbenchStatusBar";

export type WorkbenchSection = "Generate" | "Import" | "Library" | "Gallery" | "Preview" | "Export" | "AI" | "Diagnostics" | "Docs";

export function WorkbenchShell({
  activeSection,
  status,
  children
}: {
  activeSection: WorkbenchSection;
  status: WorkbenchStatus;
  children: ReactNode;
}) {
  return (
    <section className="grid flex-1 gap-4 py-6 lg:grid-cols-[180px_minmax(0,1fr)] xl:gap-5">
      <WorkbenchSidebar activeSection={activeSection} />
      <div className="min-w-0 space-y-4">
        <WorkbenchTopbar activeSection={activeSection} />
        {children}
        <WorkbenchStatusBar status={status} />
      </div>
    </section>
  );
}
