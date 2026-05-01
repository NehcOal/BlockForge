# BlockForge v5.3.0-beta.1 - Gameplay Release Hardening

## Release Train Rules

- `alpha`: feature candidate. Use for active development and feature-complete
  previews that still need regression and visual QA.
- `rc`: regression candidate. Use after feature scope is frozen and release
  validation is the primary work.
- `stable`: official public release. Use only after required automated checks,
  release documentation, and manual gates are complete.
- `patch`: bugfix only for an already published `stable` release. Do not use
  patch versions for new small features or polish.

Small polish, docs, tests, and minor fixes should be collected in the active
major-version feature branch and release train.

## Release Type

- Version: `5.3.0-beta.1`
- Type: Gameplay Alpha candidate
- Stability: Alpha for all loaders; NeoForge has the richest command scaffold

## Supported Minecraft Version

- Minecraft Java Edition: `1.21.1`
- Java: `21`

## Supported Loaders

- NeoForge `21.1.227`: most complete Connector
- Fabric Loader `0.19.2` with Fabric API `0.116.11+1.21.1`: GUI Selector + Builder Wand + Ghost Preview + Survival Material Cost + Material Refund Undo + Nearby Material Source Alpha
- Forge `52.1.14`: GUI Selector + Builder Wand + Ghost Preview + Survival Material Cost + Material Refund Undo + Nearby Material Source Alpha

## Download Files

- `blockforge-connector-neoforge-5.3.0-beta.1.jar`
- `blockforge-connector-fabric-5.3.0-beta.1.jar`
- `blockforge-connector-forge-5.3.0-beta.1.jar`

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
| Nearby chest material sourcing | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Blueprint Pack import | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Permission nodes | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Protection regions | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Sponge `.schem` import | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Blueprint Table | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Material Cache block | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Builder Anchor | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Advanced Builder Wand modes | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Build Planner model | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Layer Build planning | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Build Pause/Resume state | ✅ Alpha | scaffold | scaffold |
| Repair Plan pure logic | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Collision/Replace report | pure validation | pure validation | pure validation |
| Builder Station | ✅ Alpha scaffold | ✅ Alpha scaffold | ✅ Alpha scaffold |
| Material Link | ✅ Alpha scaffold | ✅ Alpha scaffold | ✅ Alpha scaffold |
| Construction Core | ✅ Alpha scaffold | ✅ Alpha scaffold | ✅ Alpha scaffold |
| Server Audit / Quota DTOs | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| NeoForge Station Commands | ✅ Alpha scaffold | planned | planned |
| Station Runtime pure tick executor | ✅ Beta | ✅ common | ✅ common |
| Material Cache GUI state | ✅ common | ✅ common | ✅ common |
| Builder Station GUI action model | ✅ common | ✅ common | ✅ common |
| Audit JSONL formatter | ✅ common | ✅ common | ✅ common |
| Admin rollback decision model | ✅ common | ✅ common | ✅ common |
| Gameplay Beta QA docs | ✅ Beta | ✅ Beta | ✅ Beta |
| Settlement Core | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Contract Board | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Reward Crate | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Architect Desk | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Architect Ledger / Token / Seal | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Contract generation | ✅ common | ✅ common | ✅ common |
| Contract verification heuristic | ✅ common | ✅ common | ✅ common |
| Rewards / reputation | ✅ common | ✅ common | ✅ common |
| Settlement Events | ✅ Alpha | ✅ partial | ✅ partial |
| Settlement Stability | ✅ common | ✅ common | ✅ common |
| Event Board | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Project Chains | ✅ common | ✅ common | ✅ common |
| Project Map | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| Emergency Repairs | ✅ common | ✅ common | ✅ common |
| Emergency Beacon | ✅ Alpha | ✅ Alpha | ✅ Alpha |
| House Plan | ✅ Alpha | ✅ common | ✅ common |
| Rule-based House Generator | ✅ Alpha | ✅ common | ✅ common |
| House Presets | ✅ Alpha | ✅ common | ✅ common |
| Roof Generator | ✅ Alpha | ✅ common | ✅ common |
| Door / Window Placement | ✅ Alpha | ✅ common | ✅ common |
| House Quality Check | ✅ Alpha | ✅ common | ✅ common |
| House Material Estimate | ✅ Alpha | ✅ common | ✅ common |
| House -> Blueprint v2 | ✅ Alpha | ✅ common | ✅ common |
| Web House Designer | ✅ Alpha | n/a | n/a |
| In-game House Commands | ✅ Alpha | ✅ partial | ✅ partial |
| Settlement persistence | partial | partial | partial |
| Contract Board GUI | planned | planned | planned |

