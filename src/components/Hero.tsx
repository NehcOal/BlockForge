import type { AppCopy } from "@/lib/i18n";

type HeroProps = {
  copy: AppCopy["hero"];
};

export function Hero({ copy }: HeroProps) {
  return (
    <header className="flex flex-col gap-6 pb-5 lg:flex-row lg:items-end lg:justify-between">
      <div className="flex gap-5">
        <div className="mt-3 hidden h-14 w-14 shrink-0 sm:block">
          <div className="forge-logo-cube h-6 w-6 rotate-45 rounded-sm" />
        </div>
        <div>
          <p className="mb-2 text-sm font-bold uppercase tracking-[0.36em] text-forge">
            {copy.eyebrow}
          </p>
          <h1 className="text-5xl font-black leading-none tracking-tight text-stone-100 drop-shadow-[0_12px_42px_rgba(217,164,65,0.18)] sm:text-6xl xl:text-7xl">
            BlockForge
          </h1>
          <p className="mt-4 max-w-2xl text-lg leading-8 text-stone-200">
            {copy.subtitle}
          </p>
          <p className="mt-2 max-w-2xl text-sm leading-6 text-stone-500">
            {copy.description}
          </p>
        </div>
      </div>

      <div className="forge-panel-muted grid w-full max-w-md grid-cols-3 gap-0 p-2">
        {copy.steps.map((item) => (
          <div
            className="border-r border-forge/20 px-4 py-3 text-center text-xs font-bold text-stone-300 last:border-r-0"
            key={item}
          >
            {item}
          </div>
        ))}
      </div>
    </header>
  );
}
