# Builder Station

Status: `4.1.0-beta.1` beta candidate. Minecraft manual regression is pending.

Builder Station is the beta server-side construction runner for BuildPlan
jobs. v3.5 registered the in-game block on all three loaders; v4.0 adds the
common tick runtime used to validate station batches before loader world
placement.

## Current Scope

- `blockforge_connector:builder_station`: registered on NeoForge, Fabric, and
  Forge.
- `BuilderStationJob`: one queued or running BuildPlan job.
- `BuilderStationJobStatus`: queued, running, paused, completed, cancelled, and
  failed.
- `BuilderStationQueue`: in-memory queue helper with queue limits and per-player
  current-job lookup.
- `BuilderStationState`: pure station position, owner, bound blueprint, bound
  anchor, material caches, active BuildPlan id, and status.
- `ServerBuildRules`: server policy knobs for Builder Station jobs.
- `ServerBuildRuleEvaluator`: pure rule checks before a job is accepted.
- `BuilderStationTickExecutor`: pure runtime that marks bounded BuildPlan
  batches and refuses unsafe ticks when chunks, protection, materials, quota, or
  cooldown gates fail.
- `BuilderStationStatusView`: common state payload for future station screens.
- `BuilderStationActionValidator`: server-side action gate for create/start/
  pause/resume/step/cancel/clear GUI buttons.
- NeoForge command scaffold:
  - `/blockforge station list`
  - `/blockforge station info`
  - `/blockforge station bind blueprint <id>`
  - `/blockforge station bind anchor nearest`
  - `/blockforge station bind cache nearest`
  - `/blockforge station createplan`
  - `/blockforge station start`
  - `/blockforge station pause`
  - `/blockforge station resume`
  - `/blockforge station cancel`
  - `/blockforge station step`
  - `/blockforge station status`
  - `/blockforge station clear`

## Not Yet Implemented

- Tick-based world placement.
- Persistent queue storage.
- Full Material Cache inventory-backed sourcing.
- Multiplayer conflict resolution.
- Loader-specific UI/menu for station jobs.

Those remain planned follow-up commits inside the v4.x gameplay beta train.

## Safety Rules

Builder Station jobs must continue to respect:

- permission checks
- protection preflight
- material checks
- Material Cache / nearby container rules
- undo recording

Do not mark real gameplay passed until tested in Minecraft.
