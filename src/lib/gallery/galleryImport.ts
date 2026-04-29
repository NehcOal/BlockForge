import { createImportReport, type ImportReport } from "@/lib/import/importReport";
import type { BlueprintGalleryBundle, BlueprintGalleryItem } from "@/lib/gallery/galleryTypes";
import { validateBlueprintJson } from "@/lib/voxel/blueprintValidation";
import JSZip from "jszip";

export type GalleryImportResult = {
  items: BlueprintGalleryItem[];
  report: ImportReport;
};

export async function importBlueprintGalleryBundle(payload: Blob | ArrayBuffer | Uint8Array, sourceFileName = "gallery.blockforgegallery.zip"): Promise<GalleryImportResult> {
  const messages: ImportReport["messages"] = [];
  try {
    const zip = await JSZip.loadAsync(payload);
    for (const path of Object.keys(zip.files)) {
      if (!isSafeZipPath(path)) {
        messages.push({ severity: "error", path, message: "Gallery bundle contains an unsafe path." });
      }
    }
    if (messages.some((message) => message.severity === "error")) {
      return withReport([], sourceFileName, messages);
    }
    const manifestFile = zip.file("blockforge-gallery.json");
    if (!manifestFile) {
      return withReport([], sourceFileName, [{ severity: "error", message: "Gallery bundle is missing blockforge-gallery.json." }]);
    }
    const bundle = JSON.parse(await manifestFile.async("string")) as BlueprintGalleryBundle;
    if (bundle.schemaVersion !== 1 || !Array.isArray(bundle.items)) {
      return withReport([], sourceFileName, [{ severity: "error", message: "Gallery bundle must use schemaVersion 1 and include items." }]);
    }
    const ids = new Set<string>();
    const items: BlueprintGalleryItem[] = [];
    for (const [index, item] of bundle.items.entries()) {
      const path = `items[${index}]`;
      if (item.id.includes("..") || item.id.includes("/") || item.id.includes("\\")) {
        messages.push({ severity: "error", path: `${path}.id`, message: "Gallery item id contains a path traversal segment." });
        continue;
      }
      if (ids.has(item.id)) {
        messages.push({ severity: "warning", path: `${path}.id`, message: `Duplicate gallery id "${item.id}" skipped.` });
        continue;
      }
      const validation = validateBlueprintJson(item.blueprintV2);
      if (!validation.valid) {
        messages.push({ severity: "warning", path, message: `Invalid blueprint skipped: ${validation.issues[0]?.message ?? "validation failed"}` });
        continue;
      }
      ids.add(item.id);
      items.push(item);
    }
    messages.push({ severity: "info", message: `Imported ${items.length} gallery item(s).` });
    return withReport(items, sourceFileName, messages);
  } catch {
    return withReport([], sourceFileName, [{ severity: "error", message: "Gallery bundle is not a valid ZIP bundle." }]);
  }
}

function withReport(items: BlueprintGalleryItem[], sourceFileName: string, messages: ImportReport["messages"]): GalleryImportResult {
  return {
    items,
    report: createImportReport({
      id: sourceFileName,
      sourceType: "gallery",
      sourceFileName,
      messages
    })
  };
}

function isSafeZipPath(path: string): boolean {
  return !path.includes("..") && !path.startsWith("/") && !path.startsWith("\\") && !/^[a-z]:/i.test(path);
}
