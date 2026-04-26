# BlockForge v1.2.5-alpha.1 - Multiloader Parity Alpha

## Release Type

- Version: `1.2.5-alpha.1`
- Type: Multiloader Parity Alpha release candidate
- Stability: Alpha for Fabric and Forge, recommended full experience on NeoForge

## Supported Minecraft Version

- Minecraft Java Edition: `1.21.1`
- Java: `21`

## Supported Loaders

- NeoForge `21.1.227`: most complete Connector
- Fabric Loader `0.19.2` with Fabric API `0.116.11+1.21.1`: GUI Selector + Builder Wand + Ghost Preview + Survival Material Cost + Material Refund Undo Alpha
- Forge `52.1.14`: GUI Selector + Builder Wand + Ghost Preview + Survival Material Cost + Material Refund Undo Alpha

## Download Files

- `blockforge-connector-neoforge-1.2.5-alpha.1.jar`
- `blockforge-connector-fabric-1.2.5-alpha.1.jar`
- `blockforge-connector-forge-1.2.5-alpha.1.jar`

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
| Nearby chest sourcing | ❌ | ❌ | ❌ |

NeoForge is the most complete connector for now. Fabric and Forge include GUI
Selector, Builder Wand, Ghost Preview, Survival Material Cost, and Material
Refund Undo Alpha support.

## Verified Tests

- `pnpm lint`
- `pnpm test`
- `pnpm build`
- `mod/neoforge-connector/gradlew.bat build`
- `mod/fabric-connector/gradlew.bat build`
- `mod/forge-connector/gradlew.bat build`

Manual Minecraft status:

- NeoForge: full Connector flows previously verified.
- Fabric: command-loop Alpha verified; GUI, Builder Wand, Ghost Preview, survival material cost, and material refund undo manual testing pending.
- Forge: command-loop Alpha verified; GUI, Builder Wand, Ghost Preview, survival material cost, and material refund undo manual testing pending.
- v1.2.5 batched multiloader regression: pending.

## Known Limitations

- Fabric and Forge Ghost Preview only renders a bounding box and ground footprint.
- Fabric and Forge preview does not scan collisions, show material status,
  render individual blocks, or preview textures.
- Fabric and Forge Material Refund Undo is Alpha; inventory overflow drops near
  the player.
- Fabric and Forge do not include GUI material summaries, nearby chest sourcing,
  recipe substitutions, or BlockEntity NBT undo.
- Fabric and Forge Builder Wand support is Alpha and has a 2 second per-player cooldown.
- Fabric and Forge undo snapshots are in-memory and capped at 20 snapshots per player.
- Fabric and Forge do not persist undo history across disconnects or restarts.
- Dedicated server smoke testing is pending for this release candidate.
- No automatic publishing to Modrinth or CurseForge is performed by this release
  template.

## Upgrade Notes

- Use the jar that matches your loader.
- Keep existing blueprint JSON files in `.minecraft/config/blockforge/blueprints/`.
- NeoForge users should prefer the NeoForge jar for the complete experience.
- Fabric and Forge users should treat this as an Alpha GUI + command +
  material-cost + refund-undo workflow.
