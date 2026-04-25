export function createSafeFileName(name: string, extension: string): string {
  const safeBaseName = name
    .trim()
    .toLowerCase()
    .replace(/\s+/g, "-")
    .replace(/[^a-z0-9-]/g, "")
    .replace(/-+/g, "-")
    .replace(/^-|-$/g, "");
  const safeExtension = extension.replace(/^\./, "").toLowerCase();

  return `${safeBaseName || "blockforge-blueprint"}.${safeExtension}`;
}

export function createSafeResourcePath(name: string): string {
  return (
    name
      .trim()
      .toLowerCase()
      .replace(/\s+/g, "_")
      .replace(/[^a-z0-9_/-]/g, "")
      .replace(/_+/g, "_")
      .replace(/\/+/g, "/")
      .replace(/^[_/-]+|[_/-]+$/g, "") || "blockforge_blueprint"
  );
}
