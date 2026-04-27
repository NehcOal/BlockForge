import { useRef, useState } from "react";
import {
  createBlueprintJsonFileName,
  createBlueprintV2JsonFileName,
  createImportedBlueprintAsset,
  createSafeFileName,
  createImportedPackAsset,
  createImportedSchematicAsset,
  getFunctionName,
  importBlueprintPackZip,
  importSpongeSchematicBlob,
  exportSpongeSchematicBlob,
  schematicFileName,
  voxelModelToBlueprintV2Json,
  voxelModelToBlueprintV2,
  voxelModelToBlueprintJson,
  voxelModelToMcFunction,
  voxelModelsToBlueprintPackZip,
  voxelModelToDataPackZip
} from "@/lib/voxel";
import type { AppCopy } from "@/lib/i18n";
import type { VoxelModel } from "@/types/blueprint";
import type { ImportedBlueprintAsset } from "@/lib/voxel";

type ExportPanelProps = {
  copy: AppCopy["export"];
  model: VoxelModel;
};

export function ExportPanel({ copy, model }: ExportPanelProps) {
  const inputRef = useRef<HTMLInputElement>(null);
  const schematicInputRef = useRef<HTMLInputElement>(null);
  const blueprintInputRef = useRef<HTMLInputElement>(null);
  const [importedAsset, setImportedAsset] = useState<ImportedBlueprintAsset | null>(null);
  const [importError, setImportError] = useState<{ reason: string; details: string } | null>(null);

  function downloadFile(content: string, type: string, fileName: string) {
    const blob = new Blob([content], { type });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");

    link.href = url;
    link.download = fileName;
    link.click();
    URL.revokeObjectURL(url);
  }

  function downloadBlob(blob: Blob, fileName: string) {
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");

    link.href = url;
    link.download = fileName;
    link.click();
    URL.revokeObjectURL(url);
  }

  function handleJsonExport() {
    downloadFile(
      JSON.stringify(model, null, 2),
      "application/json",
      createSafeFileName(model.name, "json")
    );
  }

  function handleBlueprintJsonExport() {
    downloadFile(
      voxelModelToBlueprintJson(model),
      "application/json",
      createBlueprintJsonFileName(model)
    );
  }

  function handleBlueprintV2JsonExport() {
    downloadFile(
      voxelModelToBlueprintV2Json(model),
      "application/json",
      createBlueprintV2JsonFileName(model)
    );
  }

  function handleMcFunctionExport() {
    downloadFile(
      voxelModelToMcFunction(model, { includeHeader: true }),
      "text/plain",
      createSafeFileName(model.name, "mcfunction")
    );
  }

  async function handleDataPackExport() {
    const blob = await voxelModelToDataPackZip(model, { includeReadme: true });
    downloadBlob(
      blob,
      `blockforge-${getFunctionName(model)}-datapack.zip`
    );
  }

  async function handleBlueprintPackExport() {
    const blob = await voxelModelsToBlueprintPackZip([model], {
      packId: `blockforge_${getFunctionName(model)}`,
      name: `${model.name} Pack`,
      version: "1.0.0",
      description: model.description,
      author: "BlockForge",
      license: "MIT",
      tags: ["blockforge", "blueprint"]
    });
    downloadBlob(blob, `blockforge-${getFunctionName(model)}.blockforgepack.zip`);
  }

  async function handleSchematicExport() {
    const blueprint = voxelModelToBlueprintV2(model);
    const blob = await exportSpongeSchematicBlob(blueprint, {
      name: model.name,
      author: "BlockForge"
    });
    downloadBlob(blob, schematicFileName(blueprint.id));
  }

  async function handleBlueprintPackImport(file: File | undefined) {
    if (!file) {
      return;
    }

    try {
      const imported = await importBlueprintPackZip(file);
      setImportedAsset({ ...createImportedPackAsset(imported), sourceFileName: file.name });
      setImportError(null);
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      setImportError(createImportError(copy.importError, message));
    } finally {
      if (inputRef.current) {
        inputRef.current.value = "";
      }
    }
  }

  async function handleSchematicImport(file: File | undefined) {
    if (!file) {
      return;
    }

    try {
      const imported = await importSpongeSchematicBlob(file);
      setImportedAsset({ ...createImportedSchematicAsset(imported), sourceFileName: file.name });
      setImportError(null);
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      setImportError(createImportError(copy.schematicImportError, message));
    } finally {
      if (schematicInputRef.current) {
        schematicInputRef.current.value = "";
      }
    }
  }

  async function handleBlueprintJsonImport(file: File | undefined) {
    if (!file) {
      return;
    }

    try {
      const blueprint = JSON.parse(await file.text());
      setImportedAsset({ ...createImportedBlueprintAsset(blueprint, file.name.replace(/\.json$/i, "")), sourceFileName: file.name });
      setImportError(null);
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      setImportError(createImportError(copy.importError, message));
    } finally {
      if (blueprintInputRef.current) {
        blueprintInputRef.current.value = "";
      }
    }
  }

  return (
    <section className="forge-panel p-5">
      <div className="flex flex-col gap-4">
        <div>
          <h2 className="flex items-center gap-2 text-lg font-extrabold text-forge">
            <span className="h-4 w-4 border border-forge/70 bg-black/35" />
            {copy.title}
          </h2>
          <p className="mt-2 text-sm leading-6 text-stone-400">
            {copy.description}
          </p>
        </div>
        <div className="grid gap-3">
          <div className="forge-panel-muted grid gap-2 p-3">
            <PanelGroupTitle label={copy.blueprintFilesGroup} />
            <button
              className="forge-secondary-button px-4 py-3 text-sm"
              onClick={handleJsonExport}
              type="button"
            >
              {copy.json}
            </button>
            <button
              className="forge-primary-button px-4 py-3 text-sm"
              onClick={handleBlueprintJsonExport}
              type="button"
            >
              {copy.blueprintJson}
            </button>
            <button
              className="forge-primary-button px-4 py-3 text-sm"
              onClick={handleBlueprintV2JsonExport}
              type="button"
            >
              {copy.blueprintV2Json}
            </button>
            <button
              className="forge-primary-button px-4 py-3 text-sm"
              onClick={handleBlueprintPackExport}
              type="button"
            >
              {copy.blueprintPack}
            </button>
            <button
              className="forge-primary-button px-4 py-3 text-sm"
              onClick={() => void handleSchematicExport()}
              type="button"
            >
              {copy.spongeSchematic}
            </button>
          </div>
          <div className="forge-panel-muted grid gap-2 p-3">
            <PanelGroupTitle label={copy.minecraftInstallGroup} />
            <button
              className="forge-primary-button px-4 py-3 text-sm"
              onClick={handleMcFunctionExport}
              type="button"
            >
              {copy.mcfunction}
            </button>
            <button
              className="forge-primary-button px-4 py-3 text-sm"
              onClick={handleDataPackExport}
              type="button"
            >
              {copy.datapack}
            </button>
          </div>
          <div className="forge-panel-muted grid gap-2 p-3">
            <PanelGroupTitle label={copy.interopImportGroup} />
            <input
              accept=".blockforgepack.zip,.zip,application/zip"
              className="hidden"
              onChange={(event) => void handleBlueprintPackImport(event.target.files?.[0])}
              ref={inputRef}
              type="file"
            />
            <input
              accept=".schem,application/octet-stream"
              className="hidden"
              onChange={(event) => void handleSchematicImport(event.target.files?.[0])}
              ref={schematicInputRef}
              type="file"
            />
            <input
              accept=".json,application/json"
              className="hidden"
              onChange={(event) => void handleBlueprintJsonImport(event.target.files?.[0])}
              ref={blueprintInputRef}
              type="file"
            />
            <button
              className="forge-secondary-button px-4 py-3 text-sm"
              onClick={() => blueprintInputRef.current?.click()}
              type="button"
            >
              {copy.importBlueprintJson}
            </button>
            <button
              className="forge-secondary-button px-4 py-3 text-sm"
              onClick={() => inputRef.current?.click()}
              type="button"
            >
              {copy.importBlueprintPack}
            </button>
            <button
              className="forge-secondary-button px-4 py-3 text-sm"
              onClick={() => schematicInputRef.current?.click()}
              type="button"
            >
              {copy.importSpongeSchematic}
            </button>
          </div>
        </div>
        <p className="text-xs leading-5 text-stone-500">
          {copy.hint}
        </p>
        <p className="text-xs leading-5 text-stone-500">
          {copy.blueprintHint}
        </p>
        <p className="text-xs leading-5 text-stone-500">
          {copy.blueprintV2Hint}
        </p>
        <p className="text-xs leading-5 text-stone-500">
          {copy.blueprintPackHint}
        </p>
        <p className="text-xs leading-5 text-stone-500">
          {copy.schematicHint}
        </p>
        {importedAsset ? (
          <ImportSummaryCard
            asset={importedAsset}
            copy={copy}
          />
        ) : null}
        {importError ? (
          <details className="rounded border border-red-500/40 bg-red-950/20 px-3 py-2 text-xs leading-5 text-red-100">
            <summary className="cursor-pointer font-bold">{importError.reason}</summary>
            <p className="mt-2 text-red-100/85">{importError.details}</p>
          </details>
        ) : null}
        <p className="text-xs leading-5 text-stone-500">
          {copy.datapackHint}
        </p>
      </div>
    </section>
  );
}

