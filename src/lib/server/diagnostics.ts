export type ServerDiagnosticsInput = {
  version: string;
  loader: "neoforge" | "fabric" | "forge";
  minecraftVersion: string;
  loadedLooseBlueprints: number;
  loadedPacks: number;
  loadedSchematics: number;
  loadedLitematics: number;
  protectionEnabled: boolean;
  nearbyContainersEnabled: boolean;
  permissionMode: string;
  warnings: string[];
};

export type ServerDiagnosticsReport = ServerDiagnosticsInput & {
  generatedAt: string;
  totalLoadedSources: number;
};

export function createServerDiagnosticsReport(
  input: ServerDiagnosticsInput,
  generatedAt = new Date().toISOString()
): ServerDiagnosticsReport {
  return {
    ...input,
    generatedAt,
    totalLoadedSources: input.loadedLooseBlueprints + input.loadedPacks + input.loadedSchematics + input.loadedLitematics
  };
}

export function formatServerStatus(report: ServerDiagnosticsReport): string[] {
  return [
    `BlockForge ${report.version} (${report.loader}, Minecraft ${report.minecraftVersion})`,
    `Loaded: ${report.loadedLooseBlueprints} loose, ${report.loadedPacks} packs, ${report.loadedSchematics} schematics, ${report.loadedLitematics} litematics`,
    `Protection: ${report.protectionEnabled ? "enabled" : "disabled"}`,
    `Nearby containers: ${report.nearbyContainersEnabled ? "enabled" : "disabled"}`,
    `Permission mode: ${report.permissionMode}`,
    `Diagnostics warnings: ${report.warnings.length}`
  ];
}

export function validateServerDiagnosticsConfig(input: { maxBlocks?: number; searchRadius?: number; maxFileSizeMb?: number }): string[] {
  const warnings: string[] = [];
  if (input.maxBlocks !== undefined && (input.maxBlocks < 1 || input.maxBlocks > 1_000_000)) {
    warnings.push("maxBlocks should be between 1 and 1,000,000.");
  }
  if (input.searchRadius !== undefined && (input.searchRadius < 0 || input.searchRadius > 64)) {
    warnings.push("searchRadius should be between 0 and 64.");
  }
  if (input.maxFileSizeMb !== undefined && (input.maxFileSizeMb < 1 || input.maxFileSizeMb > 100)) {
    warnings.push("maxFileSizeMb should be between 1 and 100.");
  }
  return warnings;
}
