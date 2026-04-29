# BlockForge Connector Fabric Alpha

BlockForge Connector Fabric is the Fabric alpha for BlockForge
Blueprint JSON placement. It proves the minimum Fabric loop for loading,
listing, dry-running, building, Builder Wand placement, and undoing blueprints
with an Alpha GUI Selector, Ghost Preview outline, Survival Material Cost, and
Material Refund Undo. v1.3.5 adds Fabric nearby container sourcing Alpha.
v1.4.0 adds Blueprint Pack loading Alpha from `config/blockforge/packs/`.
v1.6.0 adds Sponge `.schem` v3 import Alpha from
`config/blockforge/schematics/`.

## Target

- Minecraft Java Edition: `1.21.1`
- Fabric Loader: `0.19.2`
- Fabric API: `0.116.11+1.21.1`
- Java: `21`
- Mod ID: `blockforge_connector`
- Mod Name: `BlockForge Connector Fabric`
- Mod Version: `1.6.0-alpha.1`

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
build/libs/blockforge-connector-fabric-1.6.0-alpha.1.jar
```

## Blueprint Folder

Fabric reads the same BlockForge blueprint directory as the NeoForge Connector:

```text
.minecraft/config/blockforge/blueprints/
```

The folder is created automatically by reload and example installation flows.

## Blueprint Pack Folder

Fabric also reads pack zips from:

```text
.minecraft/config/blockforge/packs/
```

Pack blueprint ids use `packId/blueprintId`, for example
`starter_buildings/tiny_platform`.

## Schematic Folder

Fabric also reads Sponge `.schem` v3 files from:

```text
.minecraft/config/blockforge/schematics/
```

Schematic blueprint ids use `schem/<file>`, for example `schem/tiny_platform`.

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
/blockforge sources status
/blockforge sources enable
/blockforge sources disable
/blockforge sources priority <PLAYER_FIRST|CONTAINER_FIRST|PLAYER_ONLY|CONTAINER_ONLY>
/blockforge sources radius <1-32>
/blockforge sources scan
/blockforge sources selected
/blockforge info <id>
/blockforge dryrun <id>
/blockforge build <id>
/blockforge build <id> <x> <y> <z>
/blockforge build <id> rotate <0|90|180|270>
/blockforge undo
```

Permissions:

- Permission level `2`: `build`, `reload`, `packs reload`, `examples install`, `undo`, `wand`.
- Regular players: `folder`, `packs folder/list/info/blueprints/validate`, `list`, `info`, `dryrun`, `materials`, `sources`, `examples list`, `select`, `selected`, `rotate`, `gui`.

## What Fabric Alpha Supports

- Loads BlockForge Blueprint JSON from the shared config folder.
- Loads Blueprint Pack zip files from the shared pack folder.
- Installs bundled example blueprints.
- Lists and inspects loaded blueprints.
- Runs command dry-runs with placement statistics.
- Builds blueprints at the player position or explicit coordinates.
- Reuses common coordinate rotation and horizontal `facing` rotation.
- Selects a blueprint and rotation for Builder Wand placement.
- Opens an Alpha Blueprint Selector GUI with `/blockforge gui` or the default `B` key.
- Gives `blockforge_connector:builder_wand` through `/blockforge wand`.
- Shows a Ghost Preview Alpha bounding box and ground footprint while holding the Builder Wand.
- Reports material needs with `/blockforge materials <id>` and `/blockforge materials selected`.
- Rejects survival builds when required materials are missing.
- Consumes survival inventory items before command or Builder Wand placement.
- Refunds consumed survival materials when `/blockforge undo` restores the placement.
- Drops refund overflow near the player when the inventory is full.
- Can scan loaded nearby vanilla inventories as Alpha material sources when
  nearby containers are enabled.
- Can consume from nearby containers and refund to the original source when
  possible.
- Can enable or tune nearby source behavior at runtime with `/blockforge sources`.
- Can validate and list Blueprint Packs with `/blockforge packs`.
- Bypasses material consumption in creative mode.
- Places the selected blueprint with the Builder Wand by right-clicking a block.
- Records per-player block-state snapshots in an in-memory undo history.
- Restores recent Fabric builds with repeated `/blockforge undo` calls.

