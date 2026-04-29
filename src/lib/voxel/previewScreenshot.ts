import { createSafeFileName } from "@/lib/voxel/exportUtils";

export function createPreviewScreenshotFileName(modelId: string): string {
  const safeId = (modelId || "voxel-model").replace(/[\\/]+/g, "-");
  return `blockforge-preview-${createSafeFileName(safeId, "png")}`;
}

export function canvasToPngBlob(canvas: HTMLCanvasElement): Promise<Blob> {
  return new Promise((resolve, reject) => {
    if (!canvas.toBlob) {
      reject(new Error("PNG export is not supported by this browser."));
      return;
    }
    canvas.toBlob((blob) => {
      if (!blob || blob.size === 0) {
        reject(new Error("Preview PNG export produced an empty image."));
        return;
      }
      resolve(blob);
    }, "image/png");
  });
}
