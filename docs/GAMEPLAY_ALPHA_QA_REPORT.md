# BlockForge v4.2.0-beta.1 Gameplay Alpha QA Report

Date: 2026-04-29

## Summary

BlockForge v4.2.0-beta.1 is suitable as a GitHub alpha prerelease candidate
from an automated build and scaffold-validation standpoint.

It is not yet suitable to mark as gameplay-regression passed. Minecraft client
manual regression and dedicated server smoke testing remain pending because they
were not run in this workspace.

## Review Fix Verification

| Finding | Status | Notes |
|---|---|---|
| Real `.litematic` files rejected | fixed | `.litematic` import now supports gzipped NBT fixtures through the NBT reader path. |
| Invalid converted blueprints imported | fixed | Blueprint validation errors now return an error report and do not populate `imported`. |
| Gallery `.zip` is plain JSON | fixed | Gallery bundle export now uses JSZip and import expects a ZIP bundle. |

## Automated Validation

| Check | Result |
|---|---|
| `pnpm lint` | passed |
| `pnpm test` | passed: 59 files / 154 tests |
| `pnpm build` | passed |
| NeoForge `gradlew.bat build` | passed |
| Fabric `gradlew.bat build` | passed |
| Forge `gradlew.bat build` | passed |
| `git diff --check` | passed |

## Gameplay Blocks

| Block | Automated Status | Manual Status |
|---|---|---|
| Blueprint Table | resources/build passed | pending Minecraft client test |
| Material Cache | resources/build passed | pending Minecraft client test |
| Builder Anchor | resources/build passed | pending Minecraft client test |
| Builder Station | resources/build passed | pending Minecraft client test |
| Material Link | resources/build passed | pending Minecraft client test |
| Construction Core | resources/build passed | pending Minecraft client test |

## Builder Wand Modes

Builder Wand common state and loader builds pass. Manual validation of mode
cycling, BUILD, DRY_RUN, MATERIALS, UNDO, mirror, offset, and anchor behavior is
pending in real Minecraft clients.

## BuildPlan / Station / Audit / Quota

| Area | Status |
|---|---|
| BuildPlan pure logic | automated tests passed |
| NeoForge BuildPlan command scaffold | build passed, manual command test pending |
| Builder Station common queue | automated tests passed |
| NeoForge Station commands | build passed, manual command test pending |
| Audit DTO / in-memory log | automated tests passed |
| Quota / cooldown DTOs | automated tests passed |
| Fabric / Forge station command parity | planned |

## Dedicated Server

Dedicated server smoke test was not run in this workspace.

Status: pending / blocked by lack of a launched dedicated server and joined
client session in the current environment.

Required follow-up:

1. Start NeoForge dedicated server.
2. Start Fabric dedicated server.
3. Start Forge dedicated server.
4. Join each server with a matching client.
5. Run the checklist in `docs/GAMEPLAY_ALPHA_TESTING.md`.

## Known Partial Features

- Builder Station tick-based real world placement is partial.
- Material Cache inventory-backed sourcing is partial.
- Station job persistence is partial.
- Admin rollback is planned.
- Fabric / Forge station/admin/quota command parity is planned.
- Minecraft manual regression is pending.
- Dedicated server smoke test is pending.

## Release Readiness

Recommended release type:

- GitHub prerelease: yes, alpha candidate.
- Stable release: no.
- Modrinth / CurseForge publishing: pending.

Release note wording should say: `Gameplay Alpha scaffold`, not `gameplay
regression passed`.
