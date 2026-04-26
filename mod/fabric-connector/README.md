# BlockForge Connector Fabric Alpha

BlockForge Connector Fabric is the command-only Fabric alpha for BlockForge
Blueprint JSON placement. It proves the minimum Fabric loop for loading,
listing, dry-running, building, and undoing blueprints without porting the
NeoForge GUI, Ghost Preview, Builder Wand, or survival material system yet.

## Target

- Minecraft Java Edition: `1.21.1`
- Fabric Loader: `0.19.2`
- Fabric API: `0.116.11+1.21.1`
- Java: `21`
- Mod ID: `blockforge_connector`
- Mod Name: `BlockForge Connector Fabric`
- Mod Version: `1.1.3-alpha.1`

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
build/libs/blockforge-connector-fabric-1.1.3-alpha.1.jar
```

## Blueprint Folder

Fabric reads the same BlockForge blueprint directory as the NeoForge Connector:

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

The Fabric jar includes:

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
/blockforge info <id>
/blockforge dryrun <id>
/blockforge build <id>
/blockforge build <id> <x> <y> <z>
/blockforge build <id> rotate <0|90|180|270>
/blockforge undo
```

Permissions:

- Permission level `2`: `build`, `reload`, `examples install`, `undo`.
- Regular players: `folder`, `list`, `info`, `dryrun`, `examples list`.

## What Fabric Alpha Supports

- Loads BlockForge Blueprint JSON from the shared config folder.
- Installs bundled example blueprints.
- Lists and inspects loaded blueprints.
- Runs command dry-runs with placement statistics.
- Builds blueprints at the player position or explicit coordinates.
- Reuses common coordinate rotation and horizontal `facing` rotation.
- Records the latest per-player block-state snapshot.
- Restores the latest Fabric build with `/blockforge undo`.

## Current Limits

- No GUI.
- No Ghost Preview.
- No Builder Wand.
- No material requirements, inventory consumption, or refunds.
- No BlockEntity NBT snapshot or restore.
- No persistence for undo snapshots.
- No Fabric networking or client renderer yet.
- No protected block entity checks in the Alpha placer.
- Command-loop manual Minecraft testing has passed for the Alpha command flow.

## Difference From NeoForge

NeoForge remains the most complete Connector. It currently owns the Blueprint
Selector GUI, Ghost Preview, Builder Wand, common config, survival materials,
inventory transactions, and undo material refunds. Fabric and Forge Alpha are
intentionally smaller and command-only so each loader adapter can stabilize
before feature parity work begins.
