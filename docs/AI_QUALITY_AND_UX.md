# AI Quality And UX

Status: `v5.1.0-alpha.1` Alpha.

## Scope

v2.5 expands AI Generation Alpha into a more usable generation workflow:

- Prompt presets
- Multi-candidate generation
- Quality scoring
- Candidate comparison
- Structure Plan viewer
- Local refinement prompts

## Prompt Presets

Built-in presets cover starter, medieval, survival, dungeon, bridge, statue,
utility, and custom-style prompts. Users can select a preset, edit the prompt,
and generate locally or through the optional external provider.

## Multi-candidate Generation

Local Rule Generator can produce up to three deterministic candidates. External
providers are also capped at three candidates and must be mocked in tests.

## Quality Score

Scores are clamped to `0-100` and combine:

- validation
- buildability
- material diversity
- structure
- symmetry

The highest score is marked as the best candidate.

## Refine Workflow

Local refinement appends structured instructions such as taller, more windows,
or more stone bricks. External refinement remains provider-driven and must still
pass the same validation pipeline.

## Limitations

- Quality score is heuristic.
- Structure Plan viewer is read-only.
- External AI live testing remains pending until manually run.
