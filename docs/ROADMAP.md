# BlockForge Roadmap

## Version Train

BlockForge now uses one feature branch per major product train. Small fixes,
polish, documentation updates, and focused tests are grouped into the active
major-version branch instead of creating separate `0.0.1` feature branches.

Recommended current branch:

- `feature/v2.0-ai-generation`

## Active Train

| Version | Theme | Goal | Release Gate |
|---|---|---|---|
| v2.0 | AI Generation Alpha | Ship optional server-side AI generation with Local Rule Generator fallback, AI Structure Plan v1, validation, preview, and export | Web checks, mocked provider tests, docs, Browser visual QA, Minecraft manual regression notes |
| v2.1 | AI Quality & UX | Improve AI prompt UX, structure plan summaries, validation messages, retry/edit flows, and local fallback polish | No API-key regression, no client key exposure, improved usability tests |
| v2.2 | Blueprint Library | Make imported/generated blueprints easier to browse, compare, reuse, and export | Library UX tests, import/export regression, path safety checks |
| v2.3 | Server Polish | Improve connector server commands, materials feedback, GUI state consistency, and admin-facing docs | NeoForge/Fabric/Forge build, command smoke checklist, no loader parity regressions |
| v2.4 | Schematic Hardening | Harden `.schem` import/export edge cases, warnings, blockstate coverage, and manual regression docs | Web schematic tests, Java parser tests where practical, real-client regression checklist |
| v3.0 | Major Product Redesign | Rework the product experience around a more complete creator workflow and stronger presentation quality | Separate product/design plan before implementation |

## v2.0 AI Generation Alpha

- Keep `v2.0.0-alpha.1` clearly marked as Alpha, not stable.
- Keep Local Rule Generator as the no-key fallback.
- Keep OpenAI provider optional and server-side only.
- Do not expose API keys to browser client code.
- Validate AI Structure Plan output before converting to `VoxelModel`.
- Keep Browser visual QA pending until manually checked.
- Keep Minecraft manual regression pending until real clients are tested.

## Near Term

- Stabilize v2.0 AI Generation Alpha: server-side provider configuration,
  Structure Plan validation, preview/export of generated blueprints, and clear
  prompt privacy/cost copy.
- Stabilize v1.6 Sponge `.schem` import/export across Web, NeoForge, Fabric,
  and Forge.
- Keep NeoForge 1.21.1 as the recommended complete Connector target.
- Keep Fabric and Forge 1.21.1 as Alpha connectors while refund undo and
  material UX mature.
- Run the v1.4.0 Blueprint Pack regression in real Minecraft clients.
- Run the v1.6.0 Schematic Interop regression in real Minecraft clients.
- Expand Blueprint v2 block state coverage beyond basic string properties.

## Export Formats

- Keep JSON, Blueprint v1/v2 JSON, `.mcfunction`, and Function Data Pack ZIP
  exports stable.
- Stabilize `.blockforgepack.zip` import/export and add imported pack library
  management after the Alpha protocol proves out.
- Add Minecraft Structure `.nbt` export.
- Add Structure Data Pack ZIP export for `/place template` workflows.
- Stabilize Sponge `.schem` v3 export/import and add mod-side export later.
- Explore Litematica `.litematic` after Sponge interop proves stable.

## Web Product

- Improve the Local Rule Generator without changing its local-only promise.
- Improve External AI setup/status/error states.
- Improve Preview PNG and README screenshot workflow.
- Keep procedural material styling; Minecraft vanilla texture files are not
  bundled.

## Connector Plan

- Preserve NeoForge, Fabric, and Forge command naming consistency.
- Keep connector core changes out of v2.0 release-process-only updates.
- Track GUI, Builder Wand, Ghost Preview, material, protection, schematic, and
  pack regressions in `docs/MOD_CONNECTOR_TESTING.md`.

See [Multi-loader Plan](./MULTILOADER_PLAN.md) for historical connector
milestones.
