# BlockForge Roadmap

## Version Train

BlockForge uses one feature branch per major product train. Small fixes,
polish, documentation updates, and focused tests are grouped into the active
major-version branch instead of creating separate `0.0.1` feature branches.

Recommended current branch:

- `feature/v3.2-construction-workflow`

## Active Train

| Version | Theme | Goal | Release Gate |
|---|---|---|---|
| v3.2 | Construction Workflow + Build Planner | Add deterministic BuildPlan model, layer planning, preview/status commands, and repair plan pure logic | Three loader builds pass; real per-step placement pending |

## Next Trains

- v3.3: Builder Station with real tick-based plan execution.
- v3.4: Material Cache inventory/menu and cache-backed sourcing parity.
- v3.5: Anchor-fixed ghost preview and mirrored placement transformation.
- v3.6: Real Minecraft regression and dedicated server smoke test.
- v4.0: Stable release planning after alpha/rc evidence is collected.

## Current Alpha Rules

- Keep `3.2.0-alpha.1` clearly marked as Alpha, not stable.
- Keep Local Rule Generator as the no-key fallback.
- Keep OpenAI provider optional and server-side only.
- Do not expose API keys to browser client code.
- Validate every AI/import output before preview/export/build.
- Keep Browser visual QA pending until manually checked.
- Keep Minecraft manual regression pending until real clients are tested.
- Keep External AI live test pending until `OPENAI_API_KEY` is configured and
  manually tested.
