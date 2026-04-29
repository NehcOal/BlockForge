# BlockForge Audit And Rollback

Version: 3.5.0-alpha.1

Audit and rollback support is alpha-scaffolded for multiplayer servers.

## Audit Events

The common audit model can record:

- Wand build
- BuildPlan start / pause / cancel / complete
- Builder Station start / pause / resume / cancel / complete
- Undo
- Material consumed / refunded summary

## Commands

NeoForge command scaffold:

- `/blockforge admin audit`
- `/blockforge admin audit export`
- `/blockforge admin builds`

Fabric / Forge command parity is planned.

## Rollback

Rollback depends on existing undo snapshots. If no snapshot exists, admin rollback should report unavailable instead of guessing world changes.

## Current Alpha Limits

- Persistent audit export is planned.
- Admin rollback command is planned.
- Dedicated server smoke test is pending.

