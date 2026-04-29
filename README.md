# BlockForge

BlockForge is a local-first Minecraft-style voxel blueprint generator.  
Choose a preset, preview it in 3D, then export the model as JSON or Minecraft `.mcfunction` commands.

[中文文档](./README.zh-CN.md) | [中文使用手册](./docs/USER_MANUAL.zh-CN.md)

## v4.1.0 Gameplay GUI + Runtime Beta

BlockForge v4.2.0-beta.1 focuses on making the v3 gameplay systems safer to
test on real clients and servers. It does not add new Web or AI product scope.

Beta highlights:

- Material Cache menu state and Builder Station status/action DTOs for
  loader-safe GUI implementation.
- Builder Station action validation for create/start/pause/resume/step/cancel
  buttons.
- Audit JSONL formatting, admin rollback decisions, and friendly cooldown
  policy helpers.
- Diagnostics fields for active stations, active jobs, audit entries, quota
  denials, and material network sources.
- Gameplay GUI, audit, rollback, and beta QA docs updated for v4.1.

Still partial: loader-specific Material Cache/Builder Station screens,
loader-integrated station world placement, audit file writing, and real
dedicated server QA.

See [Gameplay Beta Testing](./docs/GAMEPLAY_BETA_TESTING.md) and
[Gameplay Beta QA Report](./docs/GAMEPLAY_BETA_QA_REPORT.md).

## v3.5.0 Builder Station + Multiplayer Server Rules Alpha

BlockForge v3.5.0-alpha.1 starts the Builder Station and multiplayer server
rules train.

Current alpha scope:

- Builder Station, Material Link, and Construction Core are registered across
  NeoForge, Fabric, and Forge with original placeholder resources.
- Builder Station job status, queue model, and command-driven status/step
  scaffold.
- Server build rules for quota, cooldown, audit, project membership, and
  material-source policy.
- Pure Java rule evaluator, server gameplay DTOs, and resource coverage tests.

Still partial: tick-based station world placement, persistent station jobs,
inventory-backed Material Cache sourcing, and multiplayer conflict resolution.

See [Builder Station](./docs/BUILDER_STATION.md) and
[Material Network](./docs/MATERIAL_NETWORK.md).

## v3.2.0 Construction Workflow + Build Planner Alpha

BlockForge v3.2.0-alpha.1 adds the first Build Planner layer. It turns a
blueprint placement into a deterministic, previewable construction plan before
the world is touched.

Highlights:

- Common BuildPlan / BuildLayer / BuildStep model.
- Deterministic layer planning from low Y to high Y.
- Pure validation for duplicate coordinates, missing palette references, and
  out-of-world Y positions.
- Per-player BuildPlan manager scaffolding across NeoForge, Fabric, and Forge.
- NeoForge `/blockforge buildplan ...` command-driven Alpha.
- Repair plan pure logic for missing-coordinate-only repair planning.

Current limitation: v3.2 step execution is simulated and does not place blocks.
Existing direct build and Builder Wand BUILD remain the actual placement path.

See [Build Planner](./docs/BUILD_PLANNER.md).

## v3.1.0 Gameplay Utility Blocks + Advanced Builder Wand Alpha

BlockForge v3.1.0-alpha.1 starts the in-game utility pass for NeoForge,
Fabric, and Forge. The goal is to make BlockForge usable from inside Minecraft
without leaning on commands for every step.

Gameplay highlights:

- Blueprint Table: right-click in-world entry point for the Blueprint Selector
  GUI.
- Material Cache: registered Alpha utility block and common material source DTO
  for the next cache-backed material flow.
- Builder Anchor: right-click anchor binding for player Builder Wand state.
- Builder Wand modes: preview, build, dry-run, materials, undo, rotate, mirror,
  offset, anchor, and clear-preview state.
- Sneak + right-click cycles wand mode on all three loaders.
- NeoForge includes initial `/blockforge wand ...` command helpers for mode,
  options, offset, mirror, replace, and anchor clear.

