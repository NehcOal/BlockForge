# BlockForge Roadmap

## Version Train

BlockForge uses one feature branch per major product train. Small fixes,
polish, documentation updates, and focused tests are grouped into the active
major-version branch instead of creating separate `0.0.1` feature branches.

Recommended current branch:

- `feature/v4.0-gameplay-beta`

## Active Train

| Version | Theme | Goal | Release Gate |
|---|---|---|---|
| v4.0 | Gameplay Beta Candidate | Add Station Runtime pure tick execution, server hardening docs, diagnostics fields, and beta QA readiness | Web and three loader builds pass; manual Minecraft/server QA pending |

## Next Trains

- v4.1: Real Minecraft regression and dedicated server smoke test.
- v4.2: Material Cache inventory/menu and cache-backed sourcing parity.
- v4.3: Anchor-fixed ghost preview and mirrored placement transformation.
- v5.0: Stable release planning after beta/rc evidence is collected.

## Current Beta Rules

- Keep `4.0.0-beta.1` clearly marked as Beta, not stable.
- Keep Local Rule Generator as the no-key fallback.
- Keep OpenAI provider optional and server-side only.
- Do not expose API keys to browser client code.
- Validate every AI/import output before preview/export/build.
- Keep Browser visual QA pending until manually checked.
- Keep Minecraft manual regression pending until real clients are tested.
- Keep External AI live test pending until `OPENAI_API_KEY` is configured and
  manually tested.