## What Works

- Web House Designer.
- Blueprint JSON v1/v2 export.
- Blueprint Pack export.
- Sponge `.schem` export.
- NeoForge recommended gameplay flow.
- Builder Wand.
- Ghost Preview.
- Build / Undo.
- Survival material cost / refund.

## Partial / Experimental

- Material Cache GUI: partial.
- Builder Station: partial.
- Station runtime: partial.
- Admin rollback: partial.
- Litematica: experimental.
- External AI: pending live test.
- Fabric / Forge advanced parity: partial.
- Settlement / Contract / Progression / Events: experimental.

## Validation

- Automated checks are required before every beta candidate.
- Minecraft manual regression remains pending unless explicitly recorded.
- Dedicated server smoke test remains pending unless explicitly recorded.

## v5.2 Highlights

- Adds HousePlan pure data model and deterministic house generation.
- Adds eight alpha house presets focused on practical Minecraft-style builds.
- Adds Web House Designer for preview/export-ready house models.
- Adds HousePlan material estimates and heuristic quality scoring.
- Adds NeoForge `/blockforge house ...` command reference and matching
  Fabric/Forge alpha command surfaces.

## v4.1 Highlights

- Material Cache GUI state now has a common server-safe payload.
- Builder Station GUI actions now have common server-side validation.
- Construction Core exposes a common project overview status view.
- Audit entries can be serialized as JSONL for the planned audit file path.
- Admin rollback decisions now check permission, snapshot availability, and
  protection policy before world rollback integration.
- Cooldown policy returns clear remaining-time messages.

## v4.0 Highlights

- Builder Station pure tick runtime applies loaded chunk, protection, material,
  quota, and cooldown gates before marking BuildPlan batches.
- Diagnostics now include active station, active job, audit, quota, and material
  network counts.
- Gameplay Beta testing and QA reports are documented for client and dedicated
  server runs.

## v4.0 Known Limitations

- Station world placement integration remains partial.
- Material Cache GUI and Builder Station GUI are planned / partial.
- Audit persistence and admin rollback remain planned / partial.
- Minecraft manual regression is pending.
- Dedicated server smoke test is pending.

## v3.5 Highlights

- Builder Station, Material Link, and Construction Core are registered on all
  three loaders.
- Material Network source types are modeled in common material planning.
- Server gameplay DTOs cover audit entries, quota, cooldown, projects, and
  admin build summaries.
- NeoForge exposes station/admin/quota command scaffolds.
- Gameplay alpha resources include lang keys, models, recipes, loot tables, and
  original placeholder textures.

## v3.5 Known Limitations

- Builder Station `step` is still a command-driven scaffold and does not place
  world blocks.
- Material Cache inventory-backed sourcing remains partial.
- Admin audit export and rollback are planned.
- Minecraft manual regression is pending.
- Dedicated server smoke test is pending.

## v3.2 Highlights

- Common BuildPlan / BuildLayer / BuildStep model.
- Deterministic low-to-high layer planning.
- Pure BuildPlan validation for duplicates, missing palette references, and
  world-height issues.
- Per-player BuildPlan manager scaffolding across all three loaders.
- NeoForge command-driven BuildPlan Alpha commands.
- Missing-coordinate repair plan pure logic.

