# Litematica Interop Alpha

BlockForge v5.3.0-beta.1 adds experimental `.litematic` import plumbing. The
goal is safe conversion into Blueprint v2, not full Litematica feature parity.

## Supported Scope

- Web import path for `.litematic` alpha fixtures and parsed Litematic data.
- Region metadata, palette entries, and block coordinates.
- One or more regions merged into a Blueprint v2 using relative offsets.
- Validation report before preview, library save, export, or connector build.

## Mapping Rules

- Litematic region size becomes Blueprint v2 `size`.
- Region palette entries become Blueprint v2 palette states.
- Region blocks become Blueprint v2 blocks referencing palette keys.
- Multiple regions are merged into one Blueprint and emit a warning.
- Unknown or partial block states are preserved as `minecraft:*` strings when
  possible and surfaced as warnings.

## Current Limits

- Entities are ignored.
- Tile entities and block entities are partial or ignored.
- No DataFixer cross-version conversion.
- No full Litematica feature parity.
- Binary `.litematic` NBT parsing is still alpha; malformed files return a
  friendly error report instead of entering preview/export.

## Safety Limits

- Max file size: 10 MB.
- Max regions: 32.
- Max total volume: 1,000,000 blocks.
- Max palette size: 4096.
- Invalid NBT, unsupported fields, and malformed block data must produce
  validation errors or warnings and must not crash the UI.

## QA Status

- Browser visual QA: pending.
- Minecraft manual regression: pending.
- Dedicated server smoke test: pending.
