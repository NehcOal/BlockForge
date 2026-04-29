import { describe, expect, it } from "vitest";
import { validateStructurePlan } from "@/lib/ai";
import { createValidStructurePlan } from "./ai-fixtures";

describe("validateStructurePlan", () => {
  it("accepts a valid structure plan", () => {
    const report = validateStructurePlan(createValidStructurePlan());
    expect(report.valid).toBe(true);
    expect(report.errors).toHaveLength(0);
    expect(report.estimatedBlocks).toBeGreaterThan(0);
  });

  it("rejects a missing palette", () => {
    const plan = createValidStructurePlan();
    const report = validateStructurePlan({ ...plan, palette: undefined });
    expect(report.valid).toBe(false);
    expect(report.errors.some((error) => error.path === "palette")).toBe(true);
  });

  it("rejects an element with a missing blockKey", () => {
    const plan = createValidStructurePlan();
    plan.elements[0] = { ...plan.elements[0], blockKey: "missing" };
    const report = validateStructurePlan(plan);
    expect(report.valid).toBe(false);
    expect(report.errors.some((error) => error.path === "elements[0].blockKey")).toBe(true);
  });

  it("rejects out-of-bounds elements", () => {
    const plan = createValidStructurePlan();
    plan.elements[0] = { ...plan.elements[0], to: [99, 0, 7] };
    const report = validateStructurePlan(plan);
    expect(report.valid).toBe(false);
    expect(report.errors.some((error) => error.path === "elements[0]")).toBe(true);
  });

  it("rejects estimated blocks over maxBlocks", () => {
    const plan = createValidStructurePlan();
    plan.constraints.maxBlocks = 1;
    const report = validateStructurePlan(plan);
    expect(report.valid).toBe(false);
    expect(report.errors.some((error) => error.path === "elements")).toBe(true);
  });

  it("rejects duplicate element ids", () => {
    const plan = createValidStructurePlan();
    plan.elements.push({ ...plan.elements[0] });
    const report = validateStructurePlan(plan);
    expect(report.valid).toBe(false);
    expect(report.errors.some((error) => error.message.includes("must be unique"))).toBe(true);
  });
});