## v3.2 Known Limitations

- BuildPlan `step` is simulated in this alpha and does not place blocks.
- Fabric/Forge BuildPlan command parity is planned.
- Real tick-based execution is planned for the Builder Station train.
- Minecraft manual regression is pending.
- Dedicated server smoke test is pending.

## v3.1 Highlights

- Blueprint Table opens the existing Blueprint Selector GUI from an in-world
  block interaction.
- Material Cache is registered as the dedicated BlockForge material source
  block for upcoming cache-backed material flow.
- Builder Anchor binds the current player Builder Wand state to an in-world
  anchor coordinate.
- Builder Wand now tracks preview/build/dry-run/materials/undo/rotate/mirror/
  offset/anchor/clear-preview modes.
- Sneak + right-click cycles wand mode on all three loaders.
- NeoForge includes first-pass `/blockforge wand ...` command helpers.

## v3.1 Known Limitations

- Material Cache inventory-backed sourcing is not yet complete.
- Mirror flags are tracked but full mirrored placement transform is pending.
- Anchor-fixed base replacement and fixed ghost preview still require manual
  gameplay polish.
- Minecraft manual regression is pending.
- Dedicated server smoke test is pending.
| Sponge `.schem` export | planned | planned | planned |

NeoForge is the most complete connector for now. Fabric and Forge include GUI
Selector, Builder Wand, Ghost Preview, Survival Material Cost, and Material
Refund Undo Alpha support. Nearby chest material sourcing is implemented as a
multiloader Alpha feature in v1.3.5 and disabled by default on every loader.
Blueprint Pack import is Alpha on all three connectors; Web export/import is
also Alpha.
Sponge `.schem` v3 import is Alpha on Web and all three connectors. Web
schematic export is Alpha. Mod-side schematic export is planned.

## Web Workbench Highlights

- AI Prompt Presets.
- Multi-candidate generation.
- AI Quality Score and best-candidate badge.
- Structure Plan Viewer.
- Candidate Compare.
- Refine workflow.
- Generation History.
- Local Blueprint Library.
- Workspace export/import.
- Import report, import job queue, and import worker fallback scaffolding.
- Local Rule Generator fallback remains available without API keys.
- Optional OpenAI provider is available through server-side API routes.
- AI Structure Plan v1 defines high-level structure elements before block
  conversion.
- Structured validation pipeline blocks invalid AI output before preview/export.
- Prompt privacy and API cost notices are shown in the Web UI.
- AI-generated blueprints can use the normal 3D preview and multi-format export
  flow.
- Import Blueprint JSON v1/v2.
- Import Sponge `.schem` v3.
- Import `.blockforgepack.zip`.
- Show field-level Blueprint validation reports.
- Generate deterministic local prompt-rule models without external AI API use.
- Export JSON, Blueprint v1/v2, Blueprint Pack, `.schem`, `.mcfunction`, and
  Data Pack ZIP.

Known limitations:

- No guaranteed architectural quality.
- No material-aware AI generation yet.
- No multiplayer AI generation queue.
- No persistent cloud generation history.
- External provider requires server runtime and API key.
- Browser visual QA is pending.
- Minecraft manual regression is pending.

## Verified Tests

- `pnpm lint`
- `pnpm test`
- `pnpm build`
- `mod/neoforge-connector/gradlew.bat build`
- `mod/fabric-connector/gradlew.bat build`
- `mod/forge-connector/gradlew.bat build`

Manual Minecraft status:

- NeoForge: full Connector flows previously verified; v1.3.5 nearby container
  sourcing manual testing pending.
- Fabric: command-loop Alpha verified; GUI, Builder Wand, Ghost Preview,
  survival material cost, material refund undo, and nearby container sourcing
  manual testing pending.
- Forge: command-loop Alpha verified; GUI, Builder Wand, Ghost Preview,
  survival material cost, and material refund undo previously verified. A
  v1.3.5 focused nearby container smoke test passed: player-sourced materials
  refund to player inventory, and chest-sourced materials refund to the
  original chest.
