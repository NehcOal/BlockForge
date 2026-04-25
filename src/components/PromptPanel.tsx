import type { AppCopy } from "@/lib/i18n";

type PromptPanelProps = {
  copy: AppCopy["prompt"];
  generatedPrompt: string;
  prompt: string;
  selectedPresetLabel: string;
  onGenerate: () => void;
  onPromptChange: (prompt: string) => void;
};

export function PromptPanel({
  copy,
  generatedPrompt,
  prompt,
  selectedPresetLabel,
  onGenerate,
  onPromptChange
}: PromptPanelProps) {
  return (
    <section className="forge-panel p-5">
      <div className="mb-4">
        <h2 className="flex items-center gap-2 text-lg font-extrabold text-forge">
          <span className="h-4 w-4 rounded-sm bg-forge/90 shadow-[0_0_18px_rgba(217,164,65,0.3)]" />
          {copy.title}
        </h2>
        <p className="mt-2 text-sm leading-6 text-stone-400">
          {copy.description}
        </p>
      </div>

      <label className="block text-sm font-bold text-stone-300" htmlFor="prompt">
        {copy.label}
      </label>
      <textarea
        className="forge-input mt-2 min-h-28 w-full resize-none px-4 py-3 text-sm leading-6 placeholder:text-stone-600"
        id="prompt"
        onChange={(event) => onPromptChange(event.target.value)}
        placeholder={copy.placeholder}
        value={prompt}
      />

      <button
        className="forge-primary-button mt-4 w-full px-4 py-3 text-sm"
        onClick={onGenerate}
        type="button"
      >
        {copy.button}
      </button>

      <div className="mt-4 rounded-md border border-forge/15 bg-black/25 px-4 py-3 text-sm text-stone-400">
        {generatedPrompt ? (
          <span>
            {copy.generatedPrefix}{" "}
            <span className="text-stone-100">{generatedPrompt}</span>
          </span>
        ) : (
          <span>
            {copy.emptyPrefix}{" "}
            <span className="text-stone-100">{selectedPresetLabel}</span>
          </span>
        )}
      </div>
    </section>
  );
}
