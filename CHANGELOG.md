# Changelog

All notable changes to BlockForge will be documented in this file.

## [1.2.2] - Unreleased

### Added

- Added common preview DTOs for loader-neutral preview state, bounds, and target data.
- Added Fabric Ghost Preview Alpha for Builder Wand selections.
- Added Forge Ghost Preview Alpha for Builder Wand selections.
- Added Fabric and Forge preview selection sync payloads so clients receive server-confirmed blueprint size and rotation.
- Added Fabric and Forge preview clearing payloads for invalid or missing selections.

### Changed

- Aligned Web, NeoForge, Fabric, and Forge versions to `1.2.2-alpha.1`.
- Fabric and Forge `/blockforge selected` now includes a Ghost Preview hint and refreshes client preview state.
- Updated the Loader Feature Matrix to mark Fabric and Forge Ghost Preview support as Alpha.

### Notes

- Fabric and Forge Ghost Preview only renders a rotation-aware bounding box and ground footprint.
- Fabric and Forge Ghost Preview does not modify the world, scan collisions, show material status, render individual blocks, or preview textures.
- Fabric / Forge Ghost Preview manual Minecraft testing is pending.

## [1.2.1] - Unreleased

### Added

- Added common GUI DTOs for blueprint list summaries and server-validated selection requests.
- Added Fabric Blueprint Selector GUI Alpha, opened with `/blockforge gui` or the default `B` key.
- Added Forge Blueprint Selector GUI Alpha, opened with `/blockforge gui` or the default `B` key.
- Added Fabric and Forge GUI networking payloads for blueprint list sync, selection requests, and selection results.
- Added English and Chinese GUI/keybinding translations for Fabric and Forge.

### Changed

- Aligned Web, NeoForge, Fabric, and Forge versions to `1.2.1-alpha.1`.
- Updated Fabric and Forge `/blockforge selected` to clear stale selections after blueprint reload removes the selected id.
- Updated the Loader Feature Matrix to mark Fabric and Forge GUI Selector support as Alpha.
- Updated Fabric and Forge docs with the GUI + Builder Wand Alpha flow.

### Notes

- Fabric and Forge GUI Selector only chooses blueprint id and rotation; it does not add Ghost Preview, survival material cost, material refund undo, thumbnails, editor workflows, or Web sync.
- Fabric / Forge GUI manual Minecraft testing is pending.
- Fabric / Forge Builder Wand manual Minecraft testing from v1.2.0 is still pending unless tested in game.

## [1.2.0] - Unreleased

### Added

- Added common player selection data models for loader-neutral blueprint id, rotation, and wand cooldown state.
- Added Fabric Builder Wand Alpha item registration, item model, and English/Chinese translations.
- Added Forge Builder Wand Alpha item registration, item model, and English/Chinese translations.
- Added Fabric commands for `/blockforge select`, `/blockforge selected`, `/blockforge rotate`, and `/blockforge wand`.
- Added Forge commands for `/blockforge select`, `/blockforge selected`, `/blockforge rotate`, and `/blockforge wand`.
- Added in-memory Fabric and Forge player selection managers with a 2 second Builder Wand cooldown.

### Changed

- Aligned Web, NeoForge, Fabric, and Forge versions to `1.2.0-alpha.1`.
- Fabric and Forge Builder Wand placement reuses the existing loader-specific placer and undo snapshot flow.
- Updated the Loader Feature Matrix to mark Fabric and Forge Builder Wand support as Alpha.
- Updated Fabric and Forge docs with the Builder Wand Alpha flow.

### Notes

- Fabric and Forge still do not include GUI, Ghost Preview, survival material cost, material refund undo, or BlockEntity NBT undo.
- Command builds are not throttled by the Builder Wand cooldown.
- Fabric / Forge Builder Wand manual Minecraft testing is pending.

## [1.1.3] - Unreleased

### Added

- Added a loader feature matrix to the English and Chinese READMEs.
- Added `docs/RELEASE_NOTES_TEMPLATE.md` for GitHub alpha releases.
- Added `docs/PUBLISHING.md` with multi-loader GitHub, Modrinth, and CurseForge publishing guidance.

