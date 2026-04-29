# BlockForge Connector

BlockForge Connector is a minimal NeoForge 1.21.1 mod that reads Blueprint JSON
files exported by the BlockForge web app and places them in-game with commands.

This MVP includes command placement, a basic Builder Wand, in-memory undo
snapshots, undo material refunds, a Ghost Preview candidate, and a Blueprint
Selector GUI. It does not include blueprint editing, external storage, or live
Web integration yet.

## Target

- Minecraft Java Edition: `1.21.1`
- NeoForge: `21.1.227`
- Java: `21`
- Mod ID: `blockforge_connector`
- Mod Name: `BlockForge Connector NeoForge`
- Mod Version: `1.6.0-alpha.1`

## Multi-loader Architecture Status

BlockForge v1.1.0 introduced `mod/common` as a loader-neutral common core for
future Fabric and Forge connectors. NeoForge remains the current stable and most
complete target.

The NeoForge Connector now reuses common blueprint parsing, rotation, material
counting, and build planning data where the contracts are stable. NeoForge still
owns command registration, item registration, GUI screens, payload networking,
Ghost Preview rendering, common config registration, real `setBlock` placement,
inventory checks, and material consumption/refund integration.

Fabric Connector Alpha exists under `mod/fabric-connector`, and Forge
Connector Alpha exists under `mod/forge-connector`. Both reuse common blueprint
parsing, rotation, build planning, preview DTOs, and material source groundwork.
They now include Builder Wand, GUI Selector, Ghost Preview, survival material
cost, material refund undo, and nearby chest material sourcing as Alpha
features. They do not include BlockEntity NBT undo yet.

v1.3.5 keeps NeoForge nearby container material sourcing Alpha and brings the
same source model to Fabric / Forge. It is disabled by default in common
config. When enabled, NeoForge scans loaded nearby
containers through item handler capability, consumes materials by configured
source priority, and tries to refund materials to their original containers on
undo.

v1.4.0 adds Blueprint Pack loading Alpha from `config/blockforge/packs/`.
v1.6.0 adds Sponge `.schem` v3 import Alpha from
`config/blockforge/schematics/`.

## Blueprint Protocol Support

- Blueprint v1: simple palette keys mapped to Minecraft block ids.
- Blueprint v2: palette entries with `name` and optional BlockState `properties`.

The connector converts both versions into one internal blueprint model before
placement.

## Build

Linux/macOS:

```bash
./gradlew build
```

Windows:

```powershell
gradlew.bat build
```

The built jar is written to:

```text
build/libs/blockforge-connector-neoforge-1.6.0-alpha.1.jar
```

## Blueprint Folder

At runtime, the mod reads blueprint files from:

```text
.minecraft/config/blockforge/blueprints/
```

The folder is created automatically when the server starts or when blueprints
are reloaded.

## Blueprint Pack Folder

NeoForge also reads Blueprint Pack zip files from:

```text
.minecraft/config/blockforge/packs/
```

Pack blueprint ids use `packId/blueprintId`, for example
`starter_buildings/tiny_platform`.

## Schematic Folder

NeoForge also reads Sponge `.schem` v3 files from:

```text
.minecraft/config/blockforge/schematics/
```

Schematic blueprint ids use `schem/<file>`, for example `schem/tiny_platform`.

Supported file names:

- `*.blueprint.json`
- `*.json`

Recommended naming:

```text
blockforge-<id>.blueprint.json
```

## Built-In Examples

The mod jar includes example Blueprint files:

- `tiny_platform`
- `small_test_house`
- `medieval_tower`
- `state_test_house`

List built-in examples:

```mcfunction
/blockforge examples list
```

Install built-in examples into the blueprint folder:

```mcfunction
/blockforge examples install
```

Existing files are not overwritten. After installing examples, reload the
registry:

```mcfunction
/blockforge reload
```

## Commands

```mcfunction
/blockforge folder
/blockforge examples list
/blockforge examples install
/blockforge reload
/blockforge packs folder
/blockforge packs reload
/blockforge packs list
/blockforge packs info <packId>
/blockforge packs blueprints <packId>
/blockforge packs validate
/blockforge schematics folder
/blockforge schematics reload
/blockforge schematics list
/blockforge schematics info <id>
/blockforge schematics validate
/blockforge list
/blockforge select <id>
/blockforge selected
/blockforge rotate <0|90|180|270>
/blockforge wand
/blockforge gui
/blockforge materials <id>
/blockforge materials selected
/blockforge sources scan
/blockforge sources selected
/blockforge undo
/blockforge undo list
/blockforge undo clear
/blockforge info <id>
/blockforge dryrun <id>
/blockforge build <id>
/blockforge build <id> <x> <y> <z>
/blockforge build <id> rotate <0|90|180|270>
/blockforge build <id> at <x> <y> <z> rotate <0|90|180|270>
```

Permissions:

