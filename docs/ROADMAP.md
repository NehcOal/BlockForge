# BlockForge Roadmap

## Version Train

BlockForge uses one feature branch per major product train. Small fixes,
polish, documentation updates, and focused tests are grouped into the active
major-version branch instead of creating separate `0.0.1` feature branches.

Recommended current branch:

- `feature/v3.5-gameplay-alpha`

## Active Train

| Version | Theme | Goal | Release Gate |
|---|---|---|---|
| v3.5 | Builder Station + Material Network + Server Rules | Add station blocks, material links, audit/quota/cooldown scaffolds, gameplay resources, and alpha readiness docs | Web and three loader builds pass; real station tick placement pending |

## Next Trains

- v3.6: Real Minecraft regression and dedicated server smoke test.
- v3.7: Material Cache inventory/menu and cache-backed sourcing parity.
- v3.8: Anchor-fixed ghost preview and mirrored placement transformation.
- v4.0: Stable release planning after alpha/rc evidence is collected.

## Current Alpha Rules

- Keep `3.5.0-alpha.1` clearly marked as Alpha, not stable.
- Keep Local Rule Generator as the no-key fallback.
- Keep OpenAI provider optional and server-side only.
- Do not expose API keys to browser client code.
- Validate every AI/import output before preview/export/build.
- Keep Browser visual QA pending until manually checked.
- Keep Minecraft manual regression pending until real clients are tested.
- Keep External AI live test pending until `OPENAI_API_KEY` is configured and
  manually tested.
