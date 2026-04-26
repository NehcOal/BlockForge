# BlockForge Multi-loader Plan

## Why Multi-loader

BlockForge started with a NeoForge Connector because it was the fastest path to
prove in-game blueprint placement, GUI selection, Ghost Preview, survival
materials, undo, and release packaging.

The next goal is to support the major Java mod loader paths without copying the
entire Connector three times:

- NeoForge 1.21.1
- Fabric 1.21.1
- Forge 1.21.1

## Why Not Copy Three Full Connectors

Blueprint parsing, rotation rules, material report data, build planning, and
undo transaction records are not loader-specific. Copying those pieces into
three projects would create drift and make bug fixes harder to apply evenly.

The multi-loader architecture keeps stable domain logic in `mod/common` and lets
each loader own only the integration code that truly depends on that loader API.

## Common Core Responsibilities

`mod/common` owns loader-neutral Java types and logic:

- Blueprint data model and parser contracts for v1/v2 JSON.
- Palette entries and blueprint summary data.
- Rotation rules for `0`, `90`, `180`, and `270` degrees.
- Horizontal `facing` rotation.
- Build planning data such as `BuildPlan` and `PlannedBlock`.
- Material data such as `MaterialRequirement`, `MaterialReport`,
  `ConsumedMaterialEntry`, and `MaterialTransaction`.
- Undo metadata that does not contain Minecraft `BlockState`, `BlockPos`, or
  player objects.
- Minimal platform references for block, player, and world adapters.

`BuildPlan` only describes what should be placed. It does not modify a
Minecraft world.

## Loader Adapter Responsibilities

Each loader adapter owns API-specific integration:

- Mod initialization and registration.
- Command registration.
- Item registration.
- GUI screens.
- Networking payloads.
- Ghost Preview rendering.
- Config registration.
- Real world placement through the loader/Minecraft server APIs.
- Registry lookup for blocks and items.
- Player inventory checks, item consumption, and refunds.
- Block snapshot capture and restore when Minecraft `BlockState` or block
  entity data is required.

## Current Gradle Organization

In v1.1.1, `mod/common` is included by the NeoForge and Fabric builds as an
additional Java source root:

```groovy
sourceSets.main.java {
    srcDir('../common/src/main/java')
}
```

This keeps the existing NeoForge Gradle project stable while Fabric Alpha proves
the minimum adapter surface. A full multi-project Gradle layout can be added
after `fabric-connector` and `forge-connector` both exist.

## Version Plan

- `v1.1.0`: common core and NeoForge adapter integration.
- `v1.1.1`: Fabric Connector command Alpha.
- `v1.1.2`: Forge Connector alpha.
- `v1.1.3`: parity pass across NeoForge, Fabric, and Forge.

## Current Risks

- Fabric networking and rendering differ from NeoForge and will need their own
  adapter code.
- Forge and NeoForge are related but not API-identical, so Forge cannot be
  treated as a drop-in copy.
- GUI and Ghost Preview will need loader-specific implementations.
- Registry lookup, inventory mutation, and world placement must remain in each
  loader adapter to avoid leaking Minecraft runtime types into common core.

## v1.1.1 Status

- NeoForge remains the complete and stable in-game target.
- Fabric now has a command-only Alpha under `mod/fabric-connector`.
- Fabric Alpha supports blueprint reload/list/info/dryrun/build/undo and bundled example installation.
- Fabric Alpha reuses common blueprint parsing, rotation, and build planning data.
- Fabric Alpha does not include GUI, Ghost Preview, Builder Wand, survival material costs, inventory transactions, material refunds, or BlockEntity NBT undo.
- Forge support is planned but not implemented.
- Manual Minecraft Fabric regression testing is pending.
