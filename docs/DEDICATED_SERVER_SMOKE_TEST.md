# BlockForge Dedicated Server Smoke Test

Status: `5.3.0-beta.1` checklist. Do not mark passed until the server is actually started and the commands below are run.

Run this separately for NeoForge, Fabric, and Forge on Minecraft `1.21.1` with Java `21`.

## v5.3 Required Server Smoke Test

Current status: `pending`

Use clean builds before copying jars:

```powershell
cd mod/neoforge-connector
gradlew.bat clean build

cd ../fabric-connector
gradlew.bat clean build

cd ../forge-connector
gradlew.bat clean build
```

Expected release jars:

- `blockforge-connector-neoforge-5.3.0-beta.1.jar`
- `blockforge-connector-fabric-5.3.0-beta.1.jar`
- `blockforge-connector-forge-5.3.0-beta.1.jar`

Checklist:

1. Start NeoForge dedicated server.
2. Confirm no client-only class crash.
3. Join server.
4. Run `/blockforge status`.
5. Run `/blockforge diagnostics`.
6. Run `/blockforge diagnostics export`.
7. Run blueprint reload/list/info commands.
8. If possible, test wand build/undo.
9. Repeat Fabric / Forge server launch smoke tests, or leave explicitly `pending`.

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
