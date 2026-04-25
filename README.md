# BlockForge

BlockForge is a local-first Minecraft-style voxel blueprint generator.  
Choose a preset, preview it in 3D, then export the model as JSON or Minecraft `.mcfunction` commands.

[中文文档](./README.zh-CN.md) | [中文使用手册](./docs/USER_MANUAL.zh-CN.md)

## v0.2.0 Features

- 5 built-in voxel presets
- Interactive 3D browser preview
- JSON export
- Minecraft `.mcfunction` export
- Minecraft Java 1.21.1 Data Pack ZIP export
- TypeScript-first voxel data model
- Vitest coverage for core export and preset logic

## Features

- Five built-in voxel presets: medieval tower, small cottage, dungeon entrance, stone bridge, and pixel statue.
- 3D browser preview powered by React Three Fiber.
- Orbit, pan, and zoom controls for inspecting voxel models.
- Export voxel models as JSON.
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
4. Export Data Pack ZIP.
5. Copy the zip into `.minecraft/saves/<world>/datapacks`.
6. Run `/reload`.
7. Run `/function blockforge:build/<blueprint_id>`.
8. Export JSON for data use.
9. Export `.mcfunction` for Minecraft command workflows.
10. Optionally enter a prompt to update the local prompt state.

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
```

## Roadmap

- Full datapack ZIP export.
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
