# BlockForge v1.8.0-alpha.1 - Web Polish + GUI Search Alpha

## Release Type

- Version: `1.8.0-alpha.1`
- Type: Web Import / Validation / Local Generation Workbench Alpha candidate
- Stability: Alpha for Fabric and Forge, recommended full experience on NeoForge

## Supported Minecraft Version

- Minecraft Java Edition: `1.21.1`
- Java: `21`

## Supported Loaders

- NeoForge `21.1.227`: most complete Connector
- Fabric Loader `0.19.2` with Fabric API `0.116.11+1.21.1`: GUI Selector + Builder Wand + Ghost Preview + Survival Material Cost + Material Refund Undo + Nearby Material Source Alpha
- Forge `52.1.14`: GUI Selector + Builder Wand + Ghost Preview + Survival Material Cost + Material Refund Undo + Nearby Material Source Alpha

## Download Files

- `blockforge-connector-neoforge-1.8.0-alpha.1.jar`
- `blockforge-connector-fabric-1.8.0-alpha.1.jar`
- `blockforge-connector-forge-1.8.0-alpha.1.jar`

## Feature Matrix

| Feature | NeoForge | Fabric | Forge |
|---|---|---|---|
| Blueprint v1/v2 loading | ✅ | ✅ Alpha | ✅ Alpha |
| Examples install | ✅ | ✅ Alpha | ✅ Alpha |
| Reload/list/info/dryrun | ✅ | ✅ Alpha | ✅ Alpha |
| Build command | ✅ | ✅ Alpha | ✅ Alpha |
| Rotation | ✅ | ✅ Alpha | ✅ Alpha |
| Undo blocks | ✅ | ✅ Alpha | ✅ Alpha |
| Builder Wand | ✅ | ✅ Alpha | ✅ Alpha |
| GUI Selector | ✅ | ✅ Alpha | ✅ Alpha |
| Ghost Preview | ✅ | ✅ Alpha | ✅ Alpha |
| Survival material cost | ✅ | ✅ Alpha | ✅ Alpha |
| Material refund undo | ✅ | ✅ Alpha | ✅ Alpha |
| BlockEntity NBT undo | ✅ best effort | ❌ | ❌ |
| Nearby chest material sourcing | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Blueprint Pack import | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Permission nodes | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Protection regions | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Sponge `.schem` import | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Sponge `.schem` export | planned | planned | planned |

NeoForge is the most complete connector for now. Fabric and Forge include GUI
Selector, Builder Wand, Ghost Preview, Survival Material Cost, and Material
Refund Undo Alpha support. Nearby chest material sourcing is implemented as a
multiloader Alpha feature in v1.3.5 and disabled by default on every loader.
Blueprint Pack import is Alpha on all three connectors; Web export/import is
also Alpha.
Sponge `.schem` v3 import is Alpha on Web and all three connectors. Web
schematic export is Alpha. Mod-side schematic export is planned.

## Web Workbench Highlights

- Import Blueprint JSON v1/v2.
- Import Sponge `.schem` v3.
- Import `.blockforgepack.zip`.
- Show field-level Blueprint validation reports.
- Generate deterministic local prompt-rule models without external AI API use.
- Export JSON, Blueprint v1/v2, Blueprint Pack, `.schem`, `.mcfunction`, and
  Data Pack ZIP.

## Verified Tests

- `pnpm lint`
- `pnpm test`
- `pnpm build`
- `mod/neoforge-connector/gradlew.bat build`
- `mod/fabric-connector/gradlew.bat build`
- `mod/forge-connector/gradlew.bat build`

Manual Minecraft status:

- NeoForge: full Connector flows previously verified; v1.3.5 nearby container
  sourcing manual testing pending.
- Fabric: command-loop Alpha verified; GUI, Builder Wand, Ghost Preview,
  survival material cost, material refund undo, and nearby container sourcing
  manual testing pending.
- Forge: command-loop Alpha verified; GUI, Builder Wand, Ghost Preview,
  survival material cost, and material refund undo previously verified. A
  v1.3.5 focused nearby container smoke test passed: player-sourced materials
  refund to player inventory, and chest-sourced materials refund to the
  original chest.
- v1.8.0 build validation: pending before release.
- Full NeoForge / Fabric / Forge v1.8.0 Minecraft regression testing
  is still pending.
- Browser visual QA is pending.

## GUI Search Highlights

- NeoForge, Fabric, and Forge Blueprint Selector GUI now supports search,
  source filtering, warning filtering, sorting, and server-side pagination.
- Rows show source tags and best-effort warning badges.
- Selection, Builder Wand, and Ghost Preview remain linked through the
  server-validated selection request.

## Known Limitations

- Fabric and Forge Ghost Preview only renders a bounding box and ground footprint.
- Fabric and Forge preview does not scan collisions, show material status,
  render individual blocks, or preview textures.
- Fabric and Forge Material Refund Undo is Alpha; inventory overflow drops near
  the player.
- Nearby container sourcing is disabled by default. Forge has a focused
  source-aware refund smoke pass; NeoForge and Fabric nearby source testing
  remain pending.
- Fabric and Forge do not include GUI material source details, recipe
  substitutions, protected-container permission checks, or BlockEntity NBT undo.
- Fabric and Forge Builder Wand support is Alpha and has a 2 second per-player cooldown.
- Fabric and Forge undo snapshots are in-memory and capped at 20 snapshots per player.
- Fabric and Forge do not persist undo history across disconnects or restarts.
- Dedicated server smoke testing is pending for this release candidate.
- Web pack import currently displays an import summary and does not persist an
  imported pack library.
- Web Local Prompt Rule Generator is deterministic and local-only; external AI
  API adapter work is planned for v2.0.
- Blueprint Pack zips are loaded from `config/blockforge/packs/`; there is no
  remote download or server-client pack sync.
- No automatic publishing to Modrinth or CurseForge is performed by this release
  template.
- Sponge `.schem` support ignores entities, biomes, and full BlockEntity NBT
  fidelity in this Alpha.

## Upgrade Notes

- Use the jar that matches your loader.
- Keep existing blueprint JSON files in `.minecraft/config/blockforge/blueprints/`.
- NeoForge users should prefer the NeoForge jar for the complete experience.
- Fabric and Forge users should treat this as an Alpha GUI + command +
  material-cost + refund-undo workflow.
## v1.6.0 Schematic Notes

- Sponge `.schem` support targets v3 GZip NBT files.
- Schematics are loaded from `config/blockforge/schematics/`.
- Imported schematic ids use `schem/<file_name>`.
- Litematica `.litematic` is planned.

## v1.5.0 Security Notes

- Permissions/protection support is Alpha.
- Supported loaders: NeoForge, Fabric, Forge.
- Manual Minecraft regression testing pending unless explicitly verified.
- External permission and claim integrations are planned optional adapters.
