import { useRef, useState } from "react";
import {
  createBlueprintJsonFileName,
  createBlueprintV2JsonFileName,
  createSafeFileName,
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

type ExportPanelProps = {
  copy: AppCopy["export"];
  model: VoxelModel;
};

export function ExportPanel({ copy, model }: ExportPanelProps) {
  const inputRef = useRef<HTMLInputElement>(null);
  const schematicInputRef = useRef<HTMLInputElement>(null);
  const [packImportStatus, setPackImportStatus] = useState("");
  const [schematicImportStatus, setSchematicImportStatus] = useState("");

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
      setPackImportStatus(
        `${copy.importSuccess}: ${imported.manifest.name} v${imported.manifest.version} | ${imported.blueprints.length} blueprint(s): ${imported.blueprints.map((blueprint) => blueprint.registryId).join(", ")}`
      );
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      setPackImportStatus(`${copy.importError}: ${message}`);
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
      setSchematicImportStatus(
        `${copy.schematicImportSuccess}: ${imported.blueprint.name} | ${imported.blueprint.size.width}x${imported.blueprint.size.height}x${imported.blueprint.size.depth} | palette=${Object.keys(imported.blueprint.palette).length} | blocks=${imported.blueprint.blocks.length}${imported.warnings.length ? ` | warnings=${imported.warnings.join("; ")}` : ""}`
      );
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      setSchematicImportStatus(`${copy.schematicImportError}: ${message}`);
    } finally {
      if (schematicInputRef.current) {
        schematicInputRef.current.value = "";
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
          <div className="grid gap-2">
            <p className="text-xs font-bold uppercase tracking-[0.18em] text-stone-500">
              Data
            </p>
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
          <div className="grid gap-2">
            <p className="text-xs font-bold uppercase tracking-[0.18em] text-stone-500">
              Commands
            </p>
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
        {packImportStatus ? (
          <p className="rounded border border-forge/30 bg-black/20 px-3 py-2 text-xs leading-5 text-stone-300">
            {packImportStatus}
          </p>
        ) : null}
        {schematicImportStatus ? (
          <p className="rounded border border-forge/30 bg-black/20 px-3 py-2 text-xs leading-5 text-stone-300">
            {schematicImportStatus}
          </p>
        ) : null}
        <p className="text-xs leading-5 text-stone-500">
          {copy.datapackHint}
        </p>
      </div>
    </section>
  );
}
