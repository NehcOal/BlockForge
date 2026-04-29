# BlockForge Roadmap

## Version Train

BlockForge uses one feature branch per major product train. Small fixes,
polish, documentation updates, and focused tests are grouped into the active
major-version branch instead of creating separate `0.0.1` feature branches.

Recommended current branch:

- `feature/v4.3-loader-gui-runtime`

## Active Train

| Version | Theme | Goal | Release Gate |
|---|---|---|---|
| v4.3 | Loader GUI Parity + World Runtime Integration | Track three-loader GUI parity, gate station world placement before mutation, and expose command/audit/diagnostics integration status honestly | Web and three loader builds pass; manual Minecraft/server QA pending |

## Next Trains

- v4.4: Loader-specific Material Cache and Builder Station screens.
- v4.5: Real Minecraft regression and dedicated server smoke test.
- v4.6: Anchor-fixed ghost preview and mirrored placement transformation.
- v5.0: Stable release planning after beta/rc evidence is collected.

## Current Beta Rules

- Keep `4.3.0-beta.1` clearly marked as Beta, not stable.
- Keep Local Rule Generator as the no-key fallback.
- Keep OpenAI provider optional and server-side only.
- Do not expose API keys to browser client code.
- Validate every AI/import output before preview/export/build.
- Keep Browser visual QA pending until manually checked.
- Keep Minecraft manual regression pending until real clients are tested.
- Keep External AI live test pending until `OPENAI_API_KEY` is configured and
  manually tested.