### Changed

- Aligned Web, NeoForge, Fabric, and Forge versions to `1.1.3-alpha.1`.
- Standardized release jar names:
  - `blockforge-connector-neoforge-1.1.3-alpha.1.jar`
  - `blockforge-connector-fabric-1.1.3-alpha.1.jar`
  - `blockforge-connector-forge-1.1.3-alpha.1.jar`
- Updated CI to build Web, NeoForge, Fabric, and Forge without silently skipping existing loader modules.
- Updated CI artifact names to `blockforge-neoforge-jar`, `blockforge-fabric-jar`, and `blockforge-forge-jar`.
- Updated install, testing, roadmap, and multi-loader docs to clarify that NeoForge is the most complete Connector while Fabric and Forge remain command-only Alpha connectors.

### Notes

- v1.1.3 does not add GUI, Ghost Preview, Builder Wand, material costs, or material refunds to Fabric or Forge.
- Fabric and Forge command-loop manual Minecraft testing passed before this stabilization pass.
- NeoForge remains the recommended full-experience Connector for the Alpha release.

## [1.1.2] - Unreleased

### Added

- Added `mod/forge-connector`, a Forge 1.21.1 command-only Connector Alpha.
- Added Forge commands for `folder`, `examples list`, `examples install`, `reload`, `list`, `info`, `dryrun`, `build`, rotated build, coordinate build, and `undo`.
- Added Forge example blueprint resources for `tiny_platform`, `small_test_house`, `state_test_house`, and `medieval_tower`.
- Added Forge placement and per-player latest-build undo snapshots.
- Added a Forge CI job that runs only when `mod/forge-connector` exists and uploads Forge jars.
- Added `mod/forge-connector/README.md`.

### Changed

- Forge Connector reuses `mod/common` through an additional Java source root for blueprint parsing, rotation, and `BuildPlan` data.
- Forge and Fabric Alpha undo restoration now suppresses drops during block-state rollback so attached blocks such as doors and torches do not drop as items during undo.
- Updated Web/package metadata toward `1.1.2-alpha.1`.
- Updated multi-loader docs to mark Forge as command-only Alpha while NeoForge remains the complete Connector.

### Notes

- Forge Alpha does not include GUI, Ghost Preview, Builder Wand, survival materials, inventory consumption, material refunds, or BlockEntity NBT undo.
- Fabric Alpha command-loop manual testing passed before this Forge branch.
- Manual Minecraft Forge command-loop testing passed for example install, reload, list, dryrun, build, rotated `state_test_house`, undo, and invalid blueprint id handling.
- Initial Forge undo testing exposed attached-block item drops for doors/torches; the Alpha undo path now suppresses drops during restoration.

## [1.1.1] - Unreleased

### Added

- Added `mod/fabric-connector`, a Fabric 1.21.1 command-only Connector Alpha.
- Added Fabric commands for `folder`, `examples list`, `examples install`, `reload`, `list`, `info`, `dryrun`, `build`, rotated build, coordinate build, and `undo`.
- Added Fabric example blueprint resources for `tiny_platform`, `small_test_house`, `state_test_house`, and `medieval_tower`.
- Added Fabric placement and per-player latest-build undo snapshots.
- Added a Fabric CI job that runs only when `mod/fabric-connector` exists and uploads Fabric jars.
- Added `mod/fabric-connector/README.md`.

### Changed

- Fabric Connector reuses `mod/common` through an additional Java source root for blueprint parsing, rotation, and `BuildPlan` data.
- Updated Web/package metadata toward `1.1.1-alpha.1`.
- Updated multi-loader docs to mark Fabric as command-only Alpha while NeoForge remains the complete Connector.

### Notes

- Fabric Alpha does not include GUI, Ghost Preview, Builder Wand, survival materials, inventory consumption, material refunds, or BlockEntity NBT undo.
- NeoForge remains the stable and most complete in-game target.
- Manual Minecraft Fabric command-loop testing passed for example install, reload, list, dryrun, build, rotated `state_test_house`, undo, and invalid blueprint id handling.

