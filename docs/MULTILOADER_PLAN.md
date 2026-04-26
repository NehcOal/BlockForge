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

In v1.1.2, `mod/common` is included by the NeoForge, Fabric, and Forge builds
as an additional Java source root:

```groovy
sourceSets.main.java {
    srcDir('../common/src/main/java')
}
```

This keeps the existing loader Gradle projects stable while Fabric and Forge
Alpha prove the minimum adapter surface. A full multi-project Gradle layout can
be added after the three connector modules settle.

## Version Plan

- `v1.1.0`: common core and NeoForge adapter integration.
- `v1.1.1`: Fabric Connector command Alpha.
- `v1.1.2`: Forge Connector command Alpha.
- `v1.1.3`: multi-loader Alpha stabilization, documentation alignment, CI
  artifacts, and release packaging.
- `v1.2.0`: Fabric and Forge Builder Wand Alpha parity.
- `v1.2.1`: Fabric and Forge GUI Selector Alpha parity.
- `v1.2.2`: Fabric and Forge Ghost Preview Alpha parity.
- `v1.2.3`: Fabric and Forge Survival Material Cost Alpha parity.
- `v1.2.4`: Fabric and Forge Material Refund Undo Alpha parity.
- `v1.2.5+`: deeper material UX, GUI material summaries, and follow-up
  stabilization.

## Current Risks

- Fabric networking and rendering differ from NeoForge and require their own
  adapter code.
- Forge and NeoForge are related but not API-identical, so Forge cannot be
  treated as a drop-in copy.
- Ghost Preview needs loader-specific rendering. Fabric and Forge now have
  Ghost Preview Alpha renderers, but they intentionally render only an outline
  and footprint.
- Registry lookup, inventory mutation, and world placement must remain in each
  loader adapter to avoid leaking Minecraft runtime types into common core.
- Fabric and Forge Material Refund Undo is Alpha as of v1.2.4. Refunded items
  that do not fit in the player inventory drop near the player.
- Fabric and Forge still do not support nearby chest sourcing, GUI material
  icons, collision-aware preview, or BlockEntity NBT undo.

## v1.1.3 Status

- All three connector modules exist: `mod/neoforge-connector`,
  `mod/fabric-connector`, and `mod/forge-connector`.
- NeoForge remains the recommended and most complete in-game target.
- Fabric has a command-only Alpha under `mod/fabric-connector`.
- Fabric Alpha command-loop manual testing passed for example install, reload, list, dryrun, build, rotated `state_test_house`, undo, and invalid blueprint id handling.
- Forge has a command-only Alpha under `mod/forge-connector`.
- Forge Alpha command-loop manual testing passed for example install, reload,
  list, dryrun, build, rotated `state_test_house`, undo, and invalid blueprint
  id handling.
- Fabric and Forge Alpha both reuse common blueprint parsing, rotation, and build planning data.
- Fabric and Forge Alpha do not include GUI, Ghost Preview, Builder Wand, survival material costs, inventory transactions, material refunds, or BlockEntity NBT undo.
- Forge and Fabric Alpha undo restoration suppresses drops during block-state rollback.
- v1.1.3 aligns versions to `1.1.3-alpha.1`, uses loader-specific jar names,
  and publishes CI artifacts for NeoForge, Fabric, and Forge.

## v1.2.0 Status

- Fabric and Forge add Builder Wand Alpha support without GUI, Ghost Preview,
  survival material cost, material refund undo, or client networking.
- Fabric and Forge players can use `/blockforge select`, `/blockforge selected`,
  `/blockforge rotate`, and `/blockforge wand`.
- Fabric and Forge wand placement reuses the existing loader-specific placer and
  per-player undo history flow.
- Player selection state is in-memory and can be lost when a player disconnects
  or the server restarts.
- Wand placement has a 2 second per-player cooldown. Command builds are not
  throttled by this cooldown.

## v1.2.1 Status

- Fabric and Forge add GUI Selector Alpha support for selecting a blueprint and
  rotation.
- The GUI opens through `/blockforge gui` or the default `B` key.
- Fabric and Forge use loader-specific networking payloads to request blueprint
  lists, submit selection requests, and receive server-validated selection
  results.
- The server validates blueprint id existence and rotation before updating the
  player selection manager.
- GUI selections feed the same selection state used by `/blockforge selected`
  and Builder Wand placement.
- Fabric and Forge still do not include Ghost Preview, survival material cost,
  material refund undo, advanced thumbnails, blueprint editing, or Web sync.
- Fabric / Forge GUI and Builder Wand manual Minecraft testing is pending.

## v1.2.2 Status

- Fabric and Forge add Ghost Preview Alpha support for Builder Wand selections.
- Preview selection state is server-confirmed: the server sends blueprint id,
  name, size, and rotation through loader-specific payloads.
- The client computes `base position = clicked block + clicked side`, matching
  the Builder Wand placement path.
- The preview renders a rotation-aware bounding box and ground footprint only.
- Failed or invalid selection requests clear the client preview and surface the
  server error message.
- MVP validity checks cover holding the Builder Wand, having a selection,
  looking at a block, and world height bounds.
- Fabric and Forge still do not include collision scanning, material status,
  per-block transparent previews, texture previews, survival material cost,
  material refund undo, blueprint editing, or Web sync.
- Fabric / Forge Ghost Preview manual Minecraft testing is pending.

## v1.2.3 Status

- Fabric and Forge add Survival Material Cost Alpha support for command builds
  and Builder Wand builds.
- `/blockforge materials <id>` and `/blockforge materials selected` report
  required items, available items, missing item types, and missing materials.
- Common core provides loader-neutral material aggregation helpers. Fabric and
  Forge still own registry lookup, block-to-item mapping, inventory scans, and
  item consumption.
- Creative players bypass material consumption. Survival players must have the
  required items. Adventure and Spectator builds are rejected by the Alpha
  material gate.
- Fabric and Forge run a dry-run placement precheck before consuming materials.
- `/blockforge undo` restores blocks only and does not refund materials yet.
- GUI material summary is planned for a later release; v1.2.3 exposes material reports
  through commands.
- Fabric / Forge Survival Material Cost manual Minecraft testing is pending.

## v1.2.4 Status

- Fabric and Forge add Material Refund Undo Alpha support for command builds
  and Builder Wand builds.
- Common core provides `MaterialRefundResult` alongside existing
  `ConsumedMaterialEntry` and `MaterialTransaction` DTOs.
- Fabric and Forge undo snapshots now attach the material transaction recorded
  during survival builds.
- `/blockforge undo` restores blocks first, then refunds consumed survival
  materials. If the inventory is full, overflow items drop near the player.
- Creative builds create a creative-bypass transaction with no consumed items,
  so undo reports that no materials were consumed.
- If material consumption succeeds but placement does not produce an undo
  snapshot, Fabric and Forge roll back the consumed materials.
- Fabric and Forge still do not include nearby chest sourcing, recipe
  substitutions, GUI material icons, or BlockEntity NBT undo.
- Fabric / Forge Material Refund Undo manual Minecraft testing is pending.
