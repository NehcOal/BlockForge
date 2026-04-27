import { aiStructurePlanJsonSchema } from "@/lib/ai/structurePlanSchema";
import { validatePromptSafety } from "@/lib/ai/promptSafety";
import type { AiStructurePlan } from "@/lib/ai/structurePlan";
import type { GenerateBlueprintRequest } from "@/lib/ai/types";

export type OpenAiProviderOptions = {
  apiKey?: string;
  model?: string;
  fetchImpl?: typeof fetch;
};

type OpenAiResponseBody = {
  output_text?: string;
  output?: Array<{
    content?: Array<{
      type?: string;
      text?: string;
    }>;
  }>;
  error?: {
    message?: string;
  };
};

export function isOpenAiConfigured(apiKey = process.env.OPENAI_API_KEY): boolean {
  return typeof apiKey === "string" && apiKey.trim().length > 0;
}

export async function generateOpenAiStructurePlan(
  request: GenerateBlueprintRequest,
  options: OpenAiProviderOptions = {}
): Promise<AiStructurePlan> {
  const apiKey = options.apiKey ?? process.env.OPENAI_API_KEY;
  if (!isOpenAiConfigured(apiKey)) {
    throw new Error("OpenAI provider is not configured. Set OPENAI_API_KEY on the server.");
  }

  const safety = validatePromptSafety(request);
  if (!safety.valid) {
    throw new Error(safety.errors.join(" "));
  }

  const fetchImpl = options.fetchImpl ?? fetch;
  const response = await fetchImpl("https://api.openai.com/v1/responses", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${apiKey}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      model: options.model ?? process.env.OPENAI_MODEL ?? "gpt-4.1-mini",
      input: [
        {
          role: "system",
          content: [
            "You create BlockForge AI Structure Plan v1 JSON only.",
            "Return high-level elements, not final block lists.",
            "Use only supported palette blocks and keep allowUnsupportedBlocks false.",
            "Keep coordinates inside the declared size and estimated blocks under maxBlocks."
          ].join(" ")
        },
        {
          role: "user",
          content: buildUserPrompt(request, safety.maxBlocks)
        }
      ],
      text: {
        format: {
          type: "json_schema",
          name: "blockforge_ai_structure_plan_v1",
          schema: aiStructurePlanJsonSchema,
          strict: true
        }
      }
    })
  });

  const data = await response.json() as OpenAiResponseBody;
  if (!response.ok) {
    throw new Error(data.error?.message ?? "OpenAI request failed.");
  }

  const outputText = extractOutputText(data);
  if (!outputText) {
    throw new Error("OpenAI response did not include a structure plan.");
  }

  return JSON.parse(outputText) as AiStructurePlan;
}

export function extractOutputText(data: OpenAiResponseBody): string | undefined {
  if (typeof data.output_text === "string") {
    return data.output_text;
  }

  for (const item of data.output ?? []) {
    for (const content of item.content ?? []) {
      if (typeof content.text === "string") {
        return content.text;
      }
    }
  }

  return undefined;
}

function buildUserPrompt(request: GenerateBlueprintRequest, maxBlocks: number): string {
  return JSON.stringify({
    prompt: request.prompt.trim(),
    styleHint: request.styleHint,
    sizeHint: request.sizeHint,
    maxBlocks
  });
}
