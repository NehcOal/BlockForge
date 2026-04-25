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
- Mod Name: `BlockForge Connector`
- Mod Version: `1.0.0-rc.1`

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
build/libs/
```

## Blueprint Folder

At runtime, the mod reads blueprint files from:

```text
.minecraft/config/blockforge/blueprints/
```

The folder is created automatically when the server starts or when blueprints
are reloaded.

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
/blockforge list
/blockforge select <id>
/blockforge selected
/blockforge rotate <0|90|180|270>
/blockforge wand
/blockforge gui
/blockforge materials <id>
/blockforge materials selected
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
- `undo` and `undo clear` require permission level `2`.
- `folder`, `list`, `info`, and `dryrun` are available to regular players.
- `gui` is available to regular players.
- `materials` is available to regular players.

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

Defaults match the behavior validated before the v1.0 release candidate.

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

- Manual Minecraft testing is pending.
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
- Manual Minecraft testing is pending for v0.8.

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
3. Refuse the build if materials are missing.
4. Consume materials through a material transaction if enough are available.
5. Place the blueprint through `BlueprintPlacer`.
6. Record both the block snapshot and material transaction for undo.

Undo flow:

1. Restore previous world blocks from the placement snapshot.
2. Refund consumed survival materials from the material transaction.
3. Drop refunded items near the player when the inventory is full.

Known v1.0.0-rc.1 limits:

- Undo refunds BlockForge material transactions, but does not restore XP, currency, or external economy state.
- No nearby chest or warehouse support.
- No special cost table for doors, fluids, torches, or multi-block placements.
- No material icons in the GUI yet.

Manual Minecraft testing before v1.0.0-rc.1 verified survival undo refunds and
full-inventory refund drops. The v1.0 RC also passed a smoke test for client
launch and the core Connector flow after common config registration.

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
