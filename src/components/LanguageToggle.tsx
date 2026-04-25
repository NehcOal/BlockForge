import type { AppCopy, Locale } from "@/lib/i18n";

type LanguageToggleProps = {
  copy: AppCopy["language"];
  locale: Locale;
  onChange: (locale: Locale) => void;
};

export function LanguageToggle({ copy, locale, onChange }: LanguageToggleProps) {
  return (
    <div
      aria-label={copy.label}
      className="inline-flex rounded-md border border-forge/35 bg-black/35 p-1 shadow-[0_0_24px_rgba(0,0,0,0.35)]"
      role="group"
    >
      <button
        aria-pressed={locale === "en"}
        className={`rounded px-3 py-2 text-xs font-black transition ${
          locale === "en"
            ? "bg-forge text-stone-950 shadow-[0_0_18px_rgba(217,164,65,0.2)]"
            : "text-stone-400 hover:text-stone-100"
        }`}
        onClick={() => onChange("en")}
        type="button"
      >
        {copy.english}
      </button>
      <button
        aria-pressed={locale === "zh"}
        className={`rounded px-3 py-2 text-xs font-black transition ${
          locale === "zh"
            ? "bg-forge text-stone-950 shadow-[0_0_18px_rgba(217,164,65,0.2)]"
            : "text-stone-400 hover:text-stone-100"
        }`}
        onClick={() => onChange("zh")}
        type="button"
      >
        {copy.chinese}
      </button>
    </div>
  );
}
