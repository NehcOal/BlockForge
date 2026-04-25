import {
  createBlueprintJsonFileName,
  createBlueprintV2JsonFileName,
  createSafeFileName,
  getFunctionName,
  voxelModelToBlueprintV2Json,
  voxelModelToBlueprintJson,
  voxelModelToMcFunction,
  voxelModelToDataPackZip
} from "@/lib/voxel";
import type { AppCopy } from "@/lib/i18n";
import type { VoxelModel } from "@/types/blueprint";

type ExportPanelProps = {
  copy: AppCopy["export"];
  model: VoxelModel;
};

export function ExportPanel({ copy, model }: ExportPanelProps) {
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
          {copy.datapackHint}
        </p>
      </div>
    </section>
  );
}
