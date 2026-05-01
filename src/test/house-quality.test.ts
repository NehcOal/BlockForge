import { describe, expect, it } from "vitest";
import { analyzeHouseQuality, estimateHouseMaterials, generateHousePlan } from "@/lib/house";

describe("house quality", () => {
  it("scores generated houses from 0 to 100", () => {
    const report = analyzeHouseQuality(generateHousePlan({ style: "starter_cottage" }));

    expect(report.total).toBeGreaterThanOrEqual(0);
    expect(report.total).toBeLessThanOrEqual(100);
    expect(report.entrance).toBeGreaterThanOrEqual(80);
    expect(report.roof).toBeGreaterThanOrEqual(80);
  });

  it("estimates material counts", () => {
    const estimate = estimateHouseMaterials(generateHousePlan({ style: "farmhouse" }));

    expect(estimate.length).toBeGreaterThan(0);
    expect(estimate.every((item) => item.count > 0)).toBe(true);
    expect(estimate.map((item) => item.block)).toContain("cobblestone");
  });
});
