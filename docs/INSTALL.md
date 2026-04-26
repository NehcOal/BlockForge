# BlockForge Install Guide

This guide covers the BlockForge web app and the Minecraft Java Connector jars
for NeoForge, Fabric, and Forge.

## Versions

- BlockForge Web: `1.2.1-alpha.1`
- Minecraft Java Edition: `1.21.1`
- Java: `21`
- NeoForge: `21.1.227`
- Fabric Loader: `0.19.2`
- Fabric API: `0.116.11+1.21.1`
- Forge: `52.1.14`

## Choose A Loader

NeoForge is the recommended complete in-game experience. Fabric and Forge are
Alpha connectors with command builds, GUI Selector Alpha, and Builder Wand Alpha placement.

| Connector | Best For | Current Status |
|---|---|---|
| NeoForge | GUI Selector, Builder Wand, Ghost Preview, survival materials, material refund undo | Most complete |
| Fabric Alpha | Command reload/list/dryrun/build/undo, GUI Selector Alpha, and Builder Wand Alpha validation | Alpha |
| Forge Alpha | Command reload/list/dryrun/build/undo, GUI Selector Alpha, and Builder Wand Alpha validation | Alpha |

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
mod/neoforge-connector/build/libs/blockforge-connector-neoforge-1.2.1-alpha.1.jar
mod/fabric-connector/build/libs/blockforge-connector-fabric-1.2.1-alpha.1.jar
mod/forge-connector/build/libs/blockforge-connector-forge-1.2.1-alpha.1.jar
```

Copy the matching jar into the Minecraft instance `mods` folder.

## Blueprint Folder

All three connectors read blueprint files from:

```text
.minecraft/config/blockforge/blueprints/
```

The folder is created automatically when the mod starts or when blueprints are
reloaded.

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
/blockforge build tiny_platform
/blockforge undo
/blockforge select tiny_platform
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

## Fabric / Forge GUI + Wand Alpha

Fabric and Forge can open the Blueprint Selector with:

```mcfunction
/blockforge gui
```

You can also press the default `B` key. The Alpha GUI syncs the blueprint list
from the server, lets the player choose a blueprint and rotation, and updates
the same selection state used by the Builder Wand.

## Fabric / Forge Alpha Limits

Fabric and Forge Alpha support command builds, Builder Wand Alpha placement, and
block undo. They do not support Ghost Preview, survival material costs, material
refund undo, or BlockEntity NBT undo yet.

## Release Artifacts

A BlockForge v1.2.1-alpha.1 release should include:

- Web source release from the GitHub tag.
- `blockforge-connector-neoforge-1.2.1-alpha.1.jar`
- `blockforge-connector-fabric-1.2.1-alpha.1.jar`
- `blockforge-connector-forge-1.2.1-alpha.1.jar`
- `examples/blueprints/`
- `docs/BLUEPRINT_PROTOCOL.md`
- `docs/MOD_CONNECTOR_TESTING.md`
- `docs/RELEASE_NOTES_TEMPLATE.md`

GitHub Actions uploads the three Connector jars as separate CI artifacts.
