# BlockForge Install Guide

This guide covers the BlockForge web app and the Minecraft Java Connector jars
for NeoForge, Fabric, and Forge.

## Versions

- BlockForge Web: `5.1.0-alpha.1`
- Minecraft Java Edition: `1.21.1`
- Java: `21`
- NeoForge: `21.1.227`
- Fabric Loader: `0.19.2`
- Fabric API: `0.116.11+1.21.1`
- Forge: `52.1.14`

## Choose A Loader

NeoForge is the recommended complete in-game experience. Fabric and Forge are
Alpha connectors with command builds, GUI Selector Alpha, Builder Wand Alpha
placement, Ghost Preview Alpha outlines, Survival Material Cost Alpha, and
Material Refund Undo Alpha. v1.3.1 adds nearby material source common-core
models plus NeoForge nearby chest sourcing Alpha. v1.3.5 extends nearby chest
sourcing Alpha to Fabric and Forge. v1.4.0 adds Blueprint Pack import/export
Alpha and pack loading from `config/blockforge/packs/`. v1.6.0 adds Sponge
`.schem` import/export Alpha and schematic loading from
`config/blockforge/schematics/`. Manual Minecraft regression for v1.6.0 is
pending.

| Connector | Best For | Current Status |
|---|---|---|
| NeoForge | GUI Selector, Builder Wand, Ghost Preview, survival materials, material refund undo, nearby chest sourcing Alpha | Most complete |
| Fabric Alpha | Command reload/list/dryrun/build/undo, GUI Selector Alpha, Builder Wand Alpha, Ghost Preview Alpha, survival material cost validation, refund undo, nearby chest sourcing Alpha | Alpha |
| Forge Alpha | Command reload/list/dryrun/build/undo, GUI Selector Alpha, Builder Wand Alpha, Ghost Preview Alpha, survival material cost validation, refund undo, nearby chest sourcing Alpha | Alpha |

Do not install multiple BlockForge connector jars into the same Minecraft
instance at once. Pick the jar that matches the loader for that instance.

## Run The Web App Locally

```bash
pnpm install
pnpm dev
```

Open:

```text
http://localhost:3000
```

Choose a preset, preview it in 3D, then export `Blueprint JSON v2` for the
Connector.

## Build Connector Jars

NeoForge:

```bash
cd mod/neoforge-connector
./gradlew build
```

Fabric:

```bash
cd mod/fabric-connector
./gradlew build
```

Forge:

```bash
cd mod/forge-connector
./gradlew build
```

Windows users can run `gradlew.bat build` in the same directories.

Expected release jar names:

```text
mod/neoforge-connector/build/libs/blockforge-connector-neoforge-5.1.0-alpha.1.jar
mod/fabric-connector/build/libs/blockforge-connector-fabric-5.1.0-alpha.1.jar
mod/forge-connector/build/libs/blockforge-connector-forge-5.1.0-alpha.1.jar
```

Copy the matching jar into the Minecraft instance `mods` folder.

## Blueprint Folder

All three connectors read blueprint files from:

```text
.minecraft/config/blockforge/blueprints/
```

The folder is created automatically when the mod starts or when blueprints are
reloaded.

## Blueprint Pack Folder

All three connectors also read Blueprint Pack zip files from:

```text
.minecraft/config/blockforge/packs/
```

Supported pack files:

```text
*.blockforgepack.zip
*.zip
```

Run:

```mcfunction
/blockforge packs validate
/blockforge packs list
/blockforge packs blueprints starter_buildings
/blockforge reload
```

## Schematic Folder

All three connectors also read Sponge `.schem` v3 files from:

```text
.minecraft/config/blockforge/schematics/
```

Run:

```mcfunction
/blockforge schematics validate
/blockforge schematics reload
/blockforge schematics list
/blockforge select schem/tiny_platform
```

Pack blueprint ids use `packId/blueprintId`, for example:

```mcfunction
/blockforge select starter_buildings/tiny_platform
```

## Install Example Blueprints

In game, run:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
```

## Command Alpha Flow

This command flow is supported on NeoForge, Fabric Alpha, and Forge Alpha:

```mcfunction
/blockforge folder
/blockforge examples list
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge info tiny_platform
/blockforge dryrun tiny_platform
/blockforge materials tiny_platform
/blockforge build tiny_platform
/blockforge undo
/blockforge select tiny_platform
/blockforge materials selected
/blockforge rotate 90
/blockforge wand
/blockforge build state_test_house rotate 90
/blockforge undo
```

## NeoForge Full Experience

Open the Blueprint Selector:

```mcfunction
/blockforge gui
```

You can also press the default `B` keybind.

Give yourself the Builder Wand:

```mcfunction
/blockforge wand
```

Hold the wand, look at a block, and right-click. Ghost Preview shows the target
placement area before you build.

In survival mode, NeoForge checks required materials before building. Undo
restores blocks and refunds recorded survival materials.

## Fabric / Forge GUI + Wand + Ghost Preview + Materials Alpha

Fabric and Forge can open the Blueprint Selector with:

```mcfunction
/blockforge gui
```

You can also press the default `B` key. The Alpha GUI syncs the blueprint list
from the server, lets the player choose a blueprint and rotation, and updates
the same selection state used by the Builder Wand.

Hold the Builder Wand and look at a block to see the Ghost Preview Alpha
outline. It renders a bounding box and ground footprint only; the preview does
not modify the world.

In survival mode, Fabric and Forge check required blueprint materials before
command or Builder Wand builds. Missing materials reject the build; enough
materials are consumed before placement. Creative mode consumes nothing. Undo
restores blocks and refunds recorded survival materials; inventory overflow
drops near the player.

## Fabric / Forge Alpha Limits

Fabric and Forge Alpha support command builds, Builder Wand Alpha placement, and
block undo with material refund Alpha. They do not support BlockEntity NBT undo,
nearby chest sourcing, recipe substitutions, or GUI material icons yet. Use
`/blockforge materials <id>` or `/blockforge materials selected` for material
reports; GUI material summary is planned later.

## Release Artifacts

A BlockForge v1.4.0-alpha.1 release should include:

- Web source release from the GitHub tag.
- `blockforge-connector-neoforge-1.4.0-alpha.1.jar`
- `blockforge-connector-fabric-1.4.0-alpha.1.jar`
- `blockforge-connector-forge-1.4.0-alpha.1.jar`
- `examples/blueprints/`
- `docs/BLUEPRINT_PROTOCOL.md`
- `docs/MOD_CONNECTOR_TESTING.md`
- `docs/RELEASE_NOTES_TEMPLATE.md`

GitHub Actions uploads the three Connector jars as separate CI artifacts.
## v1.5.0 Protection Regions

Server owners can optionally edit:

```text
config/blockforge/protection-regions.json
```

The file is created automatically on first connector startup or protection
reload. Builds denied by a region stop before materials are consumed.
# v5.1.0-alpha.1 Notes

- Expected jars are `blockforge-connector-neoforge-5.1.0-alpha.1.jar`,
  `blockforge-connector-fabric-5.1.0-alpha.1.jar`, and
  `blockforge-connector-forge-5.1.0-alpha.1.jar`.
- `/blockforge gui` opens the Alpha selector with search, pagination, source
  filtering, warning filtering, sorting, source tags, and warning badges.
- Minecraft manual regression is pending for this Alpha.
