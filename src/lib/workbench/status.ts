import type { RenderMode } from "@/lib/voxel/rendering";

export type WorkbenchValidationStatus = "unknown" | "valid" | "warning" | "error";
export type WorkbenchImportJobStatus = "idle" | "pending" | "running" | "success" | "warning" | "error";
export type WorkbenchAiProviderStatus = "local-ready" | "external-ready" | "external-unconfigured" | "generating" | "error";

export type WorkbenchStatus = {
  activeBlueprintId?: string;
  activeSource?: string;
  validationStatus: WorkbenchValidationStatus;
  warningCount: number;
  errorCount: number;
  renderMode: RenderMode;
  importJobStatus: WorkbenchImportJobStatus;
  aiProviderStatus: WorkbenchAiProviderStatus;
  saveStatus: "idle" | "saving" | "saved" | "error";
};

export function createWorkbenchStatus(partial: Partial<WorkbenchStatus> = {}): WorkbenchStatus {
  return {
    validationStatus: "unknown",
    warningCount: 0,
    errorCount: 0,
    renderMode: "auto",
    importJobStatus: "idle",
    aiProviderStatus: "local-ready",
    saveStatus: "idle",
    ...partial
  };
}

export function getWorkbenchStatusTone(status: WorkbenchStatus): "neutral" | "success" | "warning" | "error" {
  if (status.errorCount > 0 || status.validationStatus === "error" || status.importJobStatus === "error") return "error";
  if (status.warningCount > 0 || status.validationStatus === "warning" || status.importJobStatus === "warning") return "warning";
  if (status.validationStatus === "valid" || status.importJobStatus === "success" || status.saveStatus === "saved") return "success";
  return "neutral";
}