function createImportError(prefix: string, details: string): { reason: string; details: string } {
  const reason = details.toLowerCase().includes("json")
    ? "The selected file is not valid Blueprint JSON."
    : details.toLowerCase().includes("schem")
      ? "The selected schematic could not be parsed."
      : "The selected file could not be imported.";
  return {
    reason: `${prefix}: ${reason}`,
    details
  };
}

function PanelGroupTitle({ label }: { label: string }) {
  return (
    <p className="text-xs font-bold uppercase tracking-[0.18em] text-stone-500">
      {label}
    </p>
  );
}

function ImportSummaryCard({
  asset,
  copy
}: {
  asset: ImportedBlueprintAsset;
  copy: AppCopy["export"];
}) {
  const firstBlueprint = asset.blueprints[0];
  const issueCounts = countImportSummaryIssues(asset);
  return (
    <details className="rounded border border-forge/30 bg-black/20 px-3 py-3 text-xs text-stone-300" open>
      <summary className="cursor-pointer font-bold text-stone-100">{asset.name}</summary>
      <div className="flex flex-col gap-1 sm:flex-row sm:items-center sm:justify-between">
        <p className="uppercase tracking-[0.16em] text-forge">{asset.sourceType}</p>
        <p className={issueCounts.errors > 0 ? "text-red-200" : issueCounts.warnings > 0 ? "text-amber-200" : "text-emerald-200"}>
          {issueCounts.errors > 0 ? "error" : issueCounts.warnings > 0 ? "warning" : "success"}
        </p>
      </div>
      <div className="mt-3 grid gap-2 sm:grid-cols-4">
        <SummaryMetric label="Blueprints" value={asset.summary.blueprintCount.toString()} />
        <SummaryMetric label={copy.validationLabel} value={asset.summary.validationSummary} />
        <SummaryMetric label={copy.errorsLabel} value={issueCounts.errors.toString()} tone={issueCounts.errors > 0 ? "error" : "default"} />
        <SummaryMetric label={copy.warningsLabel} value={issueCounts.warnings.toString()} tone={issueCounts.warnings > 0 ? "warning" : "default"} />
      </div>
      {firstBlueprint ? (
        <div className="mt-3 leading-5 text-stone-400">
          <p>{firstBlueprint.id} | {firstBlueprint.size.width}x{firstBlueprint.size.height}x{firstBlueprint.size.depth} | palette={firstBlueprint.paletteCount} | blocks={firstBlueprint.blockCount}</p>
          {asset.sourceFileName ? <p>source file: {asset.sourceFileName}</p> : null}
          <ValidationReport issues={firstBlueprint.validation.issues} />
        </div>
      ) : null}
    </details>
  );
}

