import { describe, expect, it } from "vitest";
import { getAiFriendlyErrorMessage, mapUnknownAiError } from "@/lib/ai";

describe("AI error messages", () => {
  it("returns user-friendly messages for known errors", () => {
    expect(getAiFriendlyErrorMessage("missing-api-key")).toContain("OPENAI_API_KEY");
    expect(getAiFriendlyErrorMessage("validation-failed")).not.toContain("stack");
  });

  it("maps raw provider errors to friendly errors", () => {
    const error = mapUnknownAiError(new Error("AI structure plan failed validation: elements: bad"));
    expect(error.code).toBe("validation-failed");
    expect(error.message).toContain("validation");
    expect(error.developerDetails).toContain("elements");
  });
});
