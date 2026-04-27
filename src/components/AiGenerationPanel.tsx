"use client";

import { useEffect, useMemo, useState } from "react";
import type { AppCopy } from "@/lib/i18n";
import type { GenerateBlueprintResult } from "@/lib/ai";
import type { VoxelModel } from "@/types/blueprint";

type AiStatus = {
  openaiConfigured: boolean;
  providers: string[];
  defaultProvider: string;
};

type AiGenerationPanelProps = {
  copy: AppCopy["prompt"];
  generatedPrompt: string;
  generatedModelLabel?: string;
  prompt: string;
  selectedPresetLabel: string;
  onGenerateLocal: () => void;
  onPromptChange: (prompt: string) => void;
  onExternalGenerated: (model: VoxelModel, prompt: string) => void;
};

type ExternalState = "loading" | "not-configured" | "ready" | "generating" | "success" | "error";

export function AiGenerationPanel({
  copy,
  generatedPrompt,
  generatedModelLabel,
  prompt,
  selectedPresetLabel,
  onGenerateLocal,
  onPromptChange,
  onExternalGenerated
}: AiGenerationPanelProps) {
  const [status, setStatus] = useState<AiStatus | null>(null);
  const [externalState, setExternalState] = useState<ExternalState>("loading");
  const [externalError, setExternalError] = useState("");
  const [planSummary, setPlanSummary] = useState("");

  useEffect(() => {
    let mounted = true;
    fetch("/api/ai/status")
      .then((response) => response.json() as Promise<AiStatus>)
      .then((nextStatus) => {
        if (!mounted) return;
        setStatus(nextStatus);
        setExternalState(nextStatus.openaiConfigured ? "ready" : "not-configured");
      })
      .catch(() => {
        if (!mounted) return;
        setExternalState("error");
        setExternalError("Unable to read AI provider status.");
      });
    return () => {
      mounted = false;
    };
  }, []);

  const statusLabel = useMemo(() => {
    if (externalState === "loading") return "Checking provider";
    if (externalState === "not-configured") return "Not configured";
    if (externalState === "generating") return "Generating";
    if (externalState === "success") return "Ready";
    if (externalState === "error") return "Error";
    return "Ready";
  }, [externalState]);

  async function handleExternalGenerate() {
    const nextPrompt = prompt.trim();
    if (!nextPrompt) {
      setExternalState("error");
      setExternalError("Enter a prompt before using External AI Generation.");
      return;
    }

    setExternalState("generating");
    setExternalError("");
    setPlanSummary("");

    try {
      const response = await fetch("/api/ai/generate-blueprint", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          prompt: nextPrompt,
          provider: "openai",
          maxBlocks: 2000
        })
      });
      const payload = await response.json() as {
        result?: GenerateBlueprintResult;
        error?: string;
        details?: string[];
      };

      if (!response.ok || !payload.result) {
        throw new Error(payload.details?.join(" ") ?? payload.error ?? "AI generation failed.");
      }

      const result = payload.result;
      const structurePlan = result.structurePlan;
      setPlanSummary(
        structurePlan
          ? `${structurePlan.name} · ${structurePlan.intent} · ${structurePlan.elements.length} elements`
          : `${result.model.name} · ${result.model.blocks.length} blocks`
      );
      onExternalGenerated(result.model, nextPrompt);
      setExternalState("success");
    } catch (error) {
      setExternalState("error");
      setExternalError(error instanceof Error ? error.message : "AI generation failed.");
    }
  }

  const externalDisabled = externalState === "loading" || externalState === "not-configured" || externalState === "generating";

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

      <div className="mt-4 rounded-md border border-forge/15 bg-black/20 p-4">
        <div className="flex items-start justify-between gap-3">
          <div>
            <h3 className="text-sm font-bold text-stone-100">Local Rule Generator</h3>
            <p className="mt-1 text-xs leading-5 text-stone-500">
              No API call, rule-based. Local Rule Generator does not send prompts anywhere.
            </p>
          </div>
          <span className="rounded-sm border border-emerald-400/30 bg-emerald-400/10 px-2 py-1 text-[11px] font-bold uppercase tracking-wide text-emerald-200">
            Local
          </span>
        </div>
        <button
          className="forge-primary-button mt-3 w-full px-4 py-3 text-sm"
          onClick={onGenerateLocal}
          type="button"
        >
          {copy.button}
        </button>
      </div>

      <div className="mt-4 rounded-md border border-sky-400/20 bg-sky-950/20 p-4">
        <div className="flex items-start justify-between gap-3">
          <div>
            <h3 className="text-sm font-bold text-stone-100">External AI Generation Alpha</h3>
            <p className="mt-1 text-xs leading-5 text-stone-500">
              Provider: OpenAI · Status: <span className="text-stone-300">{statusLabel}</span>
            </p>
          </div>
          <span className="rounded-sm border border-sky-300/30 bg-sky-400/10 px-2 py-1 text-[11px] font-bold uppercase tracking-wide text-sky-200">
            Alpha
          </span>
        </div>

        <div className="mt-3 space-y-1 text-xs leading-5 text-stone-500">
          <p>External AI requests may send your prompt to the selected AI provider.</p>
          <p>API usage may incur cost. Your API key must be configured server-side.</p>
          <p>外部 AI 请求会把提示词发送给所选 AI 服务；API 使用可能产生费用；API Key 必须配置在服务端。</p>
        </div>

        <button
          className="forge-secondary-button mt-3 w-full px-4 py-3 text-sm disabled:cursor-not-allowed disabled:opacity-50"
          disabled={externalDisabled}
          onClick={handleExternalGenerate}
          type="button"
        >
          {externalState === "generating" ? "Generating with AI..." : "Generate with AI"}
        </button>

        {externalState === "not-configured" ? (
          <p className="mt-3 rounded-md border border-amber-400/25 bg-amber-400/10 px-3 py-2 text-xs leading-5 text-amber-100">
            OpenAI provider is disabled until OPENAI_API_KEY is configured on the server.
          </p>
        ) : null}
        {externalState === "error" && externalError ? (
          <p className="mt-3 rounded-md border border-red-400/25 bg-red-400/10 px-3 py-2 text-xs leading-5 text-red-100">
            {externalError}
          </p>
        ) : null}
        {externalState === "success" && planSummary ? (
          <p className="mt-3 rounded-md border border-emerald-400/25 bg-emerald-400/10 px-3 py-2 text-xs leading-5 text-emerald-100">
            Structure plan validated: {planSummary}
          </p>
        ) : null}
      </div>

      <div className="mt-4 rounded-md border border-forge/15 bg-black/25 px-4 py-3 text-sm text-stone-400">
        {generatedModelLabel ? (
          <span>
            {copy.generatedModelPrefix}{" "}
            <span className="text-stone-100">{generatedModelLabel}</span>
          </span>
        ) : generatedPrompt ? (
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

      <p className="mt-3 text-xs leading-5 text-stone-500">{copy.generatorHint}</p>
      <p className="mt-2 text-xs leading-5 text-stone-500">
        External AI API adapter is alpha. Local Rule Generator remains the default fallback.
        {status?.providers?.length ? ` Providers: ${status.providers.join(", ")}.` : ""}
      </p>
    </section>
  );
}
