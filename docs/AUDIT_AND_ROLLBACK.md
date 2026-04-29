# BlockForge Audit And Rollback

Version: 4.1.0-beta.1

Audit and rollback support is beta-scaffolded for multiplayer servers.

v4.1 adds JSONL formatting and admin rollback decision logic. Loader-level file
writing and world rollback integration remain partial until real server testing.

## Audit Events

The common audit model can record:

- Wand build
- BuildPlan start / pause / cancel / complete
- Builder Station start / pause / resume / cancel / complete
- Undo
- Material consumed / refunded summary

`AuditJsonlFormatter` serializes each `BuildAuditEntry` to one JSON object per
line for the planned path:

`config/blockforge/audit/blockforge-audit-YYYY-MM-DD.jsonl`

## Commands

NeoForge command scaffold:

- `/blockforge admin audit`
- `/blockforge admin audit export`
- `/blockforge admin builds`

Fabric / Forge command parity is planned.

## Rollback

Rollback depends on existing undo snapshots. `AdminRollbackPlanner` rejects
rollback when the caller lacks permission, no snapshot exists, or protection is
enforced and denies the rollback area. It never guesses world changes.

## Current Alpha Limits

- Loader file-writing integration for JSONL audit is pending.
- Admin rollback world integration is partial.
- Dedicated server smoke test is pending.