See [Gameplay Blocks](./docs/GAMEPLAY_BLOCKS.md) and
[Builder Wand Advanced](./docs/BUILDER_WAND_ADVANCED.md).

Current validation status:

| Area | Status |
|---|---|
| Web lint/test/build | release gate |
| NeoForge/Fabric/Forge Gradle builds | release gate |
| Minecraft manual regression | pending |
| Dedicated server smoke test | pending |

## v2.0.0 AI Generation Alpha

BlockForge v4.2.0-beta.1 adds an optional AI generation pipeline while keeping
the Local Rule Generator as the default fallback.

AI Generation highlights:

- Local Rule Generator remains available without any API key.
- Optional OpenAI provider runs through server-side API routes only.
- Browser client code never reads or bundles `OPENAI_API_KEY`.
- External AI returns an AI Structure Plan v1, not trusted final blocks.
- Structure Plans are validated before conversion to VoxelModel and Blueprint
  v2.
- AI-generated blueprints can use the existing 3D preview and multi-format
  export flow.
- External AI requests may send prompts to the selected provider and may incur
  API cost.

See [AI Generation](./docs/AI_GENERATION.md) for setup, privacy notes, and
known Alpha limits.

Expected release jars:

- `blockforge-connector-neoforge-4.2.0-beta.1.jar`
- `blockforge-connector-fabric-4.2.0-beta.1.jar`
- `blockforge-connector-forge-4.2.0-beta.1.jar`

Current validation status:

| Area | Status |
|---|---|
| Web lint/test/build | passing |
| AI provider tests with mocks | passing |
| NeoForge/Fabric/Forge Gradle builds | passing |
| External AI manual API-key smoke test | not run |
| Manual Minecraft regression | pending |
| Browser visual QA | pending |

## v1.9.0 Web Polish + In-game GUI Alpha

BlockForge v1.9.0-alpha.1 polishes the Web import, validation, and Local
Prompt Rule Generator workbench, then adds query-based in-game GUI search,
pagination, source filtering, warning filtering, sorting, source tags, and
warning badges for NeoForge, Fabric, and Forge.

Web Workbench highlights:

- Import Blueprint JSON v1/v2.
- Import Sponge `.schem` v3.
- Import `.blockforgepack.zip`.
- Show field-level validation reports for palette references, duplicate
  coordinates, out-of-bounds blocks, and malformed fields.
- Generate local rule-based prompt models without using an external AI API.
- Export JSON, Blueprint v1/v2, Blueprint Pack, `.schem`, `.mcfunction`, and
  Data Pack ZIP.
- Collapse import summaries and validation reports, with user-readable import
  errors plus expandable developer details.

In-game GUI highlights:

- Search by blueprint id, name, source id, pack id, and tags.
- Filter by All, Loose, Pack, and Schematic sources.
- Filter by warning state and sort by name, block count, or source.
- Page server-side results with 8 blueprints per page.
- Show source tags, best-effort warning badges, material summary status, and
  rotation controls while keeping selection, Builder Wand, and Ghost Preview
  linked to the server-validated selection.

See [Web Workbench](./docs/WEB_WORKBENCH.md) for usage notes and current
limits. See [GUI Search And Filters](./docs/GUI_SEARCH_AND_FILTERS.md) for the
Connector GUI query model. External AI Generation Alpha is documented in
[AI Generation](./docs/AI_GENERATION.md).

Expected release jars:

- `blockforge-connector-neoforge-1.9.0-alpha.1.jar`
- `blockforge-connector-fabric-1.9.0-alpha.1.jar`
- `blockforge-connector-forge-1.9.0-alpha.1.jar`

Current validation status:

| Area | Status |
|---|---|
| Web lint/test/build | passing |
| NeoForge/Fabric/Forge Gradle builds | passing |
| Web Workbench unit coverage | passing |
| Manual Minecraft regression | pending |
| Browser visual QA | pending |

