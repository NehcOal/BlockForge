# Schematic Interop

BlockForge v1.6.0-alpha.1 adds Sponge Schematic v3 `.schem` import/export.
This is an Alpha bridge for WorldEdit / FAWE / Sponge schematic workflows.

## Supported Format

Supported:

- GZip-compressed NBT `.schem`
- Root compound named `Schematic`
- `Version = 3`
- `DataVersion`
- `Width`, `Height`, `Length`
- `Offset`
- `Metadata`
- `Blocks.Palette`
- `Blocks.Data`

Block index mapping:

```text
index = x + z * Width + y * Width * Length
```

`Blocks.Palette` maps Sponge blockstate strings to palette indexes:

```text
minecraft:stone_bricks
minecraft:oak_door[facing=north,half=lower,hinge=left,open=false]
```

`Blocks.Data` is decoded and encoded as VarInt palette indexes.

## Web Usage

The Web app can export the current Blueprint v2 model as Sponge `.schem` v3.

Export:

1. Select a model.
2. Click `Export .schem`.
3. The file name uses `blockforge-<id>.schem`.

Import:

1. Click `Import .schem`.
2. Select a GZip NBT Sponge v3 file.
3. BlockForge parses it into Blueprint v2 and displays a summary with size,
   palette count, block count, and warnings.

The Web importer validates the schematic version, dimensions, palette, and
VarInt block data before returning a blueprint.

## Mod Usage

NeoForge, Fabric, and Forge scan this directory during reload:

```text
config/blockforge/schematics/
```

Supported files:

```text
*.schem
```

Loaded schematics enter the normal blueprint registry with ids:

```text
schem/<file_name_without_ext>
```

Example:

```text
config/blockforge/schematics/castle.schem
id: schem/castle
```

These ids work with the normal flow:

```mcfunction
/blockforge info schem/castle
/blockforge dryrun schem/castle
/blockforge select schem/castle
/blockforge gui
/blockforge wand
/blockforge undo
```

## Commands

All three connectors add:

```mcfunction
/blockforge schematics folder
/blockforge schematics reload
/blockforge schematics list
/blockforge schematics info <id>
/blockforge schematics validate
```

`reload` requires the same permission level as the existing reload command.
The other commands are available to normal players unless server permission
settings say otherwise.

## Registry Priority

When ids collide, registry priority is:

1. Loose Blueprint JSON
2. Blueprint Pack entries
3. Schematic imports

Conflicting schematic ids are skipped and reported as warnings. A bad `.schem`
file does not stop loose blueprints or packs from loading.

## Safety Limits

Current limits:

- Max `.schem` file size: 10 MB
- Max decompressed GZip NBT size: 10 MB
- Max schematic volume: 1,000,000 blocks
- Only files ending in `.schem` are scanned
- Invalid GZip / invalid NBT is reported as a warning
- Unsupported schematic versions are skipped
- `Blocks.Data` must reference only indexes declared in `Blocks.Palette`

The loaders do not extract schematics to disk.

## Conversion Notes

Sponge blockstate strings are converted to Blueprint v2 palette entries.
Properties are parsed into key/value maps when possible.

Examples:

```text
minecraft:stone_bricks
minecraft:oak_door[facing=north,half=lower,hinge=left,open=false]
```

Unknown block ids can still be registered as blueprint palette strings. Build
commands will skip them through the existing invalid-block handling.

If `Blocks.Data` references a palette index missing from `Blocks.Palette`, the
schematic is rejected. This avoids silently importing incomplete buildings.

## Current Limits

- Entities are ignored.
- Biomes are ignored.
- BlockEntities are reported as partial/ignored warnings.
- Cross-version DataFixer conversion is not implemented.
- Mod-side `.schem` export is planned.
- Litematica `.litematic` support is planned.

## v1.6.0 Manual Test Checklist

Web:

1. Select `tiny_platform`.
2. Export `.schem`.
3. Import the exported `.schem`.
4. Confirm size, palette count, and non-air block count match.

NeoForge / Fabric / Forge:

1. Put a Web-exported `.schem` in `config/blockforge/schematics/`.
2. Run `/blockforge schematics validate`.
3. Run `/blockforge schematics reload`.
4. Run `/blockforge schematics list`.
5. Run `/blockforge info schem/tiny_platform`.
6. Run `/blockforge select schem/tiny_platform`.
7. Open `/blockforge gui`.
8. Build with Builder Wand.
9. Run `/blockforge undo`.

Error cases:

- Unsupported `Version`
- Invalid GZip
- Invalid NBT
- Volume above 1,000,000 blocks
- Unknown block id
- Invalid blockstate property string

Automated coverage:

- Web export/import round trip from a built-in preset.
- Web import of common blockstate properties such as doors, stairs, and wall
  torches.
- Web warnings for ignored BlockEntities, Entities, and Biomes.
- Web and Java rejection of missing `Blocks.Palette` indexes referenced by
  `Blocks.Data`.
- Java schematic reader import of common blockstate properties and ignored
  partial-content warnings.
- Java rejection of unsupported schematic versions.

Status: v1.6.0 manual Minecraft regression testing is pending.
