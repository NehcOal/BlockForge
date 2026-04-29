import { useMemo, useRef, useState } from "react";
import {
  canvasToPngBlob,
  createPreviewScreenshotFileName,
  estimateDrawGroups,
  getUsedBlockTypes,
  resolveRenderMode,
  type RenderMode
} from "@/lib/voxel";
import { VoxelPreview3D } from "@/components/VoxelPreview3D";
import type { AppCopy, PresetCopy } from "@/lib/i18n";
import type { VoxelModel } from "@/types/blueprint";

type PreviewPanelProps = {
  copy: AppCopy["preview"];
  generatedPrompt: string;
  model: VoxelModel;
  prompt: string;
  presetCopy: PresetCopy;
};

export function PreviewPanel({
  copy,
  generatedPrompt,
  model,
  prompt,
  presetCopy
}: PreviewPanelProps) {
  const previewPrompt =
    generatedPrompt ||
    prompt.trim() ||
    copy.fallbackPrompt;
  const usedBlockTypes = getUsedBlockTypes(model);
  const [renderMode, setRenderMode] = useState<RenderMode>("auto");
  const [screenshotError, setScreenshotError] = useState("");
  const [screenshotStatus, setScreenshotStatus] = useState("");
  const [isExportingScreenshot, setIsExportingScreenshot] = useState(false);
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const resolvedRenderMode = resolveRenderMode(model.blocks.length, renderMode);
  const drawGroups = useMemo(() => estimateDrawGroups(model, renderMode), [model, renderMode]);

  async function exportPreviewPng() {
    setScreenshotError("");
    setScreenshotStatus("");
    const canvas = canvasRef.current;
    if (!canvas) {
      setScreenshotError("Preview canvas is not ready yet.");
      return;
    }
    setIsExportingScreenshot(true);
    try {
      await new Promise((resolve) => requestAnimationFrame(resolve));
      const blob = await canvasToPngBlob(canvas);
      const url = URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = createPreviewScreenshotFileName(model.id);
      link.click();
      URL.revokeObjectURL(url);
      setScreenshotStatus("Preview PNG exported.");
    } catch (error) {
      setScreenshotError(error instanceof Error ? error.message : "Preview PNG export failed.");
    } finally {
      setIsExportingScreenshot(false);
    }
  }

  return (
    <section className="forge-panel relative min-h-[660px] overflow-hidden p-4 sm:p-5">
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_45%_35%,rgba(217,164,65,0.13),transparent_34%)]" />

      <div className="relative flex h-full min-h-[640px] flex-col">
        <div className="relative overflow-hidden rounded-md border border-forge/45 bg-black/40">
          <div className="absolute left-5 top-5 z-10 max-w-md">
            <p className="text-xs font-extrabold uppercase tracking-[0.22em] text-forge">
              {copy.eyebrow}
            </p>
            <h2 className="mt-2 text-3xl font-black text-stone-100">
              {presetCopy.name}
            </h2>
            <p className="mt-2 max-w-2xl text-sm leading-6 text-stone-400">
              {presetCopy.description}
            </p>
            <p className="mt-2 max-w-2xl text-xs leading-5 text-stone-500">
              {copy.promptLabel} {previewPrompt}
            </p>
          </div>

          <div className="absolute right-5 top-5 z-10 rounded-md border border-forge/20 bg-black/45 px-3 py-2 text-xs text-stone-400 backdrop-blur-sm">
            {copy.controls}
          </div>

          <div className="absolute bottom-5 right-5 z-10 flex max-w-xs flex-col gap-2 rounded-md border border-forge/20 bg-black/55 p-3 text-xs text-stone-300 backdrop-blur-sm">
            <label className="grid gap-1">
              <span className="font-semibold text-stone-200">Render mode</span>
              <select
                className="rounded border border-forge/25 bg-stone-950 px-2 py-1 text-stone-100"
                onChange={(event) => setRenderMode(event.target.value as RenderMode)}
                value={renderMode}
              >
                <option value="auto">Auto</option>
                <option value="mesh">Mesh</option>
                <option value="instanced">Instanced</option>
              </select>
            </label>
            <button
              className="forge-secondary-button px-3 py-2 disabled:cursor-not-allowed disabled:opacity-50"
              disabled={isExportingScreenshot}
              onClick={() => void exportPreviewPng()}
              type="button"
            >
              {isExportingScreenshot ? "Exporting PNG..." : "Export Preview PNG"}
            </button>
            {screenshotError ? <p className="text-red-200">{screenshotError}</p> : null}
            {screenshotStatus ? <p className="text-emerald-200">{screenshotStatus}</p> : null}
          </div>

          <div className="pt-28 sm:pt-24">
            <VoxelPreview3D
              emptyMessage={copy.empty}
              model={model}
              onCanvasReady={(canvas) => {
                canvasRef.current = canvas;
              }}
              renderMode={renderMode}
            />
          </div>
        </div>

        <div className="mt-4 grid gap-4 text-sm sm:grid-cols-3">
          <div className="forge-panel-muted flex items-center gap-4 p-5">
            <div className="forge-mini-thumb h-10 w-10 shrink-0" />
            <div>
            <p className="text-stone-500">{copy.modelSize}</p>
            <p className="mt-1 text-xl font-semibold text-stone-100">
              {model.size.width} x {model.size.height} x {model.size.depth}
            </p>
            </div>
          </div>
          <div className="forge-panel-muted flex items-center gap-4 p-5">
            <div className="forge-mini-thumb h-10 w-10 shrink-0" />
            <div>
            <p className="text-stone-500">{copy.modelBlocks}</p>
            <p className="mt-1 text-xl font-semibold text-stone-100">
              {model.blocks.length.toLocaleString()}
            </p>
            </div>
          </div>
          <div className="forge-panel-muted flex items-center gap-4 p-5">
            <div className="h-10 w-10 shrink-0 rounded-full border border-forge/45 bg-black/40 shadow-[inset_0_0_20px_rgba(217,164,65,0.14)]" />
            <div>
            <p className="text-stone-500">{copy.promptState}</p>
            <p className="mt-1 text-xl font-semibold text-stone-100">
              {generatedPrompt ? copy.generated : copy.draft}
            </p>
            </div>
          </div>
        </div>

        <div className="forge-panel-muted mt-4 p-5 text-sm">
          <p className="text-stone-500">{copy.blockTypes}</p>
          <div className="mt-3 flex flex-wrap gap-2">
            {usedBlockTypes.map((blockType) => (
              <span
                className="rounded border border-forge/20 bg-black/35 px-3 py-2 text-xs font-semibold text-stone-200"
                key={blockType}
              >
                {blockType}
              </span>
            ))}
          </div>
        </div>

        <details className="forge-panel-muted mt-4 p-5 text-sm text-stone-300">
          <summary className="cursor-pointer font-semibold text-stone-100">Rendering stats</summary>
          <div className="mt-3 grid gap-2 sm:grid-cols-4">
            <p>Blocks: {model.blocks.length.toLocaleString()}</p>
            <p>Types: {usedBlockTypes.length}</p>
            <p>Mode: {renderMode} ({resolvedRenderMode})</p>
            <p>Draw groups: {drawGroups.toLocaleString()}</p>
          </div>
        </details>
      </div>
    </section>
  );
}
