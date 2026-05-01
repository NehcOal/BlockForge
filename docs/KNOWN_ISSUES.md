# Known Issues

Status for v5.3.0-beta.1:

- Browser visual QA: pending.
- Minecraft manual regression: pending.
- NeoForge client gameplay regression: pending.
- Fabric client smoke test: pending.
- Forge client smoke test: pending.
- Dedicated server smoke test: pending.
- Material Cache GUI: partial.
- Builder Station GUI: partial.
- Station world placement: partial.
- Admin rollback world integration: partial.
- Fabric / Forge advanced parity: partial.
- Litematica import: experimental.
- External AI live test: pending.
- Modrinth / CurseForge publishing: pending.
- Marketplace / cloud sync: not implemented.

## v5.3 Beta Hardening

- Large new gameplay systems are frozen.
- Core selling path is Web House Designer -> Blueprint export -> NeoForge
  preview/build/undo/material refund.
- Settlement / Contract / Progression / Events are experimental and not main
  selling points.
- Builder Station automation remains partial unless real Minecraft regression
  proves world placement, undo, material transactions, and audit together.

## v5.1 Alpha

## v5.2 Alpha

- House generation is deterministic and produces functional alpha shapes, not
  polished architecture.
- House-stage construction is represented by modules; the existing BuildPlan
  still executes by y-layer.
- In-game House Planner GUI is planned; v5.2 uses Web Designer and commands.
- Fabric / Forge house command surfaces are registered as alpha partial
  command flows.
- Minecraft manual regression and dedicated server smoke test remain pending.

- Settlement event, project, and emergency repair persistence is partial.
- Event Board, Project Map, and Emergency Beacon use command/chat feedback
  instead of full GUIs.
- Emergency repair verification is common pure logic; real world snapshot
  verification remains planned.
- v3.5 Gameplay Alpha manual regression: pending.
- Builder Station tick-based real placement is partial.
- Material Cache inventory-backed sourcing is partial.
- v4.0 Station Runtime is covered by pure tests, but loader-integrated world
  placement still needs real Minecraft validation.
- Material Cache GUI and Builder Station GUI have common state/action models,
  but loader-specific screens are partial.
- Audit JSONL formatting and admin rollback decisions are implemented in common
  logic, but loader file-writing and world rollback integration are partial.
- Modrinth / CurseForge publishing: pending.
- Litematica support is partial and experimental.
- No cloud sync.
- No online marketplace.
- AI generation quality is not guaranteed.
- Fabric / Forge advanced NBT undo coverage may remain partial.
- Binary `.litematic` parsing needs broader fixture coverage before any stable
  compatibility claim.
- Material Cache has common inventory logic, but loader-specific menu/screen behavior still needs manual client and server testing.
- Builder Station has common runtime/material resolution, but full world placement and GUI parity remain partial until in-game regression is completed.
- Audit JSONL writing has common file support, but loader command wiring and export flows need dedicated server validation.
- Diagnostics JSON export has common file support, but loader command wiring needs dedicated server validation.
- v4.3 station world placement has a common pre-mutation gate, but real loader
  world mutation remains partial until Minecraft regression verifies block
  placement, undo snapshots, material transactions, and audit writes together.
- Loader GUI parity is tracked by common status reports; NeoForge, Fabric, and
  Forge Material Cache / Builder Station screens are not yet all verified as
  fully interactive inventory GUIs.
- v4.4 quick-move, screen-registration, mutation-result, audit-path, and
  rollback-result contracts are covered by tests, but real loader Screen/Menu
  classes and world rollback still need Minecraft manual regression.
- v5.1 Settlement Contracts are Alpha: common settlement/contract/reward logic
  is tested, but persistent loader stores, full Contract Board GUI, world
  snapshot verification, and multiplayer balancing remain partial.
