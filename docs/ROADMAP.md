# BlockForge Roadmap

## Near Term

- Stabilize the v1.2.4 Fabric / Forge GUI Selector, Builder Wand, Ghost Preview, Survival Material Cost, and Material Refund Undo Alpha.
- Keep NeoForge 1.21.1 as the recommended complete Connector target.
- Keep Fabric and Forge 1.21.1 as Alpha connectors while refund undo and material UX mature.
- Validate Fabric and Forge material refund undo in real Minecraft instances.
- Stabilize BlockForge Blueprint v1 as the Web and Mod shared protocol.
- Add schema validation tooling for exported blueprint files.
- Add Ghost Preview collision and replacement scans.
- Add search/paging when the Blueprint Selector list grows.
- Add GUI material summaries for Fabric and Forge.
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
- v1.1.3: multi-loader Alpha stabilization, feature matrix, CI artifact naming, and release packaging.
- v1.2.0: Fabric and Forge Builder Wand Alpha parity.
- v1.2.1: Fabric and Forge GUI Selector Alpha parity.
- v1.2.2: Fabric and Forge Ghost Preview Alpha parity.
- v1.2.3: Fabric and Forge Survival Material Cost Alpha parity.
- v1.2.4: Fabric and Forge Material Refund Undo Alpha parity.
- v1.2.5+: Fabric and Forge material UX refinement.

See [Multi-loader Plan](./MULTILOADER_PLAN.md) for details.