## v1.9.0 Rendering Performance + Screenshot Export Alpha

BlockForge v1.9.0-alpha.1 adds a Web rendering performance pass for larger
voxel previews. The 3D preview now supports `Auto`, `Mesh`, and `Instanced`
render modes. Auto keeps the existing mesh path for small models and switches
to InstancedMesh rendering at 300 blocks or more, grouped by block type.

Web rendering highlights:

- Instanced rendering for larger voxel models.
- Rendering stats for block count, unique block types, render mode, and draw
  groups.
- Minecraft-inspired procedural material styles without bundling Minecraft
  vanilla texture files.
- Better camera fit helpers for large and small models.
- Export Preview PNG for README, release notes, and gallery screenshots.

### Generate a README screenshot

1. Open the Web app and choose or generate a model.
2. Adjust the 3D preview camera to the desired angle.
3. Click `Export Preview PNG`.
4. Place the image at `public/screenshots/blockforge-hero.png` when you want to
   use it in the README, release notes, or a Modrinth gallery.

The screenshot export uses the current preview canvas and does not upload data.
The material style is procedural; Minecraft vanilla texture files are not
included.

## v1.6.0 Schematic Interop Alpha

BlockForge v1.6.0-alpha.1 adds Sponge `.schem` v3 interoperability. The Web
app can export and import GZip NBT Sponge schematics, while NeoForge, Fabric,
and Forge can scan `config/blockforge/schematics/` and load `.schem` files into
the normal blueprint registry as `schem/<file>`.

Expected release jars:

- `blockforge-connector-neoforge-1.6.0-alpha.1.jar`
- `blockforge-connector-fabric-1.6.0-alpha.1.jar`
- `blockforge-connector-forge-1.6.0-alpha.1.jar`

Current validation status:

| Area | Status |
|---|---|
| Web lint/test/build | passing |
| Web `.schem` export/import unit coverage | passing |
| Java schematic reader unit coverage | passing |
| NeoForge/Fabric/Forge Gradle builds | release gate |
| Manual Minecraft schematic regression | pending |

Manual Minecraft regression must still be run before marking v1.6.0 stable.

## v1.5.0 Server Permissions & Protection Layer Alpha

BlockForge v1.5.0-alpha.1 adds a server-side permissions and protection layer
for NeoForge, Fabric, and Forge. Builds now run a security preflight before
material checks or block placement, built-in protection regions can deny
BlockForge builds, and nearby container material sourcing respects protected
container positions.

Expected release jars:

- `blockforge-connector-neoforge-1.5.0-alpha.1.jar`
- `blockforge-connector-fabric-1.5.0-alpha.1.jar`
- `blockforge-connector-forge-1.5.0-alpha.1.jar`

## Loader Feature Matrix

| Feature | NeoForge | Fabric | Forge |
|---|---|---|---|
| Blueprint v1/v2 loading | ✅ | ✅ Alpha | ✅ Alpha |
| Examples install | ✅ | ✅ Alpha | ✅ Alpha |
| Reload/list/info/dryrun | ✅ | ✅ Alpha | ✅ Alpha |
| Build command | ✅ | ✅ Alpha | ✅ Alpha |
| Rotation | ✅ | ✅ Alpha | ✅ Alpha |
| Undo blocks | ✅ | ✅ Alpha | ✅ Alpha |
| Builder Wand | ✅ | ✅ Alpha | ✅ Alpha |
| GUI Selector | ✅ | ✅ Alpha | ✅ Alpha |
| Ghost Preview | ✅ | ✅ Alpha | ✅ Alpha |
| Survival material cost | ✅ | ✅ Alpha | ✅ Alpha |
| Material refund undo | ✅ | ✅ Alpha | ✅ Alpha |
| BlockEntity NBT undo | ✅ best effort | ❌ | ❌ |
| Nearby chest material sourcing | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Blueprint Pack import | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Permission nodes | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Protection regions | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Build preflight | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Container protection checks | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Sponge `.schem` import | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| In-game GUI search | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| In-game GUI pagination | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| In-game GUI source filtering | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Sponge `.schem` export | planned | planned | planned |
| Litematica `.litematic` | planned | planned | planned |

