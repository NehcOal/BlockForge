# BlockForge Roadmap

## Version Train

BlockForge uses one feature branch per major product train. Small fixes,
polish, documentation updates, and focused tests are grouped into the active
major-version branch instead of creating separate `0.0.1` feature branches.

Recommended current branch:

- `feature/v5.1-settlement-events`

## Active Train

| Version | Theme | Goal | Release Gate |
|---|---|---|---|
| v5.1 | Settlement Events + Project Chains + Emergency Repairs | Add event pressure, stability, project chains, emergency repair DTOs, and command/resource scaffolds | Web and three loader builds pass; manual Minecraft/server QA pending |

## Next Trains

- v5.2: Persistent settlement/event/contract stores.
- v5.3: Contract Board, Event Board, Project Map, and Architect Desk GUI polish.
- v5.4: World snapshot contract and emergency repair verification.
- v5.5: Fabric/Forge command parity, multiplayer balancing, Minecraft regression, and dedicated server smoke test campaign.

## Current Alpha Rules

- Keep `5.1.0-alpha.1` clearly marked as Alpha, not stable.
- Keep Local Rule Generator as the no-key fallback.
- Keep OpenAI provider optional and server-side only.
- Do not expose API keys to browser client code.
- Validate every AI/import output before preview/export/build.
- Keep Browser visual QA pending until manually checked.
- Keep Minecraft manual regression pending until real clients are tested.
- Keep External AI live test pending until `OPENAI_API_KEY` is configured and
  manually tested.
