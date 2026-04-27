# Changelog

All notable changes to BlockForge will be documented in this file.

## [1.8.0-alpha.1] - Unreleased

### Added

- Added Web Blueprint JSON import with local parsing and validation summary.
- Added field-level Blueprint validation reports for schema, size, origin,
  palette references, duplicate coordinates, and out-of-bounds blocks.
- Added a Local Prompt Rule Generator for deterministic, local-first prompt
  driven voxel models.
- Added improved ExportPanel grouping for Blueprint files, Minecraft install
  exports, and interop imports.
- Added additional schematic, Blueprint import, validation, and local generator
  tests.
- Added `docs/WEB_WORKBENCH.md`.
- Added query-based in-game GUI search, pagination, source filtering, warning
  filtering, sorting, source tags, and warning badges for NeoForge, Fabric,
  and Forge.
- Added common `BlueprintGuiQuery`, filter/sort DTOs, `PagedBlueprintResult`,
  and `BlueprintGuiQueryService`.
- Added `docs/WEB_VISUAL_QA.md` and `docs/GUI_SEARCH_AND_FILTERS.md`.

### Changed

- Web app now behaves more like a local import, validation, and generation
  workbench while keeping the existing preview and multi-format export flow.
- Web import summaries and validation reports are clearer and collapsible, with
  user-readable import errors and expandable developer details.
- Aligned Web, NeoForge, Fabric, and Forge versions to `1.8.0-alpha.1`.

### Notes

- External AI API adapter is planned for v2.0.
- Manual Minecraft regression is pending.
- Browser visual QA is pending.

## [1.6.0] - Unreleased

### Added

- Added Web Sponge `.schem` v3 export using GZip NBT and Sponge
  `Blocks.Palette` / `Blocks.Data`.
- Added Web Sponge `.schem` import that converts schematic blocks into
  Blueprint JSON v2 data.
- Added Web NBT reader/writer, gzip helpers, and VarInt codec for schematic
  interoperability.
- Added common Java schematic DTOs, blockstate string codec, VarInt codec, and
  Sponge schematic reader.
- Added NeoForge, Fabric, and Forge schematic registries for
  `config/blockforge/schematics/*.schem`.
- Added `/blockforge schematics folder|reload|list|info|validate` on all three
  connectors.
- Added Sponge schematic Vitest coverage for blockstate strings, VarInts,
  export, and import.
- Added Web schematic regression coverage for common blockstate properties,
  ignored partial-content warnings, and exported-imported round trips.
- Added Java schematic reader unit coverage for common blockstate properties,
  ignored partial-content warnings, unsupported versions, and missing palette
  indexes.
- Added `docs/SCHEMATIC_INTEROP.md`.

### Changed

- Aligned Web, NeoForge, Fabric, and Forge versions to `1.6.0-alpha.1`.
- `/blockforge reload` now scans loose blueprints, Blueprint Packs, and
  schematics.
- Imported schematics use registry ids in the form `schem/<file_name>`.
- Web and Java schematic import now enforce a 10 MB decompressed GZip NBT
  limit before parsing.

### Fixed

- Fixed schematic import safety so small compressed files cannot expand into
  unbounded NBT data during Web import or connector reload/validate.
- Fixed Web and Java schematic import so `Blocks.Data` references to missing
  `Blocks.Palette` indexes are rejected instead of silently dropping blocks.

### Review Follow-up Fixes

- Addressed v1.6.0 schematic review findings for bounded GZip decompression
  and strict palette index validation.
- Added Vitest coverage for bounded gzip output and missing schematic palette
  indexes.

### Notes

- Sponge `.schem` support is Alpha and targets Sponge Schematic v3.
- Web export is implemented; mod-side schematic export remains planned.
- Entities, biomes, and full BlockEntity NBT fidelity are not implemented in
  this Alpha.
- Web lint/test/build and Java unit tests are the automated release gate;
  real Minecraft validation is tracked in `docs/MOD_CONNECTOR_TESTING.md`.
- v1.6.0 manual Minecraft regression testing is pending.

## [1.5.0] - Unreleased

### Added

- Added common permission nodes and permission check result DTOs.
- Added common protection region, build area, matcher, preflight, and
  `protection-regions.json` schema.
- Added NeoForge, Fabric, and Forge fallback permission services.
- Added NeoForge, Fabric, and Forge protection region loaders for
  `config/blockforge/protection-regions.json`.
- Added `/blockforge protection folder|reload|list|info|check` and
  `/blockforge permissions check <node>` on all three connectors.
- Added build preflight before material consumption and block placement.
- Added protected-container checks for nearby material sourcing and refunds.
- Added `docs/PERMISSIONS_AND_PROTECTION.md`.

### Changed

- Aligned Web, NeoForge, Fabric, and Forge versions to `1.5.0-alpha.1`.
- Builder Wand builds now use BlockForge permission/protection checks instead
  of loader-specific hard-coded permission checks.