## Web Feature Matrix

| Feature | Web |
|---|---|
| Web Workbench | ✅ Alpha |
| Blueprint validation report | ✅ Alpha |
| Local Rule Generator | ✅ Alpha |
| External AI Generation | ✅ Alpha |
| AI Structure Plan validation | ✅ Alpha |
| AI Prompt Presets | ✅ Alpha |
| Multi-candidate Generation | ✅ Alpha |
| AI Quality Score | ✅ Alpha |
| Structure Plan Viewer | ✅ Alpha |
| Candidate Compare | ✅ Alpha |
| Refine Workflow | ✅ Alpha |
| Generation History | ✅ Alpha |
| Local Blueprint Library | ✅ Alpha |
| Workspace Export/Import | ✅ Alpha |
| Import Job Queue | ✅ Alpha |
| Import Worker | ✅ Alpha |
| Web 3D instanced rendering | ✅ Alpha |
| Preview PNG export | ✅ Alpha |
| Minecraft-inspired procedural materials | ✅ Alpha |
| Vanilla Minecraft texture pack | ❌ Not bundled |

## Schematic Interop Matrix

| Feature | Web | NeoForge | Fabric | Forge |
|---|---|---|---|---|
| Export Sponge `.schem` v3 | ✅ Alpha | planned | planned | planned |
| Import Sponge `.schem` v3 | ✅ Alpha | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Schematic registry id | N/A | `schem/<file>` | `schem/<file>` | `schem/<file>` |
| Litematica `.litematic` | planned | planned | planned | planned |

## Blueprint Pack Matrix

| Feature | Web | NeoForge | Fabric | Forge |
|---|---|---|---|---|
| Export `.blockforgepack.zip` | ✅ Alpha | N/A | N/A | N/A |
| Import `.blockforgepack.zip` | ✅ Alpha | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Pack manifest validation | ✅ Alpha | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Pack blueprint registry ids | N/A | ✅ Alpha | ✅ Alpha | ✅ Alpha |

Fabric and Forge GUI Selector, Builder Wand, Ghost Preview, Survival Material
Cost, and Material Refund Undo support are Alpha. Ghost Preview only renders a
rotation-aware bounding box and ground footprint. Fabric and Forge undo now
restores blocks and refunds consumed survival materials, but still does not
restore BlockEntity NBT. Nearby chest material sourcing is Alpha on all three
loaders, disabled by default, scans only loaded chunks, and is pending manual
Minecraft regression for NeoForge and Fabric. Forge has passed a focused
v1.3.5 smoke test where player-sourced materials returned to the player and
chest-sourced materials returned to the original chest.

See [Material Sources](./docs/MATERIAL_SOURCES.md) for the v1.3 design notes.
See [Blueprint Packs](./docs/BLUEPRINT_PACKS.md) for the v1.4 pack format.
See [Permissions and Protection](./docs/PERMISSIONS_AND_PROTECTION.md) for the
v1.5 server safety model.
See [Schematic Interop](./docs/SCHEMATIC_INTEROP.md) for the v1.6 Sponge
schematic import/export notes.
See [GUI Search And Filters](./docs/GUI_SEARCH_AND_FILTERS.md) for the v1.8
server-side GUI search, pagination, filtering, sorting, source tags, and
warning badge notes.

