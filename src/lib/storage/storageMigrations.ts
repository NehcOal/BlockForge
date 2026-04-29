export const BLOCKFORGE_DB_NAME = "blockforge-local-workbench";
export const BLOCKFORGE_DB_VERSION = 1;

export function runStorageMigrations(db: IDBDatabase): void {
  if (!db.objectStoreNames.contains("history")) db.createObjectStore("history", { keyPath: "id" });
  if (!db.objectStoreNames.contains("library")) db.createObjectStore("library", { keyPath: "id" });
  if (!db.objectStoreNames.contains("workspaces")) db.createObjectStore("workspaces", { keyPath: "id" });
}
