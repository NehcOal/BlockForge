import type { ImportJob, ImportJobStatus } from "@/lib/import/importJobTypes";
import type { ImportReport } from "@/lib/import/importReport";

export class ImportJobQueue {
  private readonly jobs = new Map<string, ImportJob>();

  enqueue(job: Omit<ImportJob, "status">): ImportJob {
    const next = { ...job, status: "pending" as const };
    this.jobs.set(next.id, next);
    return next;
  }

  update(id: string, status: ImportJobStatus, report?: ImportReport, error?: string): ImportJob {
    const existing = this.jobs.get(id);
    if (!existing) throw new Error(`Import job ${id} was not found.`);
    const next = { ...existing, status, report, error };
    this.jobs.set(id, next);
    return next;
  }

  list(): ImportJob[] {
    return Array.from(this.jobs.values());
  }
}
