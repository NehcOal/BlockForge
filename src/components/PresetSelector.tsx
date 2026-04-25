import { getPresetCopy, type AppCopy, type Locale } from "@/lib/i18n";
import type { PresetId, VoxelModel } from "@/types/blueprint";

type PresetSelectorProps = {
  copy: AppCopy["presets"];
  locale: Locale;
  presets: VoxelModel[];
  selectedPresetId: PresetId;
  onSelect: (presetId: PresetId) => void;
};

export function PresetSelector({
  copy,
  locale,
  presets,
  selectedPresetId,
  onSelect
}: PresetSelectorProps) {
  return (
    <section className="forge-panel p-5">
      <div className="mb-4">
        <h2 className="flex items-center gap-2 text-lg font-extrabold text-forge">
          <span className="h-4 w-4 border border-forge/70 bg-black/35 shadow-[inset_0_0_0_3px_rgba(217,164,65,0.16)]" />
          {copy.title}
        </h2>
        <p className="mt-2 text-sm leading-6 text-stone-400">
          {copy.description}
        </p>
      </div>

      <div className="grid gap-2">
        {presets.map((preset) => {
          const isSelected = preset.id === selectedPresetId;
          const presetCopy = getPresetCopy(locale, preset.id);

          return (
            <button
              className={`grid grid-cols-[48px_1fr_18px] items-center gap-3 rounded-md border p-2.5 text-left transition ${
                isSelected
                  ? "border-forge bg-forge/12 shadow-[inset_0_0_0_1px_rgba(217,164,65,0.28),0_0_22px_rgba(217,164,65,0.08)]"
                  : "border-stone-800/80 bg-black/25 hover:border-forge/45 hover:bg-stone-900/60"
              }`}
              key={preset.id}
              onClick={() => onSelect(preset.id)}
              type="button"
            >
              <div className="forge-mini-thumb h-12 w-12" />
              <div className="min-w-0">
                <span className="text-sm font-semibold text-stone-100">
                  {presetCopy.name}
                </span>
                <p className="mt-1 line-clamp-2 text-xs leading-5 text-stone-500">
                  {presetCopy.description}
                </p>
              </div>
              <span
                className={`h-4 w-4 rounded-sm border ${
                  isSelected
                    ? "border-forge bg-forge shadow-[0_0_12px_rgba(217,164,65,0.38)]"
                    : "border-stone-600 bg-black/30"
                }`}
              />
            </button>
          );
        })}
      </div>
    </section>
  );
}
