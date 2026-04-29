export type WorkbenchActionId =
  | "import-blueprint-json"
  | "import-schem"
  | "import-litematic"
  | "generate-local"
  | "generate-ai"
  | "export-blueprint-v2"
  | "export-schem"
  | "export-png"
  | "save-library"
  | "open-gallery"
  | "run-validation";

export type WorkbenchAction = {
  id: WorkbenchActionId;
  label: string;
  group: "Import" | "Generate" | "Export" | "Library" | "Diagnostics";
  enabled: boolean;
};

export const defaultWorkbenchActions: WorkbenchAction[] = [
  { id: "import-blueprint-json", label: "Import Blueprint JSON", group: "Import", enabled: true },
  { id: "import-schem", label: "Import .schem", group: "Import", enabled: true },
  { id: "import-litematic", label: "Import .litematic", group: "Import", enabled: true },
  { id: "generate-local", label: "Generate locally", group: "Generate", enabled: true },
  { id: "generate-ai", label: "Generate with AI", group: "Generate", enabled: false },
  { id: "export-blueprint-v2", label: "Export Blueprint v2", group: "Export", enabled: true },
  { id: "export-schem", label: "Export .schem", group: "Export", enabled: true },
  { id: "export-png", label: "Export PNG", group: "Export", enabled: true },
  { id: "save-library", label: "Save to Library", group: "Library", enabled: true },
  { id: "open-gallery", label: "Open Gallery", group: "Library", enabled: true },
  { id: "run-validation", label: "Run Validation", group: "Diagnostics", enabled: true }
];

export function searchWorkbenchActions(actions: WorkbenchAction[], query: string): WorkbenchAction[] {
  const text = query.trim().toLowerCase();
  if (!text) return actions;
  return actions.filter((action) => [action.id, action.label, action.group].some((value) => value.toLowerCase().includes(text)));
}
