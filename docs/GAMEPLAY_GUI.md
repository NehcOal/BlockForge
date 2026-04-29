# BlockForge Gameplay GUI

Version: 4.2.0-beta.1

Status: common state/action layer implemented; loader-specific screens remain
partial. This beta keeps dedicated-server safety ahead of GUI scope.

v4.2 adds common inventory/runtime support used by loader GUIs:

- `MaterialCacheInventory` models cache slots, insert/extract, full-cache fallback, and break drops without Minecraft classes.
- `MaterialCacheMenuState` remains the server-safe menu state DTO.
- `BuilderStationStatusView` and `BuilderStationActionValidator` gate station buttons before server actions execute.
- Loader-specific Material Cache and Builder Station screens remain partial until Minecraft client/server smoke tests are completed.

Do not mark Material Cache GUI or Builder Station GUI as fully passed until NeoForge, Fabric, and Forge have been opened in-game and verified.

## Blueprint Table

Implemented:

- Opens the existing Blueprint Selector.
- Keeps server-side selection validation.
- Does not load client-only screen classes from dedicated server init.

Planned polish:

- Selected blueprint source tag.
- Warning badge.
- BuildPlan create shortcut.
- Station / Anchor hints.

## Material Cache

Status: common menu state implemented; loader screens partial.

`MaterialCacheMenuState` carries cache id, position, slot counts, linked station
count, material source priority, accessibility, protection state, and warnings.
NeoForge/Fabric/Forge can render this state without loading client screen
classes from common/server code.

Still pending: full loader inventory screen, quick-move behavior, automation
access, and real Minecraft inventory sync testing.

## Builder Station

Status: common status/action layer implemented; loader screens partial.

The common runtime can process safe batches in pure tests. v4.1 adds
`BuilderStationStatusView`, `BuilderStationAction`, and
`BuilderStationActionValidator` so button clicks can be validated server-side.

The GUI should show:

- bound blueprint
- bound anchor
- bound material caches
- job status
- progress
- current layer
- issues count
- start / pause / resume / cancel / step buttons

All button actions must send packets to the server and let the server validate
permissions and state transitions.

## Construction Core

Status: status view scaffold.

`ConstructionCoreStatusView` summarizes project id, owner, station count, anchor
count, cache count, and active job count.

Expected future UI:

- project info
- station list
- anchor list
- material cache list

## Beta Safety Rules

- Dedicated server must not load screen classes.
- Client must not mutate job state directly.
- Server remains authoritative for station, cache, quota, audit, and placement
  decisions.
- Minecraft manual regression and dedicated server smoke test remain pending.
