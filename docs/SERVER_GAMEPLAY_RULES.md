# BlockForge Server Gameplay Rules

Version: 5.1.0-alpha.1

Server Gameplay Rules are the alpha control layer for multiplayer BlockForge builds.

## v5.1 Event Safety

Settlement events, projects, and emergency repairs do not bypass quota,
cooldown, protection, material checks, or admin rollback rules. Events do not
randomly damage the world; damaged-structure events only create repair state.

In v4.0, the common Station Runtime consumes these rules as pure gates before a
station batch can be marked for placement.

## Included Rules

- Build quota DTO and checker.
- Cooldown DTO for spam protection.
- Admin build summary DTO.
- Audit entry and in-memory audit log.
- Project/team scaffold.

## Quota Defaults

Recommended alpha defaults:

- `enableBuildQuota=false`
- `maxBlocksPerPlayerPerDay=50000`
- `maxActiveBuildJobsPerPlayer=2`
- `maxStationJobsPerDimension=8`
- `maxBlocksPerBuildJob=10000`

When quota is denied, no material should be consumed and no block should be placed.

## Cooldowns

Recommended alpha defaults:

- Wand build: 2 seconds
- Station start: 5 seconds
- BuildPlan step: 1 second

## Current Alpha Limits

- Persistent quota storage is planned.
- Persistent audit JSONL export is planned.
- Admin build aggregation is command scaffold only.
- Minecraft manual regression is pending.