## GUI + Builder Wand + Ghost Preview Alpha Flow

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge gui
/blockforge materials selected
/blockforge wand
```

You can also press the default `B` key to open the selector. Pick a blueprint,
choose `0°`, `90°`, `180°`, or `270°`, then click Select. Hold the Builder Wand
and look at a block to see the Ghost Preview outline. Right-click to build.
Fabric places the selected blueprint at `clickedPos.offset(clickedSide)`. The wand has a 2 second cooldown
per player. Command builds are not throttled by the wand cooldown. Run
`/blockforge undo` to restore the most recent wand or command placement and
refund consumed survival materials; repeat the command to walk backward through
that player's in-memory history.

In survival mode, Fabric checks and consumes the required blueprint materials
before command or Builder Wand builds. Creative mode consumes nothing. Adventure
and Spectator mode builds are rejected by the Alpha material gate.

## Current Limits

- Ghost Preview only renders a bounding box and ground footprint.
- No collision scan, material status, per-block transparent preview, or texture preview.
- Failed or invalid selection requests clear the client preview and show the server error message.
- Material refund undo is Alpha; inventory overflow is dropped near the player.
- No GUI material summary yet; use `/blockforge materials <id>` or `/blockforge materials selected`.
- Nearby chest material sourcing is Alpha, disabled by default, and has no
  config file yet; `/blockforge sources enable|disable|priority|radius` changes
  runtime server settings for the current session only.
- Blueprint Pack loading is Alpha. Pack zips are read directly and are not
  extracted to disk.
- No recipe substitutions.
- No BlockEntity NBT snapshot or restore.
- No persistence for undo snapshots.
- Undo history is capped at 20 snapshots per player.
- GUI Selector is Alpha and only syncs blueprint list, selected blueprint, and rotation.
- If the default `B` key conflicts, change it in Minecraft Controls.
- No protected block entity checks in the Alpha placer.
- Command-loop manual Minecraft testing has passed for the Alpha command flow.
- GUI Selector, Builder Wand, Ghost Preview, Survival Material Cost, Material
  Refund Undo, nearby container sourcing, and Blueprint Pack loading manual
  Minecraft testing is pending for v1.4.0.

## Difference From NeoForge

NeoForge remains the most complete Connector. It currently owns common config
and deeper material behavior. Fabric and Forge Alpha are
intentionally smaller so each loader adapter can stabilize before deeper parity
work begins.
## v1.5.0 Security Alpha

Fabric now loads `config/blockforge/protection-regions.json` and runs
BlockForge permission/protection preflight before build commands, Builder Wand
placement, material consumption, and nearby-container material use. Fabric
Permissions API / LuckPerms integration is planned; the Alpha falls back to
vanilla permission levels.
# v4.3.0-beta.1 GUI Search Notes

- `/blockforge gui` and the default `B` key open the Alpha selector with
  search, pagination, source filtering, warning filtering, sorting, source tags,
  warning badges, and rotation controls.
- GUI results are queried on the server with `BlueprintGuiQueryService`.
- Selection remains server-validated and continues to update Builder Wand and
  Ghost Preview state.
- Minecraft manual regression is pending.

## v4.3.0-beta.1 Web Rendering Note

Connector logic is unchanged in this release. Version is synchronized with the
Web Rendering Performance + Screenshot Export Alpha.

## v4.3.0-beta.1 Notes

- Version aligned to 4.3.0-beta.1.
- Server diagnostics and dedicated server documentation are Alpha documentation items for this train.
- Core connector gameplay logic is unchanged in the Web productization pass.
- Minecraft manual regression and dedicated server smoke testing remain pending.



## v4.3.0-beta.1 Gameplay Tools Notes

- Adds Blueprint Table, Material Cache, and Builder Anchor block/item registration.
- Blueprint Table opens the existing Blueprint Selector GUI from in-world right-click.
- Builder Anchor binds the player's Builder Wand state to the anchor coordinate.
- Builder Wand supports shared advanced mode state; sneak + right-click cycles modes.
- Material Cache inventory-backed sourcing is registered as Alpha follow-up work.
- Minecraft manual regression and dedicated server smoke testing remain pending.


## v4.3.0-beta.1 Build Planner Notes

- Adds shared BuildPlan manager scaffolding for this loader.
- BuildPlan pure logic covers deterministic layer planning, validation, progress, and repair plan generation.
- NeoForge currently has the reference `/blockforge buildplan ...` command set.
- Real per-step world placement and Fabric/Forge command parity are planned v3.2 train polish.
- Minecraft manual regression and dedicated server smoke testing remain pending.