function ValidationReport({ issues }: { issues: ImportedBlueprintAsset["blueprints"][number]["validation"]["issues"] }) {
  if (issues.length === 0) {
    return <p className="mt-2 text-emerald-200">Validation report: no issues.</p>;
  }
  const sections = Array.from(new Set(issues.map((issue) => issue.section)));
  return (
    <details className="mt-2 rounded border border-forge/10 bg-black/20 p-2">
      <summary className="cursor-pointer text-stone-200">Validation report</summary>
      <div className="mt-2 grid gap-2">
        {sections.map((section) => (
          <div key={section}>
            <p className="font-bold text-stone-200">{section}</p>
            {issues.filter((issue) => issue.section === section).map((issue) => (
              <p className={issue.severity === "error" ? "text-red-200" : issue.severity === "warning" ? "text-amber-200" : "text-stone-400"} key={`${issue.field}-${issue.message}`}>
                {issue.severity}: {issue.field} - {issue.message}{issue.suggestion ? ` Suggestion: ${issue.suggestion}` : ""}
              </p>
            ))}
          </div>
        ))}
      </div>
    </details>
  );
}

function SummaryMetric({
  label,
  value,
  tone = "default"
}: {
  label: string;
  value: string;
  tone?: "default" | "warning" | "error";
}) {
  const valueClass = tone === "error"
    ? "text-red-200"
    : tone === "warning"
      ? "text-amber-200"
      : "text-stone-100";
  return (
    <div className="rounded border border-forge/15 bg-black/20 px-2 py-2">
      <p className="text-[10px] uppercase tracking-[0.14em] text-stone-500">{label}</p>
      <p className={`mt-1 font-bold ${valueClass}`}>{value}</p>
    </div>
  );
}

export function countImportSummaryIssues(asset: ImportedBlueprintAsset): { errors: number; warnings: number } {
  const issues = asset.blueprints.flatMap((blueprint) => blueprint.validation.issues);
  return {
    errors: issues.filter((issue) => issue.severity === "error").length,
    warnings: asset.warnings.length + issues.filter((issue) => issue.severity === "warning").length
  };
}
