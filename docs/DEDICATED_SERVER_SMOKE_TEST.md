# BlockForge Dedicated Server Smoke Test

Status: `4.3.0-beta.1` checklist. Do not mark passed until the server is actually started and the commands below are run.

Run this separately for NeoForge, Fabric, and Forge on Minecraft `1.21.1` with Java `21`.

## Startup Checks

- Start the dedicated server with the connector jar installed.
- Confirm the log shows BlockForge loading without `ClassNotFoundException` for client screens or renderers.
- Confirm `config/blockforge/` is created.
- Confirm no client-only package is loaded on the dedicated server path.

## Commands

```mcfunction
/blockforge status
/blockforge diagnostics
/blockforge diagnostics export
/blockforge reload
/blockforge station status
/blockforge station step
/blockforge quota get Dev
/blockforge admin audit
```

## Expected Results

- Commands return clear success, partial, or permission messages.
- Diagnostics export writes under `config/blockforge/diagnostics/`.
- Audit JSONL writes under `config/blockforge/audit/` when an audited action is triggered.
- Permission denials do not crash the server.
- Material missing, quota denied, cooldown denied, and protection denied paths do not consume materials.

## Pending

- NeoForge dedicated server smoke test: pending.
- Fabric dedicated server smoke test: pending.
- Forge dedicated server smoke test: pending.
