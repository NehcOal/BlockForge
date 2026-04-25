# Changelog

All notable changes to BlockForge will be documented in this file.

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
