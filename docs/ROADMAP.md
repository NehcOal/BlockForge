# BlockForge Roadmap

## Near Term

- Stabilize BlockForge Blueprint v1 as the Web and Mod shared protocol.
- Add schema validation tooling for exported blueprint files.
- Run full Minecraft client/server manual validation for the Builder Wand MVP.
- Run full Minecraft client/server manual validation for undo and safety-limit behavior.
- Run full Minecraft client/server manual validation for the Ghost Preview MVP candidate.
- Add Ghost Preview collision and replacement scans.
- Replace code-level Connector safety constants with a NeoForge common config file.
- Validate Blueprint Selector GUI in Minecraft and add search/paging when the blueprint list grows.
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
