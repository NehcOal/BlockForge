# BlockForge Roadmap

## Version Train

BlockForge uses one feature branch per major product train. Small fixes,
polish, documentation updates, and focused tests are grouped into the active
major-version branch instead of creating separate `0.0.1` feature branches.

Recommended current branch:

- `feature/v3.1-gameplay-tools`

## Active Train

| Version | Theme | Goal | Release Gate |
|---|---|---|---|
| v3.1 | Gameplay Utility Blocks + Advanced Builder Wand | Add Blueprint Table, Material Cache, Builder Anchor, and wand modes | Three loader builds pass; Minecraft manual regression pending |

## Next Trains

- v3.2: Material Cache inventory/menu and cache-backed sourcing parity.
- v3.3: Anchor-fixed ghost preview and mirrored placement transformation.
- v3.4: Real Minecraft regression and dedicated server smoke test.
- v3.5: Litematica binary fixture hardening.
- v4.0: Stable release planning after alpha/rc evidence is collected.

## Current Alpha Rules

- Keep `3.1.0-alpha.1` clearly marked as Alpha, not stable.
- Keep Local Rule Generator as the no-key fallback.
- Keep OpenAI provider optional and server-side only.
- Do not expose API keys to browser client code.
- Validate every AI/import output before preview/export/build.
- Keep Browser visual QA pending until manually checked.
- Keep Minecraft manual regression pending until real clients are tested.
- Keep External AI live test pending until `OPENAI_API_KEY` is configured and
  manually tested.
