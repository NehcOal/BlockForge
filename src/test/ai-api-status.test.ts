import { describe, expect, it } from "vitest";
import { GET } from "@/app/api/ai/status/route";

describe("AI status route", () => {
  it("returns provider status without exposing API keys", async () => {
    const response = GET();
    const body = await response.json();
    expect(body.providers).toContain("local-rule");
    expect(body.providers).toContain("openai");
    expect(JSON.stringify(body)).not.toContain("OPENAI_API_KEY");
    expect(JSON.stringify(body)).not.toContain("sk-");
  });
});
