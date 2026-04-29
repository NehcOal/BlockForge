export type AiErrorCode =
  | "missing-api-key"
  | "provider-unavailable"
  | "prompt-too-long"
  | "invalid-structure-plan"
  | "estimated-blocks-exceeded"
  | "network-error"
  | "unknown-server-error"
  | "server-runtime-required"
  | "structured-output-parse-failed"
  | "validation-failed";

export type AiFriendlyError = {
  code: AiErrorCode;
  message: string;
  developerDetails?: string;
};

const aiErrorMessages: Record<AiErrorCode, string> = {
  "missing-api-key": "External AI is not configured. Add OPENAI_API_KEY on the server.",
  "provider-unavailable": "The selected AI provider is unavailable. You can still use Local Rule Generator.",
  "prompt-too-long": "Prompt is too long. Shorten it and try again.",
  "invalid-structure-plan": "The AI returned a structure plan BlockForge could not use.",
  "estimated-blocks-exceeded": "The generated plan is too large for the current maxBlocks limit.",
  "network-error": "Network request failed. Check the server connection and try again.",
  "unknown-server-error": "AI generation failed on the server.",
  "server-runtime-required": "External AI requires a server runtime.",
  "structured-output-parse-failed": "The AI response could not be parsed as a Structure Plan.",
  "validation-failed": "Generated structure failed validation and was not loaded into preview."
};

export function getAiFriendlyErrorMessage(code: AiErrorCode): string {
  return aiErrorMessages[code];
}

export function createAiFriendlyError(
  code: AiErrorCode,
  developerDetails?: string
): AiFriendlyError {
  return {
    code,
    message: getAiFriendlyErrorMessage(code),
    developerDetails
  };
}

export function mapUnknownAiError(error: unknown): AiFriendlyError {
  const details = error instanceof Error ? error.message : String(error);
  const lowered = details.toLowerCase();
  if (lowered.includes("openai provider is not configured") || lowered.includes("openai_api_key")) {
    return createAiFriendlyError("missing-api-key", details);
  }
  if (lowered.includes("prompt must be")) {
    return createAiFriendlyError("prompt-too-long", details);
  }
  if (lowered.includes("estimated") && lowered.includes("maxblocks")) {
    return createAiFriendlyError("estimated-blocks-exceeded", details);
  }
  if (lowered.includes("structure plan failed validation")) {
    return createAiFriendlyError("validation-failed", details);
  }
  if (lowered.includes("parse") || lowered.includes("json")) {
    return createAiFriendlyError("structured-output-parse-failed", details);
  }
  if (lowered.includes("fetch") || lowered.includes("network")) {
    return createAiFriendlyError("network-error", details);
  }
  return createAiFriendlyError("unknown-server-error", details);
}
