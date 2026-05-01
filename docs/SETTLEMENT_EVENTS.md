# Settlement Events

BlockForge v5.1.0-alpha.1 adds an alpha settlement event loop. Events are pure gameplay state: they do not randomly break blocks, remove items, or bypass protection.

## Flow

1. A settlement tracks stability, prosperity, safety, logistics, culture, and maintenance debt.
2. The event generator uses game time, settlement level, active event count, and stability pressure.
3. Active events can suggest contracts, project stages, or emergency repair requests.
4. Resolving an event grants reputation and stability.
5. Ignoring or failing events increases maintenance debt.

## Event Board

`blockforge_connector:event_board` is the in-world entry point. In this alpha it uses chat/command feedback:

- `/blockforge events list`
- `/blockforge events info <eventId>`
- `/blockforge events refresh`
- `/blockforge events resolve <eventId>`
- `/blockforge events ignore <eventId>`

Fabric and Forge expose matching command scaffolds; NeoForge has the reference command behavior.

## Limits

- Default target: at most 3 active events per settlement.
- Default refresh interval: 24,000 game ticks.
- Emergency repair timeout: 72,000 game ticks.
- Project chain limit: 2 active projects per settlement.
- Positive events become more likely once stability is at least 60.
- Stability warnings begin below 30.
- Critical events are limited by generator pressure and settlement level.
- No world damage is performed by events.
- Loader persistence is partial; common DTOs are ready for file-backed storage.

## Manual QA

Status: pending. Do not mark Minecraft manual regression or dedicated server smoke test as passed until tested in game.
