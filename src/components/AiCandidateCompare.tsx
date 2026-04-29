import type { AiGenerationCandidate } from "@/lib/ai";

export function AiCandidateCompare({
  candidates,
  selectedCandidateId,
  onSelect
}: {
  candidates: AiGenerationCandidate[];
  selectedCandidateId?: string;
  onSelect: (candidate: AiGenerationCandidate) => void;
}) {
  if (candidates.length === 0) return null;
  const best = [...candidates].sort((a, b) => b.qualityScore.total - a.qualityScore.total)[0];
  return (
    <section className="rounded border border-forge/20 bg-black/20 p-3 text-xs text-stone-300">
      <p className="font-bold text-stone-100">Candidates</p>
      <div className="mt-2 grid gap-2">
        {candidates.map((candidate) => (
          <button
            className={`rounded border px-3 py-2 text-left ${candidate.id === selectedCandidateId ? "border-forge bg-forge/10" : "border-forge/15 bg-black/20"}`}
            key={candidate.id}
            onClick={() => onSelect(candidate)}
            type="button"
          >
            <span className="font-bold text-stone-100">{candidate.name}</span>
            {candidate.id === best.id ? <span className="ml-2 text-emerald-200">Best</span> : null}
            <span className="block text-stone-400">Score {candidate.qualityScore.total} · {candidate.model.blocks.length} blocks · {candidate.provider}</span>
          </button>
        ))}
      </div>
    </section>
  );
}
