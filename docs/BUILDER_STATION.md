# Builder Station

Status: `3.5.0-alpha.1` scaffold. Minecraft manual regression is pending.

Builder Station is the alpha server-side construction runner for BuildPlan
jobs. v3.5 registers the in-game block on all three loaders and starts with
command-driven job scaffolding so loader-specific execution can be hardened
without changing the data contract again.

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
- UI/menu for station jobs.

Those are planned follow-up commits inside the v3.5 train.

## Safety Rules

Builder Station jobs must continue to respect:

- permission checks
- protection preflight
- material checks
- Material Cache / nearby container rules
- undo recording

Do not mark real gameplay passed until tested in Minecraft.
