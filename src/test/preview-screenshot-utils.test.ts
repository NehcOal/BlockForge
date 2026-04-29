import { describe, expect, it } from "vitest";
import { createPreviewScreenshotFileName } from "@/lib/voxel";

describe("preview screenshot helpers", () => {
  it("creates safe PNG filenames", () => {
    expect(createPreviewScreenshotFileName("starter/tower")).toBe("blockforge-preview-starter-tower.png");
    expect(createPreviewScreenshotFileName("")).toBe("blockforge-preview-voxel-model.png");
  });
});
