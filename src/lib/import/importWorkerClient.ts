export function canUseImportWorker(): boolean {
  return typeof Worker !== "undefined";
}

export async function runImportWithFallback<T>(workerTask: () => Promise<T>, fallbackTask: () => Promise<T>): Promise<T> {
  if (!canUseImportWorker()) return fallbackTask();
  try {
    return await workerTask();
  } catch {
    return fallbackTask();
  }
}
