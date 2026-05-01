# BlockForge Gameplay GUI

Version: 5.3.0-beta.1

Status: common state/action layer implemented; loader-specific screens remain
partial. This beta keeps dedicated-server safety ahead of GUI scope.

v4.4 adds the remaining shared contracts needed before real loader screens can
be considered complete:

- `MaterialCacheQuickMovePlan` defines cache/player/hotbar shift-click routing.
- `LoaderScreenRegistrationPlan` records whether a loader keeps client Screen
  classes isolated from common/server registration.

v4.3 keeps loader GUI parity explicit instead of silently treating common DTOs
as completed Minecraft screens:

- `LoaderGuiParityReport` records NeoForge, Fabric, and Forge status for
  Material Cache GUI, Builder Station GUI, Construction Core GUI, station world
  placement, admin rollback, advanced commands, audit persistence, diagnostics
  export, and dedicated-server safety.
- `LoaderGuiParityReport.releaseReadinessLabel()` keeps partial critical GUI or
  runtime paths in `draft-pr-only` state.

v4.2 added common inventory/runtime support used by loader GUIs:

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

Still pending: full loader inventory screen, automation access, and real
Minecraft inventory sync testing. Quick-move routing now has a common model,
but each loader still needs its own Menu/ScreenHandler implementation verified
in game.

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
- Fabric / Forge advanced command parity should be tested with command-surface
  coverage even when a command currently returns a clear partial message.
- Minecraft manual regression and dedicated server smoke test remain pending.
