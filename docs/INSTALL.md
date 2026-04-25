# BlockForge Install Guide

This guide covers the BlockForge web app and the NeoForge Connector release
candidate.

## Versions

- BlockForge Web: `1.0.0-rc.1`
- BlockForge Connector: `1.0.0-rc.1`
- Minecraft Java Edition: `1.21.1`
- NeoForge: `21.1.227`
- Java: `21`

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

## Install NeoForge 1.21.1

1. Install Minecraft Java Edition `1.21.1`.
2. Install Java `21`.
3. Install NeoForge for Minecraft `1.21.1`.
4. Create or open a NeoForge `1.21.1` test instance.

BlockForge Connector has been tested against NeoForge `21.1.227`.

## Install The BlockForge Connector Jar

1. Build the mod:

   ```bash
   cd mod/neoforge-connector
   ./gradlew build
   ```

   On Windows:

   ```powershell
   cd mod/neoforge-connector
   .\gradlew.bat build
   ```

2. Copy the generated jar from:

   ```text
   mod/neoforge-connector/build/libs/
   ```

3. Place the jar in your Minecraft instance `mods` folder.

## Blueprint Folder

BlockForge Connector reads blueprint files from:

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

## Use The GUI

Open the Blueprint Selector:

```mcfunction
/blockforge gui
```

You can also press the default `B` keybind.

Select a blueprint, choose a rotation, then press `Select`.

## Use The Builder Wand

Give yourself the Builder Wand:

```mcfunction
/blockforge wand
```

Hold the wand, look at a block, and right-click. Ghost Preview shows the target
placement area before you build.

## Survival Materials

In survival mode, BlockForge checks the required materials before building. If
materials are missing, the build is rejected. If materials are available, they
are consumed when the build starts.

Creative mode bypasses material checks and consumes nothing.

## Undo

Undo the most recent BlockForge placement:

```mcfunction
/blockforge undo
```

Undo restores the previous blocks and refunds survival materials recorded by
the build transaction. If the player inventory is full, refunded items are
dropped near the player.

## Common Config

The Connector writes a NeoForge common config file:

```text
.minecraft/config/blockforge_connector-common.toml
```

It contains safety and material options such as max blocks per build, Builder
Wand cooldown, undo history size, replacement rules, block entity protection,
and survival material requirements.

Defaults match the behavior validated before the v1.0 release candidate.
The v1.0 RC has also passed a Minecraft smoke test for client launch and the
core Connector flow after common config registration.

## Release Artifacts

A BlockForge release should include:

- Web source release from the GitHub tag.
- Mod jar from `mod/neoforge-connector/build/libs/*.jar`.
- `examples/blueprints/`.
- `docs/BLUEPRINT_PROTOCOL.md`.
- `docs/MOD_CONNECTOR_TESTING.md`.

GitHub Actions uploads the Connector jar as a CI artifact.