- `build` requires permission level `2`.
- `reload` requires permission level `2`.
- `wand` requires permission level `2`.
- `undo`, `undo clear`, and `packs reload` require permission level `2`.
- `folder`, `list`, `info`, and `dryrun` are available to regular players.
- `packs folder/list/info/blueprints/validate` are available to regular players.
- `gui` is available to regular players.
- `materials` is available to regular players.
- `sources scan` and `sources selected` are available to regular players.

## Builder Wand MVP

The Builder Wand places the currently selected blueprint when a player right-clicks
a block.

Setup:

```mcfunction
/blockforge select state_test_house
/blockforge rotate 90
/blockforge wand
```

Then hold the Builder Wand and right-click a block. Placement uses:

```text
basePos = clickedPos.relative(clickedFace)
```

Rules:

- Actual placement runs on the server only.
- Permission level `2` is required.
- Player selection is stored in memory and is not persisted.
- The wand has a 2 second per-player cooldown.
- Placement reuses `BlueprintPlacer`.
- Successful placements are saved as in-memory undo snapshots.

Undo the latest placement:

```mcfunction
/blockforge undo list
/blockforge undo
/blockforge undo clear
```

Undo restores previous block states and attempts to restore BlockEntity NBT when
an overwritten block entity was captured. By default, BlockEntity targets are
protected and skipped instead of overwritten.

If a survival build consumed materials, undo also refunds the recorded material
transaction. If the player's inventory is full, leftover refunded items are
dropped near the player.

## Common Config

The Connector writes a NeoForge common config file:

```text
.minecraft/config/blockforge_connector-common.toml
```

Default settings:

- `maxBlocksPerBuild`: `10000`
- `wandCooldownSeconds`: `2`
- `maxUndoSnapshotsPerPlayer`: `5`
- `allowReplaceNonAir`: `true`
- `protectBlockEntities`: `true`
- `requireMaterialsInSurvival`: `true`
- `creativeModeBypassesMaterials`: `true`
- `allowBuildInAdventureMode`: `false`
- `allowBuildInSpectatorMode`: `false`
- `enableNearbyContainers`: `false`
- `nearbyContainerSearchRadius`: `8`
- `nearbyContainerMaxScanned`: `64`
- `materialSourcePriority`: `PLAYER_FIRST`
- `returnRefundsToOriginalSource`: `true`
- `allowPartialFromContainers`: `true`

Defaults keep nearby container sourcing off unless explicitly enabled.

## Ghost Preview MVP Candidate

When the player holds the Builder Wand and has selected a blueprint, the physical
client draws a preview around the block the player is looking at.

Setup:

```mcfunction
/blockforge select tiny_platform
/blockforge rotate 90
/blockforge wand
```

Preview behavior:

- The client receives selected blueprint metadata from the server through a small payload.
- The preview position uses `clickedPos.relative(clickedFace)`, matching Builder Wand placement.
- The preview renders a translucent bounding box and ground footprint.
- Cyan indicates a valid lightweight preview; red indicates an invalid height range or missing size.
- The preview does not create blocks, entities, or saved world data.
- Actual placement still happens only when right-clicking and is handled by the server.

Current Ghost Preview limits:

- No full collision scan.
- No material or protected-block scan.
- No sampled voxel rendering yet.
- No `/blockforge preview on|off` command yet.

## Blueprint Selector GUI MVP

Open the in-game selector with:

```mcfunction
/blockforge gui
```

Or use the default keybind:

```text
B
```

The GUI syncs the server's loaded blueprint list to the client and shows:

- Blueprint name and id.
- Size.
- Block count.
- Schema version.
- Whether the blueprint uses BlockState properties.
- Rotation buttons for `0°`, `90°`, `180°`, and `270°`.
- Select and Close buttons.

Selection flow:

1. The client requests the loaded blueprint list.
2. The player picks a blueprint and rotation locally.
3. The client sends a selection request to the server.
4. The server validates the blueprint id and rotation.
5. The server updates `PlayerSelectionManager`.
6. The server sends `SelectedBlueprintPayload` back to the player.
7. Ghost Preview and Builder Wand use the updated state.

Current GUI limits:

- No blueprint editing.
- No thumbnails.
- No search or advanced filtering.
- No paging beyond the first visible rows.

## Material Requirements MVP

Check the material requirements for a blueprint:

```mcfunction
/blockforge materials tiny_platform
/blockforge materials selected
```

Rules:

- Creative mode bypasses material checks and consumes nothing.
- Survival mode requires enough matching items before building.
- Adventure mode builds are blocked by default.
- Spectator mode builds are blocked by default.
- Command builds and Builder Wand builds use the same material gate.
- Material cost mode is currently `simple`: one placed block costs one item from `block.asItem()`.

Build flow:

1. Generate a material report from the selected blueprint.
2. Check the player's inventory.
3. If nearby containers are enabled, scan loaded nearby containers through
   `IItemHandler` capability.
