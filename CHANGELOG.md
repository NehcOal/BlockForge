# Changelog

All notable changes to BlockForge will be documented in this file.

## [0.9.0] - Unreleased

### Added

- Material requirement counting for loaded blueprints.
- `/blockforge materials <id>` and `/blockforge materials selected` commands.
- Material summaries in `/blockforge dryrun <id>`.
- Survival-mode material checks before command builds and Builder Wand builds.
- Survival-mode inventory consumption after material checks pass.
- Creative-mode material bypass.
- GUI material report request and material summary display.
- Server-side material report payloads for Blueprint Selector GUI.

### Notes

- Material cost mode is currently `simple`: one placed block costs one item from `block.asItem()`.
- Creative mode does not consume materials.
- Adventure and spectator builds are blocked by default.
- Undo restores world blocks only; it does not refund consumed materials yet.
- v0.9.0 build validation passed.
- Minecraft manual testing verified creative material bypass, survival missing-material rejection, survival material consumption, `/blockforge materials selected`, and block-only undo behavior.
- Manual material results for `tiny_platform`: `requiredItems=9`, missing case `availableItems=0`, enough case `availableItems=9`, and survival build consumed 9 stone bricks from a stack of 16.

## [0.8.0] - Unreleased

### Added

- Blueprint Selector GUI MVP for choosing loaded blueprints and rotation.
- `/blockforge gui` command for opening the selector.
- Client keybind for opening the selector, defaulting to `B`.
- Server-to-client blueprint list sync with blueprint summaries.
- Client-to-server selection request payload with server-side blueprint and rotation validation.
- English and Chinese GUI/keybind translations.

### Notes

- The GUI does not edit blueprints, render thumbnails, or perform advanced filtering.
- The client only requests a selection; the server remains authoritative and sends back the selected blueprint state.
- Ghost Preview and Builder Wand use the same server-validated selection state after GUI selection.
- v0.8.0 build validation passed.
- Minecraft manual testing verified `/blockforge gui`, the default `B` keybind, blueprint list sync, blueprint selection, rotation selection, Ghost Preview updates, Builder Wand placement, and `/blockforge selected` consistency.
- The GUI background was adjusted from the default blurred screen background to a single translucent overlay and clearer panel styling.

## [0.7.0] - Unreleased

### Added

- Ghost Preview MVP candidate for the BlockForge Builder Wand.
- Client-side preview state for selected blueprint size, rotation, target base position, and validity.
- Client render hook that draws a translucent bounding box and ground footprint.
- Network payloads for syncing selected blueprint metadata and clearing preview state.
- Preview synchronization from `/blockforge select`, `/blockforge selected`, and `/blockforge rotate`.

### Notes

- Ghost Preview does not modify the world and does not affect server-side placement.
- Preview validity currently checks Builder Wand held, selected blueprint, block hit target, and world height range only.
- Full collision and replacement scanning are planned for a later pass.
- v0.7.0 build validation passed.
- Minecraft manual testing verified Ghost Preview display and update behavior with Builder Wand, `tiny_platform`, `state_test_house`, `rotate 180`, and `rotate 90`.
- Manual placement results: `tiny_platform` placed `9` blocks; `state_test_house` placed `116` blocks with `appliedProperties=10` and zero missing palette, invalid block id, invalid properties, out-of-world, protected, or non-replaceable skips.

## [0.6.1] - Unreleased

### Added

- `/blockforge undo`, `/blockforge undo list`, and `/blockforge undo clear` commands.
- Per-player in-memory placement snapshots for command builds and Builder Wand builds.
- Undo restoration for previous block states and best-effort BlockEntity NBT.
- Connector safety configuration constants for max blocks, wand cooldown, undo history, non-air replacement, and BlockEntity protection.
- Placement statistics for protected BlockEntity targets and non-replaceable targets.

### Changed

- Builder Wand and command builds now save undo snapshots when blocks are placed.
- Placement safety limits now read from a centralized `BlockForgeConfig` class.

### Notes

- Configuration values are currently code-level constants; a NeoForge common config file is planned for a later pass.
- Undo history is stored in memory and is not persisted across restarts or player reconnect workflows.
- v0.6.1 build validation passed.
- Minecraft manual testing verified Builder Wand placement with `state_test_house`: `placed=116`, `appliedProperties=10`, and zero missing palette, invalid block id, invalid properties, out-of-world, protected, or non-replaceable skips.
- `/blockforge undo list`, `/blockforge undo`, and `/blockforge undo clear` still need dedicated in-game rollback validation.

## [0.6.0] - Unreleased

### Added

- BlockForge Builder Wand item for command-selected blueprint placement.
- `/blockforge select <id>`, `/blockforge selected`, `/blockforge rotate <0|90|180|270>`, and `/blockforge wand` commands.
- Per-player in-memory blueprint selection and rotation state.
- Two-second Builder Wand placement cooldown.
- English and Chinese Builder Wand item translations.
- Basic Builder Wand item model using the vanilla stick texture.

### Notes

