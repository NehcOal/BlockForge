import { getUsedBlockTypes } from "@/lib/voxel";
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

          <div className="pt-28 sm:pt-24">
            <VoxelPreview3D emptyMessage={copy.empty} model={model} />
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
      </div>
    </section>
  );
}
