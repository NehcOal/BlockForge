# BlockForge

BlockForge is a local-first Minecraft-style voxel blueprint generator.  
Choose a preset, preview it in 3D, then export the model as JSON or Minecraft `.mcfunction` commands.

[中文文档](./README.zh-CN.md) | [中文使用手册](./docs/USER_MANUAL.zh-CN.md)

## v0.9.0 Features

- 5 built-in voxel presets
- Interactive 3D browser preview
- JSON export
- BlockForge Blueprint v1 export for future Mod Connector workflows
- BlockForge Blueprint v2 export with Minecraft BlockState properties
- NeoForge 1.21.1 BlockForge Connector MVP
- Builder Wand MVP for selected blueprint placement
- Undo commands for recent BlockForge placements
- Placement snapshots and safety limit configuration constants
- Ghost Preview MVP candidate for Builder Wand placement
- Blueprint Selector GUI MVP for choosing blueprints and rotation in-game
- `/blockforge gui` and a default `B` keybind for opening the selector
- Material requirement reports for loaded blueprints
- Survival-mode material checks and item consumption
- Creative-mode material bypass
- Connector example blueprints and manual testing guide
- Minecraft `.mcfunction` export
- Minecraft Java 1.21.1 Data Pack ZIP export
- TypeScript-first voxel data model
- Vitest coverage for core export and preset logic

## Features

- Five built-in voxel presets: medieval tower, small cottage, dungeon entrance, stone bridge, and pixel statue.
- 3D browser preview powered by React Three Fiber.
- Orbit, pan, and zoom controls for inspecting voxel models.
- Export voxel models as JSON.
- Export BlockForge Blueprint v1 JSON for future mod integration.
- Export BlockForge Blueprint v2 JSON with block state properties.
- Build generated blueprints in-game with the NeoForge Connector command MVP.
- Place selected blueprints with the Builder Wand.
- Revert recent command or Builder Wand placements with `/blockforge undo`.
- Preview selected Builder Wand placement with a client-side Ghost Preview.
- Choose a loaded blueprint and rotation from the in-game Blueprint Selector GUI.
- Check required materials and consume survival inventory items before building.
- Export Minecraft `.mcfunction` commands.
- Export Minecraft Java 1.21.1 Data Pack ZIP.
- Typed voxel data model with validation helpers.
- Vitest coverage for preset integrity, bounds, duplicate coordinates, block styles, and render-position helpers.

## Demo Screenshots

![BlockForge hero screenshot](./public/screenshots/blockforge-hero.png)

> Place the latest project hero screenshot at `public/screenshots/blockforge-hero.png`.

## Tech Stack

- Next.js
- TypeScript
- Tailwind CSS
- Three.js
- React Three Fiber
- Drei
- Vitest
- pnpm

## Getting Started

```bash
pnpm install
pnpm dev
```

