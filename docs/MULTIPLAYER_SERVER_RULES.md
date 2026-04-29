# Multiplayer Server Rules

Status: `3.5.0-alpha.1` scaffold. Dedicated server smoke testing is pending.

The v3.5 server rules layer defines common policy decisions for future
multiplayer construction flows.

See also:

- [Server Gameplay Rules](./SERVER_GAMEPLAY_RULES.md)
- [Audit And Rollback](./AUDIT_AND_ROLLBACK.md)
- [Team Builds](./TEAM_BUILDS.md)

## ServerBuildRules

Fields:

- `builderStationEnabled`
- `maxActivePlansPerPlayer`
- `maxBlocksPerTick`
- `maxQueuedJobs`
- `requireAnchorForStationJobs`
- `allowMaterialCache`
- `allowNearbyContainers`
- `allowPartialBuild`
- `permissionNodePrefix`

These rules are pure Java data and do not reference Minecraft classes.

## Current Evaluator

`ServerBuildRuleEvaluator.canQueueBuilderStationJob(...)` checks:

- Builder Station enabled
- active plans per player
- queue size
- required Builder Anchor

Loader integration and config file wiring are pending.

## Audit / Quota / Cooldown Scaffold

The common serverplay package now includes:

- `BuildAuditEntry`
- `BuildAuditLog`
- `BuildQuota`
- `BuildQuotaChecker`
- `BuildCooldown`
- `BuildProject`
- `AdminBuildSummary`

NeoForge command scaffold exposes `/blockforge admin audit`,
`/blockforge admin builds`, and `/blockforge quota ...`.

## Pending QA

- Minecraft manual regression: pending
- Dedicated server smoke test: pending
- Multiplayer conflict test: pending
