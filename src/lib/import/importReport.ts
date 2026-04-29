export type ImportReport = {
  id: string;
  sourceType: "blueprint-json" | "schem" | "blueprint-pack" | "workspace" | "litematic" | "gallery";
  sourceFileName?: string;
  status: "success" | "warning" | "error";
  summary: {
    importedItems: number;
    warnings: number;
    errors: number;
  };
  messages: Array<{
    severity: "info" | "warning" | "error";
    path?: string;
    message: string;
    suggestion?: string;
  }>;
};

export function createImportReport(input: Omit<ImportReport, "status" | "summary">): ImportReport {
  const warnings = input.messages.filter((message) => message.severity === "warning").length;
  const errors = input.messages.filter((message) => message.severity === "error").length;
  return {
    ...input,
    status: errors > 0 ? "error" : warnings > 0 ? "warning" : "success",
    summary: {
      importedItems: input.messages.filter((message) => message.severity === "info").length,
      warnings,
      errors
    }
  };
}