- v1.9.0 build validation: pending before release.
- Full NeoForge / Fabric / Forge v1.9.0 Minecraft regression testing
  is still pending.
- Browser visual QA is pending.

## GUI Search Highlights

- NeoForge, Fabric, and Forge Blueprint Selector GUI now supports search,
  source filtering, warning filtering, sorting, and server-side pagination.
- Rows show source tags and best-effort warning badges.
- Selection, Builder Wand, and Ghost Preview remain linked through the
  server-validated selection request.

## Rendering Highlights

- Web preview uses InstancedMesh for larger voxel models in Auto render mode.
- Preview PNG export can create README, release notes, and gallery screenshots.
- Material styling is procedural and Minecraft-inspired; no Minecraft vanilla
  texture files are bundled.

## Known Limitations

- Fabric and Forge Ghost Preview only renders a bounding box and ground footprint.
- Fabric and Forge preview does not scan collisions, show material status,
  render individual blocks, or preview textures.
- Fabric and Forge Material Refund Undo is Alpha; inventory overflow drops near
  the player.
- Nearby container sourcing is disabled by default. Forge has a focused
  source-aware refund smoke pass; NeoForge and Fabric nearby source testing
  remain pending.
- Fabric and Forge do not include GUI material source details, recipe
  substitutions, protected-container permission checks, or BlockEntity NBT undo.
- Fabric and Forge Builder Wand support is Alpha and has a 2 second per-player cooldown.
- Fabric and Forge undo snapshots are in-memory and capped at 20 snapshots per player.
- Fabric and Forge do not persist undo history across disconnects or restarts.
- Dedicated server smoke testing is pending for this release candidate.
- Web pack import currently displays an import summary and does not persist an
  imported pack library.
- Web Local Prompt Rule Generator is deterministic and local-only. Optional
  External AI Generation Alpha requires a server runtime and API key.
- Blueprint Pack zips are loaded from `config/blockforge/packs/`; there is no
  remote download or server-client pack sync.
- No automatic publishing to Modrinth or CurseForge is performed by this release
  template.
- Sponge `.schem` support ignores entities, biomes, and full BlockEntity NBT
  fidelity in this Alpha.

## Upgrade Notes

- Use the jar that matches your loader.
- Keep existing blueprint JSON files in `.minecraft/config/blockforge/blueprints/`.
- NeoForge users should prefer the NeoForge jar for the complete experience.
- Fabric and Forge users should treat this as an Alpha GUI + command +
  material-cost + refund-undo workflow.
## v1.6.0 Schematic Notes

- Sponge `.schem` support targets v3 GZip NBT files.
- Schematics are loaded from `config/blockforge/schematics/`.
- Imported schematic ids use `schem/<file_name>`.
- Litematica `.litematic` is planned.

## v1.5.0 Security Notes

- Permissions/protection support is Alpha.
- Supported loaders: NeoForge, Fabric, Forge.
- Manual Minecraft regression testing pending unless explicitly verified.
- External permission and claim integrations are planned optional adapters.
# BlockForge v5.3.0-beta.1 - Product Workbench Alpha

## Summary

Release Readiness + Litematica Interop Alpha + Blueprint Gallery + Server Admin
Polish + Unified Product Workbench.

## Major Changes

- Unified Product Workbench shell and status bar.
- Command Palette action registry.
- Experimental `.litematic` import pipeline into Blueprint v2.
- Local Blueprint Gallery and `.blockforgegallery.zip` bundle format.
- Server diagnostics schema/docs and server issue template.
- v3.0 QA/readiness documentation.

## Known Limitations

- Browser visual QA pending.
- Minecraft manual regression pending.
- External AI live test pending.
- Dedicated server smoke test pending.
- Litematica support is partial and experimental.
- No Modrinth / CurseForge publishing unless performed manually.