### Notes

- External provider integrations such as LuckPerms, Fabric Permissions API,
  FTB Chunks, and Open Parties and Claims remain optional/planned.
- v1.5.0 NeoForge and Fabric manual Minecraft regression testing is pending.

### Manual Testing

- Forge 1.21.1 development client smoke test passed on 2026-04-27.
- Fixed Forge client join failure caused by an unsynchronized custom Brigadier
  blueprint id argument type; Forge now uses a vanilla string argument with
  registry suggestions.
- Confirmed Forge protection regions can deny `/blockforge protection check`
  when `allowedPermissions` is empty.
- Confirmed Forge protected-area command build and Builder Wand build are
  denied before material consumption and block placement.
- Confirmed Forge build and undo still work outside the protected area.
- Confirmed OP bypass is controlled by the region's `allowedPermissions`
  field: an empty list denies everyone, while adding
  `blockforge.build.bypass_protection` allows OP fallback bypass.

### Review Follow-up Fixes

- Confirmed the v1.3.5 nearby source review fixes remain present while adding
  v1.5.0 security: Fabric and Forge nearby container sourcing use runtime
  settings instead of compile-time-disabled constants, Fabric source refunds
  validate the stored dimension id, Fabric container insertion/extraction now
  respects inventory slot rules, and Fabric dryrun reports material source
  status.
- Confirmed the v1.4.0 Blueprint Pack review fixes remain present: pack export
  writes each manifest entry from the matching model index, Web import strictly
  rejects unsafe external pack and blueprint ids, and the published schema
  rejects traversal, absolute, backslash, and Windows-drive blueprint paths.

## [1.4.0] - Unreleased

### Added

- Added Blueprint Pack v1 protocol documentation and JSON schema.
- Added Web Blueprint Pack export using Blueprint JSON v2 inside
  `.blockforgepack.zip`.
- Added Web Blueprint Pack import with manifest validation, blueprint JSON
  validation, and path traversal rejection.
- Added common Blueprint Pack metadata DTOs and registry id helpers.
- Added NeoForge, Fabric, and Forge pack loaders for
  `config/blockforge/packs/`.
- Added `/blockforge packs folder|reload|list|info|blueprints|validate` on all
  three connectors.
- Added `examples/packs/starter_buildings/` as a source example pack.
- Added Blueprint Pack Vitest coverage for manifest creation, export, import,
  path traversal rejection, missing manifest, and invalid blueprint JSON.

### Changed

- Aligned Web, NeoForge, Fabric, and Forge versions to `1.4.0-alpha.1`.
- `/blockforge reload` now scans loose blueprints and Blueprint Packs.
- Pack blueprints use `packId/blueprintId` registry ids to avoid loose
  blueprint collisions.
- GUI Selector details now label blueprints as `source=loose` or `source=pack`.

### Fixed

- Fixed Blueprint Pack export for packs containing multiple models with the
  same display name by writing each manifest entry from the corresponding
  source model index.
- Fixed Web Blueprint Pack import so external manifests must already use safe
  `packId` and blueprint ids; invalid ids are rejected instead of silently
  normalized.
- Tightened the public Blueprint Pack JSON schema so blueprint paths reject
  path traversal, absolute paths, backslashes, and Windows drive prefixes.

### Notes

- Blueprint Pack support is Alpha.
- Pack zip files are read directly and are not extracted to disk.
- v1.4.0 manual Minecraft regression testing is pending.

## [1.3.5] - Unreleased

### Added

- Added nearby container material sourcing Alpha for NeoForge, Fabric, and
  Forge.
- Added NeoForge / Fabric / Forge source scanners for loaded nearby containers.
- Added Fabric and Forge source-aware material consumption and undo refund.
- Added `/blockforge sources scan` and `/blockforge sources selected` on all
  three loaders.
- Added common material source data models for player inventory, nearby
  containers, and mixed sources.
- Added `MaterialSourceConfig` with safe defaults for future nearby container
  sourcing.
- Added material source scan plan and scan result models.
- Added `MaterialSourcePlanner` for loader-neutral source report planning.
- Added `docs/MATERIAL_SOURCES.md` for the v1.3 nearby material source design.

### Changed

- Aligned Web, NeoForge, Fabric, and Forge versions to `1.3.5-alpha.1`.
- Extended `ConsumedMaterialEntry` and `MaterialTransaction` with optional
  source metadata while keeping existing player-inventory constructors.
- Updated NeoForge, Fabric, and Forge survival material flow to optionally
  combine player inventory and nearby containers by configured source priority.
- Updated undo material refund to prefer original nearby containers, then
  player inventory, then player-near drops where supported.
- Updated the Loader Feature Matrix to mark nearby chest material sourcing as
  Alpha on all three loaders.

### Fixed

- Fixed Fabric and Forge nearby container sourcing so Alpha testers can enable
  the feature at runtime instead of shipping it permanently compiled off.