## [1.1.0] - Unreleased

### Added

- Added `mod/common` as the first multi-loader common core for future Fabric and Forge connectors.
- Added loader-neutral blueprint, rotation, build planning, material, undo, platform, and utility packages under `com.blockforge.common`.
- Added `BuildPlan` and `PlannedBlock` data structures for describing planned placements without touching a Minecraft world.
- Added `docs/MULTILOADER_PLAN.md` with the v1.1 multi-loader architecture plan.

### Changed

- NeoForge Connector now compiles `mod/common/src/main/java` as a shared Java source root.
- NeoForge blueprint parsing delegates to the common parser while preserving the existing NeoForge-facing model classes.
- NeoForge rotation logic delegates to the common rotation enum.
- NeoForge material counting delegates to the common material counter while registry and inventory access remain NeoForge-specific.
- NeoForge placement preflight now exposes a common `BuildPlan` alongside the existing placement result.
- Updated Web and Connector versions to `1.1.0`.

### Notes

- NeoForge 1.21.1 remains the only stable in-game target in v1.1.0.
- Fabric and Forge connectors are planned but not implemented in this release.
- Fabric / Forge CI jobs are intentionally not enabled until their modules exist.
- Manual Minecraft regression testing is pending.

## [1.0.1] - Unreleased

### Fixed

- Fixed survival-mode material over-consumption when a partial build skips protected, non-replaceable, out-of-world, or invalid targets.
- Hardened NeoForge Connector blueprint parsing so malformed JSON shapes are reported as invalid blueprints instead of escaping as unchecked parser errors.
- Added Connector-side validation for positive blueprint dimensions, in-bounds block coordinates, duplicate positions, and missing palette references.

### Added

- Added NeoForge Connector JUnit tests for malformed blueprint parsing and partial-build material counting.

### 中文摘要

- 修复生存模式部分放置时可能多扣材料的问题。
- 加固蓝图 JSON 解析，坏蓝图会变成加载 warning，不再中断 reload。
- 增加蓝图尺寸、坐标边界、重复坐标和 palette 引用校验。
- 新增 Connector 单元测试覆盖本次 bugfix。

## [1.0.0-rc.1] - Unreleased

### Added

- Release candidate packaging documentation.
- English and Chinese install guides.
- GitHub Actions Web and NeoForge Connector CI jobs.
- CI artifact upload for Connector jars.
- NeoForge common config for Connector safety and material settings.

### Changed

- Updated Web and Connector versions to `1.0.0-rc.1`.
- Cleaned up Connector mod metadata for the GitHub release candidate.
- Documented release artifacts and the fastest in-game setup path.

### Notes

- Target versions remain Minecraft Java Edition `1.21.1`, NeoForge `21.1.227`, and Java `21`.
- Defaults in the common config preserve the v0.9.1 validated behavior.
- Minecraft manual smoke testing verified the v1.0 RC client launch and core Connector flow after common config registration.

## [0.9.1] - Unreleased

### Added

- Material transactions for survival builds.
- Undo material refunds for BlockForge placements.
- Best-effort material rollback when a build consumes items but fails to place blocks.
- Material refund drop handling when the player's inventory is full.
- Shared `BuildService` path for command builds and Builder Wand builds.

### Notes

- Creative mode still bypasses material consumption and has no materials to refund.
- Undo restores world blocks first, then refunds recorded survival materials.
- If inventory space is unavailable during undo, refunded items are dropped near the player.
- Undo does not restore XP, currency, or external economy state because BlockForge does not include an economy system.
- v0.9.1 build validation passed.
- Minecraft manual testing verified survival material refund on undo and full-inventory refund drops.

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
- Undo restores world blocks only in v0.9.0; v0.9.1 adds material refunds.
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

- Configuration values are code-level constants in v0.6.1; v1.0.0-rc.1 moves them to a NeoForge common config.
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
