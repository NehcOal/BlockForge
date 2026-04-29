import { NextResponse } from "next/server";
import { generateBlueprint, validatePromptSafety } from "@/lib/ai";
import type { GenerateBlueprintRequest } from "@/lib/ai";

export async function POST(request: Request) {
  try {
    const body = await request.json() as Partial<GenerateBlueprintRequest>;
    const generationRequest: GenerateBlueprintRequest = {
      prompt: String(body.prompt ?? ""),
      sizeHint: body.sizeHint,
      styleHint: body.styleHint,
      maxBlocks: body.maxBlocks,
      provider: body.provider === "openai" ? "openai" : "local-rule"
    };

    const safety = validatePromptSafety(generationRequest);
    if (!safety.valid) {
      return NextResponse.json(
        {
          error: "Prompt rejected.",
          details: safety.errors
        },
        { status: 400 }
      );
    }

    if (generationRequest.provider !== "openai") {
      return NextResponse.json(
        {
          error: "This endpoint only handles external AI generation."
        },
        { status: 400 }
      );
    }

    if (!process.env.OPENAI_API_KEY) {
      return NextResponse.json(
        {
          error: "OpenAI provider is not configured. Set OPENAI_API_KEY on the server."
        },
        { status: 503 }
      );
    }

    const result = await generateBlueprint(generationRequest, {
      openAiApiKey: process.env.OPENAI_API_KEY
    });

    return NextResponse.json({ result });
  } catch (error) {
    return NextResponse.json(
      {
        error: error instanceof Error ? error.message : "AI generation failed."
      },
      { status: 500 }
    );
  }
}
