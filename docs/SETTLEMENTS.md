# BlockForge Settlements

BlockForge v5.3.0-beta.1 adds the first Settlement gameplay loop.

## v5.1 Event Integration

Settlements now expose alpha event state through common DTOs: active event
count, stability/prosperity/safety/logistics/culture, maintenance debt, active
project chains, and emergency repair requests. Loader persistence remains
partial; NeoForge exposes reference commands and Fabric/Forge expose clear
command scaffolds.

## Scope

- `blockforge_connector:settlement_core` is registered on NeoForge, Fabric, and Forge.
- Settlements are represented by common pure Java DTOs: `Settlement`, `SettlementLevel`, `SettlementPermission`, and `SettlementStatus`.
- NeoForge exposes command scaffold for `/blockforge settlement ...`.
- Fabric and Forge include block/item/resource registration; richer command parity is partial.

## Commands

- `/blockforge settlement create <name>`
- `/blockforge settlement info`
- `/blockforge settlement list`
- `/blockforge settlement members`
- `/blockforge settlement invite <player>`
- `/blockforge settlement leave`
- `/blockforge settlement level`
- `/blockforge settlement contracts`
- `/blockforge settlement abandon`

## Alpha Limits

- Loader persistence is partial.
- Settlement member invitations are scaffolded.
- Settlement Core GUI is planned.
- Minecraft manual regression and dedicated server smoke test are pending.
