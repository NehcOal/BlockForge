# Known Issues

Status for v4.1.0-beta.1:

- Browser visual QA: pending.
- Minecraft manual regression: pending.
- External AI live test: pending.
- Dedicated server smoke test: pending.
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
