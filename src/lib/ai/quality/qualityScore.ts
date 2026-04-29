export type AiQualityScore = {
  total: number;
  structure: number;
  buildability: number;
  materialDiversity: number;
  symmetry: number;
  validation: number;
  notes: string[];
  warnings: string[];
};

export function clampScore(value: number): number {
  return Math.max(0, Math.min(100, Math.round(value)));
}
