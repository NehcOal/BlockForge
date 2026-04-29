# Web Workbench

BlockForge v3.1.0-alpha.1 polishes the local Web Import / Validation / Local
Generation Workbench. It keeps the app local-first: files are parsed in the
browser, validation runs locally, and no cloud storage is used. External AI
Generation Alpha is optional and requires a server-side provider.

## Import Workbench

The Web app can import:

- Blueprint JSON v1/v2
- Sponge `.schem` v3
- `.blockforgepack.zip`

Imported files are parsed into a shared local summary shape with source type,
source filename, blueprint count, warnings, errors, validation status,
dimensions, palette count, and block count. Import summaries are collapsible and
show success, warning, or error status. Pack import currently performs local
parsing and display only; it does not persist a pack library.

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
severity: error | warning | info
section: Model | Size | Origin | Palette | Blocks | Coordinates | Duplicate blocks | Missing palette references
path: JSON field path such as blocks[2].state
message: user-facing validation detail
suggestion: optional fix hint
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
- Informational model block counts

Examples:

- `error: blocks[3] references missing palette key "stonee"`
- `error: block at x=20 is outside width=11`
- `warning: duplicate block coordinate 1,2,3; later block wins`
- `info: model contains 656 blocks`

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

- External AI Generation Alpha requires server runtime and server-side
  `OPENAI_API_KEY`.
- Local Rule Generator remains available without any API key.
- Rendering uses procedural material styles; Minecraft vanilla texture files are
  not bundled.
- Preview PNG export reads the current local canvas only.
- No cloud data is saved.
- Imported packs are parsed and summarized locally only.
- Minecraft manual regression is pending.
- Browser visual QA is pending.

## AI Generation Alpha

The Web Workbench can receive generated blueprints from:

- Local Rule Generator: deterministic, local, no server request.
- External AI Provider: optional OpenAI provider through server-side API routes.

External AI results are not trusted as final blocks. They must pass AI Structure
Plan validation, deterministic conversion, and Blueprint v2 validation before
they are loaded into preview/export.
# v3.0 Workbench Additions

- Unified workflow navigation: Generate, Import, Library, Gallery, Preview,
  Export, AI, Diagnostics, Docs.
- Import `.litematic` alpha is available through the validated import pipeline.
- Gallery saves local Blueprint v2 items and can export/import gallery bundles.
- All imports must produce an ImportReport before preview, library save, export,
  or connector build.