- Builder Wand placement requires permission level 2.
- Selection state is in-memory only and is not persisted.
- Ghost Preview, GUI selection, undo, and material costs are not part of v0.6.
- Gradle build passed; Minecraft manual testing is pending for Builder Wand.

## [0.5.0] - Unreleased

### Added

- BlockForge Blueprint Protocol v2 with block state palette entries.
- Web export for Blueprint JSON v2.
- JSON Schema for `blockforge-blueprint-v2`.
- `state_test_house` Blueprint v2 example with oak door and wall torch properties.
- NeoForge Connector support for both Blueprint v1 and v2.
- Connector BlockState property application for valid Minecraft properties.
- Basic build rotation support for `0`, `90`, `180`, and `270` degrees.

### Changed

- Connector now uses an internal blueprint model converted from v1 or v2 input.
- Dry run output now includes schema version, property counts, blocks with properties, and invalid property counts.

### Notes

- Rotation currently affects x/z coordinates and horizontal `facing` values only.
- Block state support applies simple string properties and skips invalid property values without crashing.

### Verified

- Manually tested on Minecraft Java Edition 1.21.1 with NeoForge 21.1.227.
- Verified `state_test_house` Blueprint v2 loading and placement.
- Confirmed oak door and wall torch BlockState properties applied correctly.
- Confirmed `rotate 90` and coordinate `rotate 180` builds placed successfully.
- Confirmed `placed=116`, `appliedProperties=10`, and zero skipped missing palette, invalid block id, invalid properties, or out-of-world entries.

## [0.4.1] - Unreleased

### Added

- Example Blueprint v1 files for Connector testing.
- Built-in `/blockforge examples list` and `/blockforge examples install` commands.
- Manual testing checklist for Minecraft Java 1.21.1 and NeoForge 21.1.227.
- Vitest coverage for example Blueprint v1 files.

### Changed

- Hardened Connector placement checks for empty blueprints and out-of-world Y coordinates.
- Expanded dryrun output with blueprint metadata, palette count, estimated placed blocks, and limit status.

### Verified

- Manually tested on Minecraft Java Edition 1.21.1 with NeoForge 21.1.227.
- Verified `/blockforge examples install`, `/blockforge reload`, `/blockforge list`, and build commands for `tiny_platform`, `small_test_house`, and `medieval_tower`.
- Confirmed the three bundled examples placed `9`, `162`, and `229` blocks with zero skipped palette, invalid block id, or out-of-world entries.

## [0.4.0] - Unreleased

### Added

- NeoForge 1.21.1 BlockForge Connector MVP under `mod/neoforge-connector`.
- `/blockforge` command tree for folder, reload, list, info, dryrun, and build workflows.
- Blueprint v1 loading from `.minecraft/config/blockforge/blueprints/`.
- Server-side blueprint placement with a 10000 block safety limit.
- Connector README with installation, commands, usage, limits, and roadmap.

## [0.3.1] - Unreleased

### Added

- BlockForge Blueprint v1 protocol for future Web and Mod integration.
- Blueprint JSON export button.
- JSON Schema for `blockforge-blueprint-v1`.
- Blueprint protocol documentation.
- Vitest coverage for Blueprint v1 conversion and JSON export.

## [0.2.0] - 2026-04-25

### Added

- Minecraft Java Edition 1.21.1 Data Pack ZIP export.
- `pack.mcmeta` generation with `pack_format` 48.
- Data pack function output at `data/blockforge/function/build/<blueprint_id>.mcfunction`.
- Optional `README.txt` inside generated data pack zip files.
- `createSafeResourcePath()` for Minecraft-safe resource paths.
- Export UI button for Data Pack ZIP downloads.
- Export documentation covering JSON, `.mcfunction`, and Data Pack ZIP formats.
- Vitest coverage for data pack metadata, zip contents, function paths, and setblock command output.

### Notes

- Data Pack ZIP export targets Minecraft Java Edition only.
- Bedrock Edition, `.nbt`, `.schem`, and Mod integration are planned separately.

## [0.1.0] - 2026-04-25

### Added

- Initial Next.js, TypeScript, Tailwind CSS, and pnpm project setup.
- Five built-in voxel building presets:
  - Medieval Tower
  - Small Cottage
  - Dungeon Entrance
  - Stone Bridge
  - Pixel Statue
- Typed `VoxelModel` data structure with preset validation utilities.
- Interactive 3D browser preview using Three.js, React Three Fiber, and Drei.
- Orbit, pan, and zoom controls for inspecting voxel buildings.
- JSON export for the current voxel model.
- Minecraft `.mcfunction` export using `setblock` commands.
- English and Simplified Chinese UI language switch.
- English README, Chinese README, and Chinese user manual.
- GitHub Actions CI for lint, test, and build.
- Vitest coverage for preset generation, bounds validation, export utilities, Minecraft block mapping, rendering helpers, and i18n copy.

### Notes

- Prompt input is local UI state in v0.1.0. Real prompt-to-structure generation is planned for a future release.
- Full datapack ZIP export, `.schem` export, block texture rendering, and InstancedMesh performance optimization are on the roadmap.
