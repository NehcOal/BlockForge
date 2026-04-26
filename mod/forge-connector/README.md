# BlockForge Connector Forge Alpha

BlockForge Connector Forge is the Forge alpha for BlockForge
Blueprint JSON placement. It proves the minimum Forge loop for loading,
listing, dry-running, building, Builder Wand placement, and undoing blueprints
with an Alpha GUI Selector, Ghost Preview outline, Survival Material Cost, and
Material Refund Undo. v1.3.0 adds nearby material source common-core models only; this connector does not scan or consume nearby containers yet.

## Target

- Minecraft Java Edition: `1.21.1`
- Forge: `52.1.14`
- Java: `21`
- Mod ID: `blockforge_connector`
- Mod Name: `BlockForge Connector Forge`
- Mod Version: `1.3.0-alpha.1`

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
build/libs/blockforge-connector-forge-1.3.0-alpha.1.jar
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
/blockforge gui
/blockforge materials <id>
/blockforge materials selected
/blockforge info <id>
/blockforge dryrun <id>
/blockforge build <id>
/blockforge build <id> <x> <y> <z>
/blockforge build <id> rotate <0|90|180|270>
/blockforge undo
```

Permissions:

- Permission level `2`: `build`, `reload`, `examples install`, `undo`, `wand`.
- Regular players: `folder`, `list`, `info`, `dryrun`, `materials`, `examples list`, `select`, `selected`, `rotate`, `gui`.

## What Forge Alpha Supports

- Loads BlockForge Blueprint JSON from the shared config folder.
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
- Bypasses material consumption in creative mode.
- Places the selected blueprint with the Builder Wand by right-clicking a block.
- Records per-player block-state snapshots in an in-memory undo history.
- Restores recent Forge builds with repeated `/blockforge undo` calls.

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
Forge places the selected blueprint at `clickedPos.relative(clickedFace)`. The wand has a 2 second
cooldown per player. Command builds are not throttled by the wand cooldown. Run
`/blockforge undo` to restore the most recent wand or command placement and
refund consumed survival materials; repeat the command to walk backward through
that player's in-memory history.

In survival mode, Forge checks and consumes the required blueprint materials
before command or Builder Wand builds. Creative mode consumes nothing. Adventure
and Spectator mode builds are rejected by the Alpha material gate.

## Current Limits

- Ghost Preview only renders a bounding box and ground footprint.
- No collision scan, material status, per-block transparent preview, or texture preview.
- Failed or invalid selection requests clear the client preview and show the server error message.
- Material refund undo is Alpha; inventory overflow is dropped near the player.
- No GUI material summary yet; use `/blockforge materials <id>` or `/blockforge materials selected`.
- No nearby chest material sourcing or recipe substitutions.
- No BlockEntity NBT snapshot or restore.
- No persistence for undo snapshots.
- Undo history is capped at 20 snapshots per player.
- GUI Selector is Alpha and only syncs blueprint list, selected blueprint, and rotation.
- If the default `B` key conflicts, change it in Minecraft Controls.
- No protected block entity checks in the Alpha placer.
- Command-loop manual Minecraft testing has passed for the Alpha command flow.
- GUI Selector, Builder Wand, Ghost Preview, Survival Material Cost, and Material Refund Undo parity manual Minecraft testing is pending.

## Difference From NeoForge And Fabric

NeoForge remains the most complete Connector. It currently owns common config,
material refund undo, and deeper material behavior.

Fabric and Forge Alpha are intentionally smaller and parallel in scope: they
prove each loader can reuse `mod/common` for blueprint parsing, rotation, build
planning, selection state, basic GUI networking, and preview state before deeper parity work begins.
