# BlockForge Connector Forge Alpha

BlockForge Connector Forge is the command-only Forge alpha for BlockForge
Blueprint JSON placement. It proves the minimum Forge loop for loading,
listing, dry-running, building, Builder Wand placement, and undoing blueprints
without porting the NeoForge GUI, Ghost Preview, or survival material system yet.

## Target

- Minecraft Java Edition: `1.21.1`
- Forge: `52.1.14`
- Java: `21`
- Mod ID: `blockforge_connector`
- Mod Name: `BlockForge Connector Forge`
- Mod Version: `1.2.0-alpha.1`

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
build/libs/blockforge-connector-forge-1.2.0-alpha.1.jar
```

## Blueprint Folder

Forge reads the same BlockForge blueprint directory as the NeoForge and Fabric
Connectors:

```text
.minecraft/config/blockforge/blueprints/
```

The folder is created automatically by reload and example installation flows.

Supported file names:

- `*.blueprint.json`
- `*.json`

Blueprint v1 and v2 JSON are parsed through `mod/common`. Blueprint v2
properties are applied when the target Minecraft block supports the property and
value; invalid properties cause that block placement to be skipped and counted
as `skippedInvalidProperties`.

## Built-In Examples

The Forge jar includes:

- `tiny_platform`
- `small_test_house`
- `state_test_house`
- `medieval_tower`

Commands:

```mcfunction
/blockforge examples list
/blockforge examples install
/blockforge reload
```

Existing files are skipped and not overwritten.

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
/blockforge info <id>
/blockforge dryrun <id>
/blockforge build <id>
/blockforge build <id> <x> <y> <z>
/blockforge build <id> rotate <0|90|180|270>
/blockforge undo
```

Permissions:

- Permission level `2`: `build`, `reload`, `examples install`, `undo`, `wand`.
- Regular players: `folder`, `list`, `info`, `dryrun`, `examples list`, `select`, `selected`, `rotate`.

## What Forge Alpha Supports

- Loads BlockForge Blueprint JSON from the shared config folder.
- Installs bundled example blueprints.
- Lists and inspects loaded blueprints.
- Runs command dry-runs with placement statistics.
- Builds blueprints at the player position or explicit coordinates.
- Reuses common coordinate rotation and horizontal `facing` rotation.
- Selects a blueprint and rotation for Builder Wand placement.
- Gives `blockforge_connector:builder_wand` through `/blockforge wand`.
- Places the selected blueprint with the Builder Wand by right-clicking a block.
- Records the latest per-player block-state snapshot.
- Restores the latest Forge build with `/blockforge undo`.

## Builder Wand Alpha Flow

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge select tiny_platform
/blockforge rotate 90
/blockforge wand
```

Hold the Builder Wand and right-click a block. Forge places the selected
blueprint at `clickedPos.relative(clickedFace)`. The wand has a 2 second
cooldown per player. Command builds are not throttled by the wand cooldown. Run
`/blockforge undo` to restore the latest wand or command placement.

## Current Limits

- No GUI.
- No Ghost Preview.
- No material requirements, inventory consumption, or refunds.
- No BlockEntity NBT snapshot or restore.
- No persistence for undo snapshots.
- No Forge networking or client renderer yet.
- No protected block entity checks in the Alpha placer.
- Command-loop manual Minecraft testing has passed for the Alpha command flow.

## Difference From NeoForge And Fabric

NeoForge remains the most complete Connector. It currently owns the Blueprint
Selector GUI, Ghost Preview, Builder Wand, common config, survival materials,
inventory transactions, and undo material refunds.

Fabric and Forge Alpha are intentionally command-only and parallel in scope:
they prove each loader can reuse `mod/common` for blueprint parsing, rotation,
and build planning before feature parity work begins.