Open [http://localhost:3000](http://localhost:3000) in your browser.

## Usage

1. Choose one of the preset blueprints.
2. Preview it in 3D.
3. Rotate, zoom, and pan the model with the preview controls.
4. Export Blueprint JSON for future BlockForge Mod Connector workflows.
5. Export Data Pack ZIP.
6. Copy the zip into `.minecraft/saves/<world>/datapacks`.
7. Run `/reload`.
8. Run `/function blockforge:build/<blueprint_id>`.
9. Export JSON for data use.
10. Export `.mcfunction` for Minecraft command workflows.
11. Optionally enter a prompt to update the local prompt state.

## Blueprint v1 Export

BlockForge Blueprint v1 is the simple block id protocol. It preserves raw voxel
coordinates and maps BlockForge block types to Minecraft Java block ids through
a palette.

Blueprint v2 adds Minecraft BlockState support. Palette entries use `{ name,
properties }`, and blocks refer to a `state` key instead of a `block` key.

See [Blueprint Protocol](./docs/BLUEPRINT_PROTOCOL.md) for the field contract.

## NeoForge Connector MVP

The repository includes a minimal NeoForge 1.21.1 mod connector at
`mod/neoforge-connector`.

It reads Blueprint v1 JSON files from:

```text
.minecraft/config/blockforge/blueprints/
```

Then it places blueprints in-game with:

```mcfunction
/blockforge build <id>
/blockforge build <id> <x> <y> <z>
/blockforge build <id> rotate <0|90|180|270>
/blockforge build <id> at <x> <y> <z> rotate <0|90|180|270>
```

See [BlockForge Connector README](./mod/neoforge-connector/README.md) for setup
and command details.

For real Minecraft testing, start with:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
```

See [Mod Connector Manual Testing](./docs/MOD_CONNECTOR_TESTING.md) for the full
checklist.

Manual test status: passed on Minecraft Java Edition 1.21.1 with NeoForge
21.1.227 for the bundled `tiny_platform`, `small_test_house`, and
`medieval_tower` examples.

Blueprint v2 manual test status: passed for `state_test_house`, including oak
door properties, wall torch facing, and `rotate 90` / `rotate 180` builds.

Builder Wand MVP:

```mcfunction
/blockforge select state_test_house
/blockforge rotate 90
/blockforge wand
```

Then hold the Builder Wand and right-click a block. The blueprint is placed on
the clicked face. The wand requires permission level 2 and has a 2 second
cooldown.

Undo the latest BlockForge placement:

```mcfunction
/blockforge undo list
/blockforge undo
/blockforge undo clear
```

v0.6.1 Gradle build passed; Minecraft manual testing is pending.

Ghost Preview MVP candidate:

```mcfunction
/blockforge select tiny_platform
/blockforge rotate 90
/blockforge wand
```

Hold the Builder Wand and look at a block. The client draws a translucent
bounding box and ground footprint at `clickedPos.relative(clickedFace)`. The
preview uses the selected blueprint size and rotation, does not modify the
world, and does not replace server-side placement checks. v0.7 Gradle build
passed; Minecraft manual testing is pending.

Blueprint Selector GUI MVP:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge gui
```

The selector can also be opened with the default `B` keybind. Pick a blueprint,
choose `0°`, `90°`, `180°`, or `270°`, then press Select. The client sends a
selection request to the server, the server validates it, and Ghost Preview /
Builder Wand use the updated state. v0.8 Gradle build passed; Minecraft manual
testing is pending.

Material Requirements MVP:

```mcfunction
/blockforge materials selected
/blockforge materials tiny_platform
```

Survival players must have enough matching items before command builds or
Builder Wand builds. Creative players bypass material checks and consume
nothing. Undo currently restores world blocks only and does not refund consumed
materials. v0.9 Gradle build passed; Minecraft manual testing is pending.

## Minecraft Function Export

BlockForge can export each voxel block as a `setblock` command. The generated file is
intended as a starting point for Minecraft Java Edition command/data-pack workflows.

Advanced datapack workflows and additional structure formats are planned for future releases.

## Data Pack ZIP Export

BlockForge can export a ready-to-install Minecraft Java 1.21.1 data pack.
The generated data pack contains a BlockForge function that places the selected voxel
model with `setblock` commands.

The generated zip contains:

```text
pack.mcmeta
data/blockforge/function/build/<blueprint_id>.mcfunction
README.txt
```

Install it by copying the zip into `.minecraft/saves/<world>/datapacks`, running
`/reload`, then running `/function blockforge:build/<blueprint_id>`.

## Project Structure

```text
src/
├─ app/                 Next.js app routes and global styles
├─ components/          UI and 3D preview components
├─ lib/voxel/           Voxel types, presets, validation, rendering helpers
├─ test/                Vitest test files
└─ types/               Shared TypeScript types
mod/
└─ neoforge-connector/  NeoForge 1.21.1 Mod Connector MVP
examples/
└─ blueprints/          Blueprint v1 files for Connector testing
```

## Roadmap

- Full datapack ZIP export.
- Ghost Preview for the Builder Wand.
- Full Ghost Preview collision and replacement checks.
- Improve Blueprint Selector with search, paging, and thumbnails.
- Add material refund support for undo.
- Add special material cost rules for doors, fluids, torches, and multi-block placements.
- NeoForge common config file for Connector safety limits.
- Blueprint v1/v2 schema validation tooling.
- `.schem` export.
- Block texture rendering.
- InstancedMesh performance optimization for larger voxel models.
- Prompt-to-structure rule engine.
- Real AI adapter for natural-language blueprint generation.
- Screenshot gallery for GitHub project presentation.

## Contributing

Issues and pull requests are welcome. Please keep changes focused, typed, and covered by
tests when they affect voxel generation or export behavior.

## License

MIT
