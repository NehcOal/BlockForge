import JSZip from "jszip";
import type { BlueprintGalleryBundle, BlueprintGalleryItem } from "@/lib/gallery/galleryTypes";

export async function exportBlueprintGalleryBundle(items: BlueprintGalleryItem[], now = new Date().toISOString()): Promise<Uint8Array> {
  const zip = new JSZip();
  const bundle: BlueprintGalleryBundle = {
    schemaVersion: 1,
    exportedAt: now,
    items
  };
  zip.file("blockforge-gallery.json", JSON.stringify(bundle, null, 2));
  zip.file("README.md", createGalleryReadme(items));
  for (const item of items) {
    zip.file(`blueprints/${item.id}.json`, JSON.stringify(item.blueprintV2, null, 2));
  }
  return zip.generateAsync({
    type: "uint8array",
    compression: "DEFLATE"
  });
}

export function createGalleryBundleFileName(name = "blockforge-gallery"): string {
  return `${name.replace(/[^a-z0-9._-]+/gi, "-").replace(/^-+|-+$/g, "").toLowerCase() || "blockforge-gallery"}.blockforgegallery.zip`;
}

function createGalleryReadme(items: BlueprintGalleryItem[]): string {
  return [
    "# BlockForge Gallery Bundle",
    "",
    "This alpha bundle was exported by BlockForge.",
    "",
    "## Items",
    "",
    ...items.map((item) => `- ${item.id}: ${item.name}`)
  ].join("\n");
}
