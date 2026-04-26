# BlockForge Roadmap

## Near Term

- Continue v1.1 multi-loader architecture work around `mod/common`.
- Keep NeoForge 1.21.1 as the current stable Connector target.
- Validate the Forge 1.21.1 command-only Connector Alpha in a real Minecraft instance.
- Start Fabric and Forge parity work only after command reload/list/dryrun/build/undo are proven in-game on both loaders.
- Stabilize BlockForge Blueprint v1 as the Web and Mod shared protocol.
- Add schema validation tooling for exported blueprint files.
- Add Ghost Preview collision and replacement scans.
- Add search/paging when the Blueprint Selector list grows.
- Add special material cost rules for non-cube blocks.
- Improve release artifact publishing beyond CI artifact upload.
- Add Java-side parser tests when the Connector test setup is stable.
- Expand Blueprint v2 block state coverage beyond basic string properties.

## Export Formats

- Keep JSON, Blueprint v1/v2 JSON, `.mcfunction`, and Function Data Pack ZIP exports stable.
- Add Minecraft Structure `.nbt` export.
- Add Structure Data Pack ZIP export for `/place template` workflows.
- Explore `.schem` export after the native Minecraft formats are reliable.

## Generation

- Improve prompt-to-structure rule generation.
- Add more voxel presets.
- Keep the generator local-first before integrating any real AI API.

## Rendering

- Improve block visual materials.
- Add optional Minecraft-like textures.
- Use `InstancedMesh` when model size grows beyond the current preset scale.

## Multi-loader Plan

- v1.1.0: common core for blueprint, rotation, material data, build planning, undo records, and platform adapter interfaces.
- v1.1.1: Fabric Connector command Alpha.
- v1.1.2: Forge Connector command Alpha.
- v1.1.3: parity pass across NeoForge, Fabric, and Forge.

See [Multi-loader Plan](./MULTILOADER_PLAN.md) for details.