Fabric / Forge GUI + Builder Wand + Ghost Preview + Survival Materials Alpha flow:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge gui
/blockforge materials selected
/blockforge wand
```

You can also press the default `B` key to open the selector. Choose a blueprint
and rotation, click Select, hold the Builder Wand, look at a block to see the
Ghost Preview outline, right-click to build, then run `/blockforge undo` to
restore the placed blocks. In survival mode, materials must be present and are
consumed before the build; creative mode consumes nothing. Undo refunds survival
materials and drops overflow near the player if the inventory is full.

## v1.1.1 Fabric Connector Alpha

BlockForge v1.1.1 adds `mod/fabric-connector`, a Fabric 1.21.1 command-only
Connector Alpha. It can install example blueprints, reload and list Blueprint
JSON files, dry-run a build plan, place blueprints in the world, and undo
recent Fabric builds per player.

Fabric Alpha reuses `mod/common` for blueprint parsing, rotation, and build
planning data. As of v1.2.5, Fabric includes GUI Selector, Builder Wand, Ghost
Preview, Survival Material Cost, and Material Refund Undo Alpha. It still does
not include BlockEntity NBT undo yet. NeoForge remains the most complete and
stable Connector target.

See [Fabric Connector README](./mod/fabric-connector/README.md) for commands,
installation notes, and current limitations.

## v1.1.2 Forge Connector Alpha

BlockForge v1.1.2 adds `mod/forge-connector`, a Forge 1.21.1 command-only
Connector Alpha. It matches the Fabric Alpha command loop: install examples,
reload and list Blueprint JSON files, dry-run a build plan, place blueprints in
the world, and undo recent Forge builds per player.

Forge Alpha reuses `mod/common` for blueprint parsing, rotation, and build
planning data. As of v1.2.5, Forge includes GUI Selector, Builder Wand, Ghost
Preview, Survival Material Cost, and Material Refund Undo Alpha. It still does
not include BlockEntity NBT undo yet. NeoForge remains the most complete and
stable Connector target.

See [Forge Connector README](./mod/forge-connector/README.md) for commands,
installation notes, and current limitations.

## v1.1.0 Multi-loader Architecture

BlockForge v1.1.0 starts the multi-loader architecture work. NeoForge 1.21.1
remains the current stable Connector target, while Fabric 1.21.1 and Forge
1.21.1 support are planned for later alpha releases.

This release adds `mod/common`, a loader-neutral Java core for blueprint
parsing, rotation, build planning, material data, undo records, platform
adapter interfaces, and utility types. The NeoForge Connector now reuses this
common core where it is safe to do so, while command registration, item
registration, GUI, networking, Ghost Preview rendering, config registration,
world placement, and inventory access remain NeoForge-specific.

See [Multi-loader Plan](./docs/MULTILOADER_PLAN.md) for the architecture plan
and version roadmap.

## v1.0.1 Bug Fixes

- Fixed survival material over-consumption when a build only places part of a blueprint because some target blocks are protected, non-replaceable, out of world, or otherwise skipped.
- Hardened Connector blueprint parsing so malformed but syntactically valid JSON is reported as a load warning instead of escaping as an unchecked parser error.
- Added Connector-side blueprint structure validation for positive size, in-bounds block coordinates, duplicate coordinates, and missing palette references.
- Added NeoForge Connector unit tests covering malformed blueprint parsing and partial-build material counting.

## v1.0.0-rc.1 Features

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
- Material transactions with undo refunds for survival builds
- Creative-mode material bypass
- NeoForge common config for Connector safety and material settings
- CI jobs for Web and NeoForge Connector builds
- CI job for Fabric Connector Alpha builds
- CI job for Forge Connector Alpha builds
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

## Fastest In-Game Start

```text
1. Install the BlockForge Connector mod.
2. Run /blockforge examples install.
3. Run /blockforge reload.
4. Open /blockforge gui.
5. Select a blueprint.
6. Get /blockforge wand.
7. Right-click to preview and build.
8. Use /blockforge undo to revert.
```

For full setup details, see [Install Guide](./docs/INSTALL.md).

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

## Fabric Connector Alpha

The repository also includes a Fabric 1.21.1 alpha at
`mod/fabric-connector`.

Build it with:

```powershell
cd mod/fabric-connector
gradlew.bat build
```

The Fabric jar is generated in:

```text
mod/fabric-connector/build/libs/
```

Fast command loop:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge dryrun tiny_platform
/blockforge build tiny_platform
/blockforge undo
```

