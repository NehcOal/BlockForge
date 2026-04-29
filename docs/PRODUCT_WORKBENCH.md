# Product Workbench

BlockForge v3.2.0-alpha.1 introduces a unified product workbench shell. It is
an information architecture update, not a rewrite of the existing stack.

## Navigation

Top-level workflows:

- Generate
- Import
- Library
- Gallery
- Preview
- Export
- AI
- Diagnostics
- Docs

The current app keeps the existing dark voxel developer-tool style while adding
a left workflow rail, topbar, active content region, and bottom status bar.

## Status System

The workbench status model tracks:

- active blueprint id
- active source
- validation status
- warning and error counts
- render mode
- import job status
- AI provider status
- save status

## Command Palette

The alpha command palette action registry includes:

- Import Blueprint JSON
- Import `.schem`
- Import `.litematic`
- Generate locally
- Generate with AI
- Export Blueprint v2
- Export `.schem`
- Export PNG
- Save to Library
- Open Gallery
- Run Validation

Keyboard interaction is planned; the action registry and UI surface are present
for v3.0 alpha.

## Empty States

When no active blueprint is loaded, the workbench should offer quick actions:

- Generate locally
- Import Blueprint
- Open Gallery

## QA Status

- Browser visual QA: pending.
- 1024px layout check: pending.
- ExportPanel overflow check: pending.
