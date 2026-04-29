import { describe, expect, it } from "vitest";
import { createGalleryBundleFileName, exportBlueprintGalleryBundle } from "@/lib/gallery/galleryExport";

describe("blueprint gallery export", () => {
  it("exports a real zip gallery bundle and safe filename", async () => {
    const payload = await exportBlueprintGalleryBundle([], "2026-01-01T00:00:00.000Z");
    const bytes = payload;
    expect(String.fromCharCode(bytes[0], bytes[1])).toBe("PK");
    expect(createGalleryBundleFileName("My Gallery!")).toBe("my-gallery.blockforgegallery.zip");
  });
});
