import { BLOCKFORGE_DB_NAME, BLOCKFORGE_DB_VERSION, runStorageMigrations } from "@/lib/storage/storageMigrations";
import { BlockForgeStorageError } from "@/lib/storage/storageErrors";

export async function openBlockForgeDb(): Promise<IDBDatabase> {
  if (typeof indexedDB === "undefined") {
    throw new BlockForgeStorageError("IndexedDB is not available in this environment.");
  }
  return new Promise((resolve, reject) => {
    const request = indexedDB.open(BLOCKFORGE_DB_NAME, BLOCKFORGE_DB_VERSION);
    request.onupgradeneeded = () => runStorageMigrations(request.result);
    request.onsuccess = () => resolve(request.result);
    request.onerror = () => reject(new BlockForgeStorageError(request.error?.message ?? "Failed to open IndexedDB."));
  });
}

export function exportStorageBackup(items: unknown[]): string {
  return JSON.stringify({ schemaVersion: 1, exportedAt: new Date().toISOString(), items }, null, 2);
}
