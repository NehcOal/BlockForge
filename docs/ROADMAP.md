# BlockForge Roadmap

## Version Train

BlockForge uses one feature branch per major product train. Small fixes,
polish, documentation updates, and focused tests are grouped into the active
major-version branch instead of creating separate `0.0.1` feature branches.

Recommended current branch:

- `feature/v5.3-gameplay-release-hardening`

## Active Train

| Version | Theme | Goal | Release Gate |
|---|---|---|---|
| v5.3 | Gameplay Release Hardening | Freeze large gameplay additions, harden core house/build/undo/material flow, simplify docs, and prepare beta candidate artifacts | Web and three loader clean builds pass; manual Minecraft/server QA pending |

## Next Trains

- v5.4: Minecraft manual regression, dedicated server smoke test campaign, and release blocker fixes.
- v5.5: Optional polish only after the beta path is proven in-game.

## Current Alpha Rules

- Keep `5.3.0-beta.1` clearly marked as Alpha, not stable.
- Keep Local Rule Generator as the no-key fallback.
- Keep OpenAI provider optional and server-side only.
- Do not expose API keys to browser client code.
- Validate every AI/import output before preview/export/build.
- Keep Browser visual QA pending until manually checked.
- Keep Minecraft manual regression pending until real clients are tested.
- Keep External AI live test pending until `OPENAI_API_KEY` is configured and
  manually tested.
