# BlockForge Audit And Rollback

Version: 5.1.0-alpha.1

v4.4 adds `LoaderIntegrationPaths`, the shared path helper for loader audit and
diagnostics output.

## v5.1 Audit Events

The audit surface should record event generated/resolved/expired, project
activated/stage completed/completed, and emergency repair started/completed.
The common DTOs are ready; loader JSONL integration for these new event types is
partial.
diagnostics files:

- Audit JSONL: `config/blockforge/audit/blockforge-audit-YYYY-MM-DD.jsonl`
- Diagnostics JSON: `config/blockforge/diagnostics/blockforge-diagnostics-<timestamp>.json`

v4.4 also adds `AdminRollbackIntegrationResult` so loaders can report whether a
rollback found a snapshot, restored blocks, refunded materials, recorded audit,
and updated station/job state. Real world rollback still requires loader undo
snapshot lookup by job id and Minecraft regression.

v4.2 adds `AuditJsonlWriter`, a server-safe file writer for `config/blockforge/audit/blockforge-audit-YYYY-MM-DD.jsonl`.

The writer returns warnings instead of throwing through server code. Loader commands still need dedicated server smoke testing before audit persistence is marked passed.

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
