# BlockForge Roadmap

## Version Train

BlockForge uses one feature branch per major product train. Small fixes,
polish, documentation updates, and focused tests are grouped into the active
major-version branch instead of creating separate `0.0.1` feature branches.

Recommended current branch:

- `feature/v3.0-product-workbench`

## Active Train

| Version | Theme | Goal | Release Gate |
|---|---|---|---|
| v2.6 | Regression QA + Live AI Readiness | Prepare visual QA, live AI smoke testing, and release notes for testers | Docs complete; Browser visual QA pending; External AI live test pending |
| v2.7 | Litematica Interop Alpha | Add safe experimental `.litematic` import into Blueprint v2 | Validation report required; no full fidelity claim |
| v2.8 | Blueprint Gallery / Sharing Workspace | Add local gallery, metadata, search, and gallery bundle import/export | Local-first; no marketplace; no cloud sync |
| v2.9 | Multiplayer / Server Admin Polish | Improve diagnostics, admin docs, and server issue reporting | Diagnostics docs/schema; dedicated server smoke test pending |
| v3.0 | Product Workbench Redesign | Organize Web into a unified workbench with status and command actions | Web checks pass; Browser visual QA pending |

## Next Trains

- v3.1: Browser visual QA and product screenshot pass.
- v3.2: Real Minecraft regression and dedicated server smoke test.
- v3.3: Litematica binary fixture hardening.
- v3.4: Connector diagnostics implementation parity.
- v4.0: Stable release planning after alpha/rc evidence is collected.

## Current Alpha Rules

- Keep `3.0.0-alpha.1` clearly marked as Alpha, not stable.
- Keep Local Rule Generator as the no-key fallback.
- Keep OpenAI provider optional and server-side only.
- Do not expose API keys to browser client code.
- Validate every AI/import output before preview/export/build.
- Keep Browser visual QA pending until manually checked.
- Keep Minecraft manual regression pending until real clients are tested.
- Keep External AI live test pending until `OPENAI_API_KEY` is configured and
  manually tested.
