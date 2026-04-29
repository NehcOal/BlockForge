import type { ImportReport } from "@/lib/import/importReport";

export type ImportJobStatus = "pending" | "running" | "success" | "warning" | "error" | "cancelled";

export type ImportJob = {
  id: string;
  sourceFileName?: string;
  status: ImportJobStatus;
  report?: ImportReport;
  error?: string;
};