- Fixed Fabric nearby source refunds so stored source dimension ids are checked
  before resolving a container at the saved coordinates.
- Fixed Fabric container material mutation to respect inventory slot validity
  and sided inventory insert/extract rules.
- Fixed Fabric dryrun output to include material source settings and source
  availability so source behavior can be inspected before building.

### Notes

- Nearby container sourcing is disabled by default on all loaders.
- Fabric and Forge use runtime `/blockforge sources` settings for this Alpha;
  config file support is planned.
- Forge nearby container source-aware consumption and undo refund passed a
  focused real-client smoke test on 2026-04-26: player-sourced materials
  returned to the player inventory, and chest-sourced materials returned to the
  original chest.
- NeoForge and Fabric nearby container sourcing manual testing remains pending
  for the v1.3.5 multiloader regression pass.

## [1.2.5] - Unreleased

### Changed

- Aligned Web, NeoForge, Fabric, and Forge versions to `1.2.5-alpha.1`.
- Refreshed the English and Chinese Loader Feature Matrix for multiloader
  parity release-candidate documentation.
- Added a v1.2.5 multiloader regression checklist covering NeoForge, Fabric,
  and Forge command, GUI, Builder Wand, Ghost Preview, survival material, refund
  undo, and repeated undo history flows.
- Updated publishing and release notes documentation for the
  `v1.2.5-alpha.1` multiloader parity Alpha release.

### Notes

- This release candidate does not add new gameplay features.
- Forge Ghost Preview skewed line-box rendering is documented as fixed.
- Fabric and Forge undo history is documented as a 20-entry per-player Alpha
  history stack.
- Fabric / Forge material refund undo remains pending for the planned batched
  in-game regression pass.

## [1.2.4] - Unreleased

### Added

- Added common `MaterialRefundResult` for loader-neutral material refund results.
- Added Fabric Material Refund Undo Alpha for command builds and Builder Wand builds.
- Added Forge Material Refund Undo Alpha for command builds and Builder Wand builds.
- Added Fabric and Forge material transactions on undo snapshots.
- Added inventory-full refund overflow drops near the player.

### Changed

- Aligned Web, NeoForge, Fabric, and Forge versions to `1.2.4-alpha.1`.
- Fabric and Forge successful survival builds now tell players that undo restores blocks and refunds materials.
- Fabric and Forge `/blockforge undo` now restores blocks and refunds consumed survival materials.
- Fabric and Forge build rollback now refunds materials if material consumption succeeds but no undo snapshot is produced.
- Updated the Loader Feature Matrix to mark Fabric and Forge Material Refund Undo support as Alpha.

### Notes

- Creative builds create a creative-bypass transaction with no consumed items and do not refund materials on undo.
- Fabric and Forge material refund undo is Alpha and does not include nearby chest sourcing, recipe substitutions, GUI material icons, or BlockEntity NBT undo.
- Fabric / Forge Material Refund Undo manual Minecraft testing is pending and intentionally deferred for a later batch regression pass.

## [1.2.3] - Unreleased

### Added

- Added common material cost mode and `MaterialCounterCore` helpers for loader-neutral material aggregation.
- Added Fabric Survival Material Cost Alpha for command builds and Builder Wand builds.
- Added Forge Survival Material Cost Alpha for command builds and Builder Wand builds.
- Added Fabric and Forge `/blockforge materials <id>` and `/blockforge materials selected`.
- Added Fabric and Forge inventory material checkers and material consumers.
- Added Fabric and Forge per-player undo history stacks for repeated undo.

### Changed

- Aligned Web, NeoForge, Fabric, and Forge versions to `1.2.3-alpha.1`.
- Fabric and Forge survival builds now reject missing materials before placement.
- Fabric and Forge creative builds bypass material consumption.
- Fixed Forge Ghost Preview line boxes rendering with a skewed/slanted transform.
- Fabric and Forge `/blockforge undo` now pops prior placements from a per-player
  history stack instead of keeping only one snapshot.
- Updated the Loader Feature Matrix to mark Fabric and Forge Survival Material Cost support as Alpha.

### Notes

- Fabric and Forge Adventure and Spectator builds are rejected by the Alpha material gate.
- Fabric and Forge consume survival materials before placement after a dry-run precheck.
- Fabric and Forge `/blockforge undo` restores blocks only and does not refund consumed materials yet.
- Fabric / Forge GUI material summary is planned for v1.2.4; v1.2.3 exposes material reports through commands.
- Fabric / Forge cumulative v1.2.0-v1.2.3 manual smoke testing was run on
  2026-04-26. The session exercised GUI selection, Builder Wand placement,
  Ghost Preview display, creative material bypass, and undo behavior.
- The manual smoke found two regressions that were fixed in this branch: Forge
  Ghost Preview skewed rendering and Fabric / Forge single-snapshot undo.
- Targeted survival missing-material rejection and survival item consumption
  retesting is still recommended before a public v1.2.3 release.

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
