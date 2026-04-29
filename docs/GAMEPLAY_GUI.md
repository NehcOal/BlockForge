# BlockForge Gameplay GUI

Version: 4.0.0-beta.1

Status: partial / planned. This beta keeps dedicated-server safety ahead of GUI
scope.

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

Status: planned / partial.

The block is registered and documented, but a full inventory GUI and quick-move
flow still need loader-specific implementation and real Minecraft testing.

## Builder Station

Status: planned / partial.

The common runtime can process safe batches in pure tests. The GUI should show:

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

Status: scaffold.

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
