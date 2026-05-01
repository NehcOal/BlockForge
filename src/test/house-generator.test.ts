import { describe, expect, it } from "vitest";
import { generateHousePlan, houseStyles } from "@/lib/house";

describe("house generator", () => {
  it("generates every alpha house preset", () => {
    for (const style of houseStyles) {
      const plan = generateHousePlan({ style });

      expect(plan.housePlanId).toBe(`house-${style}`);
      expect(plan.modules.length).toBeGreaterThan(0);
      expect(plan.footprint.width).toBeGreaterThan(0);
      expect(plan.footprint.depth).toBeGreaterThan(0);
      expect(plan.issues.filter((issue) => issue.severity === "error")).toEqual([]);
    }
  });

  it("adds door, window, and roof modules for starter cottage", () => {
    const plan = generateHousePlan({ style: "starter_cottage" });

    expect(plan.modules.some((module) => module.type === "door")).toBe(true);
    expect(plan.modules.some((module) => module.type === "window")).toBe(true);
    expect(plan.modules.some((module) => module.type === "roof")).toBe(true);
  });

  it("clamps oversized dimensions to alpha limits", () => {
    const plan = generateHousePlan({
      style: "farmhouse",
      width: 100,
      depth: 100,
      floors: 20
    });

    expect(plan.footprint.width).toBe(32);
    expect(plan.footprint.depth).toBe(32);
    expect(plan.dimensions.floors).toBe(4);
  });
});
