# Changelog

All notable changes to BlockForge will be documented in this file.

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
