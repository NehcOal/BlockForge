export function schematicFileName(id: string): string {
  return `blockforge-${id.replace(/[^a-z0-9_-]+/gi, "_").toLowerCase()}.schem`;
}
