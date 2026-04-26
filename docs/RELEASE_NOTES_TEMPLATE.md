# BlockForge Release Notes Template

## Release Type

- Version: `1.1.3-alpha.1`
- Type: Multi-loader Alpha stabilization
- Stability: Alpha for Fabric and Forge, recommended full experience on NeoForge

## Supported Minecraft Version

- Minecraft Java Edition: `1.21.1`
- Java: `21`

## Supported Loaders

- NeoForge `21.1.227`: most complete Connector
- Fabric Loader `0.19.2` with Fabric API `0.116.11+1.21.1`: command-only Alpha
- Forge `52.1.14`: command-only Alpha

## Download Files

- `blockforge-connector-neoforge-1.1.3-alpha.1.jar`
- `blockforge-connector-fabric-1.1.3-alpha.1.jar`
- `blockforge-connector-forge-1.1.3-alpha.1.jar`

## Feature Matrix

| Feature | NeoForge | Fabric Alpha | Forge Alpha |
|---|---|---|---|
| Blueprint v1/v2 loading | yes | yes | yes |
| Examples install | yes | yes | yes |
| Reload/list/info/dryrun | yes | yes | yes |
| Build command | yes | yes | yes |
| Rotation | yes | yes | yes |
| Undo blocks | yes | yes | yes |
| GUI Selector | yes | no | no |
| Builder Wand | yes | no | no |
| Ghost Preview | yes | no | no |
| Survival material cost | yes | no | no |
| Material refund undo | yes | no | no |
| BlockEntity NBT undo | yes, best effort | no | no |

NeoForge is the most complete connector for now. Fabric and Forge are Alpha
command-loop connectors.

## Verified Tests

- `pnpm lint`
- `pnpm test`
- `pnpm build`
- `mod/neoforge-connector/gradlew.bat build`
- `mod/fabric-connector/gradlew.bat build`
- `mod/forge-connector/gradlew.bat build`

Manual Minecraft status:

- NeoForge: full Connector flows previously verified.
- Fabric: command-loop Alpha verified.
- Forge: command-loop Alpha verified.
- v1.1.3: packaging and documentation stabilization; no new gameplay feature
  claims beyond the tested Alpha scope.

## Known Limitations

- Fabric and Forge do not include GUI Selector, Ghost Preview, Builder Wand,
  survival material costs, material refunds, or BlockEntity NBT undo.
- Fabric and Forge undo snapshots are in-memory and latest-build only.
- Fabric and Forge do not persist undo history across disconnects or restarts.
- No automatic publishing to Modrinth or CurseForge is performed by this release
  template.

## Upgrade Notes

- Use the jar that matches your loader.
- Keep existing blueprint JSON files in `.minecraft/config/blockforge/blueprints/`.
- NeoForge users should prefer the NeoForge jar for the complete experience.
- Fabric and Forge users should treat this as an Alpha command workflow.
