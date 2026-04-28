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
| v2.5 | AI Productization + Local Library + Server UX + Import Hardening | Package v2.1-v2.5 as one Alpha train | Web checks, mocked provider tests, import/library tests, docs, Browser visual QA pending, Minecraft manual regression pending |
| v2.6 | Multiplayer / Server QA | Run deeper server and multiplayer validation passes | Dedicated server smoke test and command regression checklist |
| v2.7 | Litematica Interop | Explore `.litematic` import/export after schematic hardening | Design doc and PoC before mainline support |
| v2.8 | Blueprint Marketplace / Sharing | Explore sharing workflows without compromising local-first behavior | Product design and safety review |
| v3.0 | Product Redesign | Rework the product experience around a more complete creator workflow and stronger presentation quality | Separate product/design plan before implementation |

## v2.0 AI Generation Alpha

- Keep `v2.5.0-alpha.1` clearly marked as Alpha, not stable.
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
