# BlockForge Roadmap

## Near Term

- Stabilize v1.6 Sponge `.schem` import/export across Web, NeoForge, Fabric,
  and Forge.
- Keep NeoForge 1.21.1 as the recommended complete Connector target.
- Keep Fabric and Forge 1.21.1 as Alpha connectors while refund undo and material UX mature.
- Defer small v1.3.x manual Minecraft testing until the v1.3.5 multiloader regression pass.
- Stabilize BlockForge Blueprint v1 as the Web and Mod shared protocol.
- Add schema validation tooling for exported blueprint files.
- Add Ghost Preview collision and replacement scans.
- Add search/paging when the Blueprint Selector list grows.
- Add GUI material summaries for Fabric and Forge.
- Stabilize nearby container material sourcing Alpha across NeoForge, Fabric,
  and Forge.
- Run the v1.4.0 Blueprint Pack regression in real Minecraft clients.
- Run the v1.6.0 Schematic Interop regression in real Minecraft clients.
- Stabilize the v1.7 Web Import / Validation / Local Generation Workbench.
- Keep external AI API adapter work planned for v2.0.
- Add special material cost rules for non-cube blocks.
- Improve release artifact publishing beyond CI artifact upload.
- Add Java-side parser tests when the Connector test setup is stable.
- Expand Blueprint v2 block state coverage beyond basic string properties.

## Export Formats

- Keep JSON, Blueprint v1/v2 JSON, `.mcfunction`, and Function Data Pack ZIP exports stable.
- Stabilize `.blockforgepack.zip` import/export and add imported pack library
  management after the Alpha protocol proves out.
- Add Minecraft Structure `.nbt` export.
- Add Structure Data Pack ZIP export for `/place template` workflows.
- Stabilize Sponge `.schem` v3 export/import and add mod-side export later.
- Explore Litematica `.litematic` after Sponge interop proves stable.

## Local Generation

- Improve the Local Prompt Rule Generator.
- Add more voxel presets.
- Keep the generator local-first before integrating any external AI API.

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
- v1.2.5: Multiloader Parity Alpha regression and release-candidate documentation.
- v1.3.0: Nearby Material Source common core.
- v1.3.1: NeoForge nearby container material sourcing reference implementation.
- v1.3.5: Fabric / Forge nearby container adapters plus batched multiloader
  in-game regression checklist for v1.3.
- v1.4.0: Blueprint Pack import/export on Web and pack loading on NeoForge,
  Fabric, and Forge.
- v1.5.0: Server permissions and protection layer.
- v1.6.0: Sponge `.schem` v3 export/import on Web and schematic loading on
  NeoForge, Fabric, and Forge.
- v1.7.0: Web Import / Validation / Local Generation Workbench.
- v2.0.0: External AI API adapter exploration after local workbench stabilizes.

See [Multi-loader Plan](./MULTILOADER_PLAN.md) for details.
## v1.5.0 Security Layer

- Server permission nodes, built-in protection regions, build preflight, and
  protected nearby-container material sourcing checks for NeoForge, Fabric, and
  Forge.
- Manual Minecraft regression testing remains pending.
