# Web Workbench

BlockForge v1.7.0-alpha.1 adds a local Web Import / Validation / Local
Generation Workbench. It keeps the app local-first: files are parsed in the
browser, validation runs locally, and no cloud storage or external AI API is
used.

## Import Workbench

The Web app can import:

- Blueprint JSON v1/v2
- Sponge `.schem` v3
- `.blockforgepack.zip`

Imported files are parsed into a shared local summary shape with source type,
blueprint count, warnings, validation status, dimensions, palette count, and
block count. Pack import currently performs local parsing and display only; it
does not persist a pack library.

## Export Formats

The current model can still be exported as:

- JSON
- Blueprint JSON v1
- Blueprint JSON v2
- Blueprint Pack
- Sponge `.schem` v3
- `.mcfunction`
- Minecraft Java 1.21.1 Data Pack ZIP

## Blueprint Validation Report

Validation reports use field-level issues:

```text
severity: error | warning
field: JSON field path such as blocks[2].state
message: user-facing validation detail
```

Current checks include:

- Supported `schemaVersion`
- Required ids and metadata
- Positive integer size
- Origin shape, with a warning when missing
- Palette entry shape
- Missing palette references
- Duplicate block coordinates
- Out-of-bounds block coordinates

## Local Rule Generator

The Local Prompt Rule Generator is deterministic and local-first. It recognizes
simple prompt keywords and produces valid `VoxelModel` structures without
calling an external AI API.

Supported building types:

- Tower
- Cottage / house
- Bridge
- Dungeon entrance
- Pixel statue

Supported size hints:

- Small
- Medium
- Large

Supported material hints include stone, wood, gold, red, and blue.

## Current Limits

- This is not an external AI API adapter.
- External AI API adapter is planned for v2.0.
- No cloud data is saved.
- Imported packs are parsed and summarized locally only.
- Minecraft manual regression is pending.
- Browser visual QA is pending.