4. Refuse the build if materials are missing across the configured sources.
5. Consume materials through a source-aware material transaction if enough are
   available.
6. Place the blueprint through `BlueprintPlacer`.
7. Record both the block snapshot and material transaction for undo.

Undo flow:

1. Restore previous world blocks from the placement snapshot.
2. Refund consumed survival materials from the material transaction.
3. If the transaction came from nearby containers and
   `returnRefundsToOriginalSource=true`, try the original container first.
4. Fall back to player inventory, then drop overflow near the player.

Known limits:

- Undo refunds BlockForge material transactions, but does not restore XP, currency, or external economy state.
- Nearby container material sourcing is Alpha, disabled by default, and pending
  v1.3.5 manual Minecraft regression.
- Blueprint Pack loading is Alpha, pending v1.4.0 manual Minecraft regression.
- No special cost table for doors, fluids, torches, or multi-block placements.
- No material icons in the GUI yet.
- Fabric and Forge have Alpha parity for core builder flows and nearby
  container sourcing, but still use simpler config surfaces.

Manual Minecraft testing before v1.0.0-rc.1 verified survival undo refunds and
full-inventory refund drops. The v1.0 RC also passed a smoke test for client
launch and the core Connector flow after common config registration. v1.1.x
keeps NeoForge as the most complete Connector while Fabric and Forge continue
as Alpha connectors.

## Usage Example

1. Export `Blueprint JSON` from the BlockForge web app.
2. Copy the file into `.minecraft/config/blockforge/blueprints/`.
3. Start Minecraft with NeoForge and this mod installed.
4. Run:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge dryrun medieval_tower
/blockforge build medieval_tower
```

To place at a specific position:

```mcfunction
/blockforge build medieval_tower 100 64 100
```

## Manual Testing

See the root repository checklist:

```text
docs/MOD_CONNECTOR_TESTING.md
```

Recommended first in-game test:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge dryrun tiny_platform
/blockforge build tiny_platform
/blockforge build state_test_house rotate 90
```

## Current Limits

- Maximum blueprint size is `10000` blocks.
- No mirroring.
- No centering offset.
- Basic rotation supports coordinates and horizontal `facing` only.
- BlockState support is limited to string properties accepted by the target Minecraft block.
- Builder Wand selection is not persisted.
- Undo history is in-memory and is not persisted.
- Builder Wand uses the same material transaction flow as command builds.
- BlockEntity positions are protected by default.
- Ghost Preview is a client-side candidate and still needs in-game validation.
- Blueprint Selector GUI is an MVP and still needs in-game validation.
- Undo refunds recorded survival materials, but the history is still in-memory only.
- Nearby container sourcing does not load chunks, does not cross dimensions, and
  only uses inventories exposed through item handler capability.
- No air clearing.
- Invalid palette entries are skipped.
- Invalid Minecraft block ids are skipped.
- Invalid BlockState properties are skipped.
- Out-of-world Y coordinates are skipped.
- Java Edition only.

## Roadmap

- Ghost Preview placement overlay.
- Rotation and mirroring.
- Block state support.
- Configurable build limits.
- Better in-game diagnostics.
## v1.5.0 Security Alpha

NeoForge now loads `config/blockforge/protection-regions.json` and runs
BlockForge permission/protection preflight before build commands, Builder Wand
placement, material consumption, and nearby-container material use. External
claim/permission integrations are planned; current behavior falls back to
vanilla permission levels when permission enforcement is enabled.
# v3.1.0-alpha.1 GUI Search Notes

- `/blockforge gui` opens the Alpha selector with search, pagination, source
  filtering, warning filtering, sorting, source tags, warning badges, material
  summary access, and rotation controls.
- GUI results are queried on the server with `BlueprintGuiQueryService`.
- Selection remains server-validated and continues to update Builder Wand and
  Ghost Preview state.
- Minecraft manual regression is pending.

## v3.1.0-alpha.1 Web Rendering Note

Connector logic is unchanged in this release. Version is synchronized with the
Web Rendering Performance + Screenshot Export Alpha.

## v3.1.0-alpha.1 Notes

- Version aligned to 3.1.0-alpha.1.
- Server diagnostics and dedicated server documentation are Alpha documentation items for this train.
- Core connector gameplay logic is unchanged in the Web productization pass.
- Minecraft manual regression and dedicated server smoke testing remain pending.



## v3.1.0-alpha.1 Gameplay Tools Notes

- Adds Blueprint Table, Material Cache, and Builder Anchor block/item registration.
- Blueprint Table opens the existing Blueprint Selector GUI from in-world right-click.
- Builder Anchor binds the player's Builder Wand state to the anchor coordinate.
- Builder Wand supports shared advanced mode state; sneak + right-click cycles modes.
- Material Cache inventory-backed sourcing is registered as Alpha follow-up work.
- Minecraft manual regression and dedicated server smoke testing remain pending.
