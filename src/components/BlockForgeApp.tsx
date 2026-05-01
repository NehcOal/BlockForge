"use client";

import { useState } from "react";
import { ExportPanel } from "@/components/ExportPanel";
import { Hero } from "@/components/Hero";
import { HouseDesignerPanel } from "@/components/HouseDesignerPanel";
import { LanguageToggle } from "@/components/LanguageToggle";
import { PresetSelector } from "@/components/PresetSelector";
import { PreviewPanel } from "@/components/PreviewPanel";
import { AiGenerationPanel } from "@/components/AiGenerationPanel";
import { WorkbenchShell } from "@/components/workbench/WorkbenchShell";
import { createWorkbenchStatus } from "@/lib/workbench/status";
import { appCopy, getPresetCopy, type Locale } from "@/lib/i18n";
import { generateVoxelModelFromPrompt, getAllPresets, getPresetById } from "@/lib/voxel";
import type { PresetId } from "@/types/blueprint";
import type { VoxelModel } from "@/types/blueprint";

const presets = getAllPresets();

export function BlockForgeApp() {
  const [locale, setLocale] = useState<Locale>("zh");
  const [prompt, setPrompt] = useState("");
  const [generatedPrompt, setGeneratedPrompt] = useState("");
  const [generatedModel, setGeneratedModel] = useState<VoxelModel | null>(null);
  const [selectedPresetId, setSelectedPresetId] =
    useState<PresetId>("medieval-tower");

  const copy = appCopy[locale];
  const presetModel = getPresetById(selectedPresetId);
  const selectedModel = generatedModel ?? presetModel;
  const selectedPresetCopy = generatedModel
    ? {
        name: generatedModel.name,
        description: generatedModel.description
      }
    : getPresetCopy(locale, selectedPresetId);
  const workbenchStatus = createWorkbenchStatus({
    activeBlueprintId: selectedModel.id,
    activeSource: generatedModel ? "generated" : "preset",
    validationStatus: "valid",
    warningCount: 0,
    errorCount: 0
  });

  function handleGenerate() {
    const nextPrompt = prompt.trim();
    setGeneratedPrompt(nextPrompt || selectedPresetCopy.name);
    setGeneratedModel(generateVoxelModelFromPrompt(nextPrompt || selectedPresetCopy.name).model);
  }

  function handleExternalGenerated(model: VoxelModel, sourcePrompt: string) {
    setGeneratedPrompt(sourcePrompt);
    setGeneratedModel(model);
  }

  function handlePresetSelect(presetId: PresetId) {
    setSelectedPresetId(presetId);
    setGeneratedModel(null);
  }

  return (
    <main className="forge-page text-stone-100">
      <div className="forge-bg-blocks" />

      <div className="relative z-10 mx-auto flex min-h-screen w-full max-w-[1580px] flex-col px-5 py-6 sm:px-8 lg:px-10 xl:px-14">
        <div className="mb-4 flex justify-end lg:mb-2">
          <LanguageToggle
            copy={copy.language}
            locale={locale}
            onChange={setLocale}
          />
        </div>
        <Hero copy={copy.hero} />

        <WorkbenchShell activeSection="Preview" status={workbenchStatus}>
          <div className="grid gap-6 lg:grid-cols-[430px_minmax(0,1fr)] lg:items-start xl:gap-7">
            <aside className="space-y-4">
              <AiGenerationPanel
                copy={copy.prompt}
                generatedPrompt={generatedPrompt}
                generatedModelLabel={generatedModel?.name}
                prompt={prompt}
                selectedPresetLabel={selectedPresetCopy.name}
                onExternalGenerated={handleExternalGenerated}
                onGenerateLocal={handleGenerate}
                onPromptChange={setPrompt}
              />
              <PresetSelector
                copy={copy.presets}
                locale={locale}
                presets={presets}
                selectedPresetId={selectedPresetId}
                onSelect={handlePresetSelect}
              />
              <HouseDesignerPanel onGenerated={handleExternalGenerated} />
              <ExportPanel copy={copy.export} model={selectedModel} />
            </aside>

            <PreviewPanel
              copy={copy.preview}
              generatedPrompt={generatedPrompt}
              model={selectedModel}
              prompt={prompt}
              presetCopy={selectedPresetCopy}
            />
          </div>
        </WorkbenchShell>

        <footer className="mt-auto border-t border-forge/15 py-5 text-sm text-stone-400">
          <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
            <p>{copy.footer.status}</p>
            <p className="text-stone-500">{copy.footer.roadmap}</p>
          </div>
        </footer>
      </div>
    </main>
  );
}