Fabric manual command-loop testing passed for example installation, reload,
list, dryrun, build, undo, rotated `state_test_house`, and invalid blueprint id
handling.

## Forge Connector Alpha

The repository also includes a Forge 1.21.1 alpha at
`mod/forge-connector`.

Build it with:

```powershell
cd mod/forge-connector
gradlew.bat build
```

The Forge jar is generated in:

```text
mod/forge-connector/build/libs/
```

Fast command loop:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge dryrun tiny_platform
/blockforge build tiny_platform
/blockforge undo
```

Forge manual command-loop testing passed for example installation, reload, list,
dryrun, build, undo, rotated `state_test_house`, and invalid blueprint id
handling. A Forge undo edge case that dropped doors/torches during rollback was
fixed by suppressing drops during snapshot restoration.

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

v0.6.1 Gradle build and Minecraft manual testing passed.

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
passed; Minecraft manual testing passed.

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
nothing. Undo now restores world blocks and refunds recorded survival materials.
If the player inventory is full, refunded items are dropped near the player.
v1.0.0-rc.1 keeps the v0.9.1-tested behavior and adds release packaging,
metadata, CI, and common config polish. Minecraft smoke testing passed for
client launch and the core Connector flow.

Connector common config:

```text
.minecraft/config/blockforge_connector-common.toml
```

The config controls safety limits, replacement rules, Builder Wand cooldown,
undo history size, and survival material requirements.

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
├─ common/              Loader-neutral Java core for future multi-loader support
├─ fabric-connector/    Fabric 1.21.1 Connector Alpha
├─ forge-connector/     Forge 1.21.1 Connector Alpha
└─ neoforge-connector/  NeoForge 1.21.1 Mod Connector
examples/
└─ blueprints/          Blueprint v1 files for Connector testing
```

## Roadmap

- Current train: `v2.5 AI Productization Alpha`. This is not stable yet.
- v2.6: Multiplayer / Server QA.
- v2.7: Litematica interop.
- v2.8: Blueprint marketplace / sharing exploration.
- v3.0: Major Product Redesign.

BlockForge now groups small polish, tests, and documentation updates into the
active major-version branch instead of creating separate small feature branches.
See [Roadmap](./docs/ROADMAP.md) and [Release Process](./docs/RELEASE_PROCESS.md).

## Contributing

Issues and pull requests are welcome. Please keep changes focused, typed, and covered by
tests when they affect voxel generation or export behavior.

## License

MIT
# BlockForge v4.2.0-beta.1

BlockForge v4.2.0-beta.1 is a Product Workbench Alpha: release readiness,
experimental Litematica import, local Blueprint Gallery, server/admin polish,
and a unified Web workbench shell.

## v3.0 Feature Matrix

| Area | Feature | Status |
|---|---|---|
| Web | Litematica `.litematic` import | ✅ Alpha |
| Web | Blueprint Gallery | ✅ Alpha |
| Web | Gallery bundle export/import | ✅ Alpha |
| Web | Unified Workbench UI | ✅ Alpha |
| Web | Command Palette action registry | ✅ Alpha |
| Web | Instanced rendering | ✅ Alpha |
| Web | Preview PNG export | ✅ Alpha |
| Web | External AI Generation | ✅ Alpha |
| Mod | Litematica loading | Planned / pending real connector regression |
| Mod | Diagnostics export | Planned / pending real connector regression |
| Docs | Server admin docs | ✅ Alpha |

Pending: Browser visual QA, Minecraft manual regression, External AI live test,
Dedicated server smoke test, Modrinth / CurseForge publishing.

Litematica support is experimental and does not claim full fidelity. Every
imported file must produce a validation report before preview, export, or build.
