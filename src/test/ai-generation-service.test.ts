import { describe, expect, it, vi } from "vitest";
import { generateBlueprint, getAiStatus } from "@/lib/ai";
import { createValidStructurePlan } from "./ai-fixtures";

describe("ai generation service", () => {
  it("disables OpenAI provider without an API key", () => {
    expect(getAiStatus("").openaiConfigured).toBe(false);
  });

  it("generates through OpenAI provider with a mocked fetch", async () => {
    const fetchImpl = vi.fn(async () => {
      return new Response(
        JSON.stringify({
          output_text: JSON.stringify(createValidStructurePlan())
        }),
        { status: 200, headers: { "Content-Type": "application/json" } }
      );
    });

    const result = await generateBlueprint(
      {
        prompt: "make a compact stone tower",
        provider: "openai",
        maxBlocks: 1000
      },
      {
        openAiApiKey: "test-key",
        fetchImpl: fetchImpl as unknown as typeof fetch
      }
    );

    expect(fetchImpl).toHaveBeenCalledTimes(1);
    expect(result.provider).toBe("openai");
    expect(result.structurePlan?.schemaVersion).toBe(1);
    expect(result.blueprintV2.schemaVersion).toBe(2);
  });
});
