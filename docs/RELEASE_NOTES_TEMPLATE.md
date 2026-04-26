# BlockForge Release Notes Template

## Release Type

- Version: `1.2.2-alpha.1`
- Type: Fabric / Forge GUI Selector + Builder Wand + Ghost Preview Alpha parity
- Stability: Alpha for Fabric and Forge, recommended full experience on NeoForge

## Supported Minecraft Version

- Minecraft Java Edition: `1.21.1`
- Java: `21`

## Supported Loaders

- NeoForge `21.1.227`: most complete Connector
- Fabric Loader `0.19.2` with Fabric API `0.116.11+1.21.1`: GUI Selector + Builder Wand + Ghost Preview Alpha
- Forge `52.1.14`: GUI Selector + Builder Wand + Ghost Preview Alpha

## Download Files

- `blockforge-connector-neoforge-1.2.2-alpha.1.jar`
- `blockforge-connector-fabric-1.2.2-alpha.1.jar`
- `blockforge-connector-forge-1.2.2-alpha.1.jar`

## Feature Matrix

| Feature | NeoForge | Fabric Alpha | Forge Alpha |
|---|---|---|---|
| Blueprint v1/v2 loading | yes | yes | yes |
| Examples install | yes | yes | yes |
| Reload/list/info/dryrun | yes | yes | yes |
| Build command | yes | yes | yes |
| Rotation | yes | yes | yes |
| Undo blocks | yes | yes | yes |
| GUI Selector | yes | yes, Alpha | yes, Alpha |
| Builder Wand | yes | yes, Alpha | yes, Alpha |
| Ghost Preview | yes | yes, Alpha | yes, Alpha |
| Survival material cost | yes | no | no |
| Material refund undo | yes | no | no |
| BlockEntity NBT undo | yes, best effort | no | no |

NeoForge is the most complete connector for now. Fabric and Forge include GUI
Selector, Builder Wand, and Ghost Preview Alpha support.

## Verified Tests

- `pnpm lint`
- `pnpm test`
- `pnpm build`
- `mod/neoforge-connector/gradlew.bat build`
- `mod/fabric-connector/gradlew.bat build`
- `mod/forge-connector/gradlew.bat build`

Manual Minecraft status:

- NeoForge: full Connector flows previously verified.
- Fabric: command-loop Alpha verified; GUI, Builder Wand, and Ghost Preview manual testing pending.
- Forge: command-loop Alpha verified; GUI, Builder Wand, and Ghost Preview manual testing pending.
- v1.2.2: Ghost Preview Alpha build validation passed.

## Known Limitations

- Fabric and Forge Ghost Preview only renders a bounding box and ground footprint.
- Fabric and Forge preview does not scan collisions, show material status,
  render individual blocks, or preview textures.
- Fabric and Forge do not include survival material costs, material refunds, or
  BlockEntity NBT undo.
- Fabric and Forge Builder Wand support is Alpha and has a 2 second per-player cooldown.
- Fabric and Forge undo snapshots are in-memory and latest-build only.
- Fabric and Forge do not persist undo history across disconnects or restarts.
- No automatic publishing to Modrinth or CurseForge is performed by this release
  template.

## Upgrade Notes

- Use the jar that matches your loader.
- Keep existing blueprint JSON files in `.minecraft/config/blockforge/blueprints/`.
- NeoForge users should prefer the NeoForge jar for the complete experience.
- Fabric and Forge users should treat this as an Alpha GUI + command workflow.
