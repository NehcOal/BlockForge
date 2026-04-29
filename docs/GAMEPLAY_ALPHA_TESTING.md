# BlockForge Gameplay Alpha Testing

Version: 4.4.0-beta.1

Status: pending manual Minecraft regression and dedicated server smoke testing.

Latest QA report: [Gameplay Alpha QA Report](./GAMEPLAY_ALPHA_QA_REPORT.md).

Run this checklist separately for NeoForge, Fabric, and Forge.

## Client Checklist

- Start Minecraft Java 1.21.1 with Java 21.
- Confirm BlockForge items appear in creative inventory:
  - Builder Wand
  - Blueprint Table
  - Material Cache
  - Builder Anchor
  - Builder Station
  - Material Link
  - Construction Core
- Place Blueprint Table.
- Right-click Blueprint Table and confirm the Blueprint Selector opens.
- Place Material Cache.
- Place Builder Anchor.
- Bind Builder Wand to Builder Anchor.
- Select `tiny_platform`.
- Test wand BUILD.
- Test wand DRY_RUN.
- Test wand MATERIALS.
- Test wand UNDO.
- Create and preview a BuildPlan.
- Run BuildPlan step.
- Place Builder Station.
- Run station bind / status / step scaffold commands.
- Test quota denied behavior once enforcement is wired.
- Test audit command scaffold.
- Export diagnostics.

## Dedicated Server Checklist

- Start dedicated server.
- Join with a client.
- Run `/blockforge status`.
- Run `/blockforge diagnostics`.
- Run `/blockforge examples install`.
- Run `/blockforge reload`.
- Run `/blockforge buildplan create tiny_platform`.
- Run `/blockforge station status`.
- Confirm no client-only class crash.

## Pending Status

- Minecraft manual regression: pending
- Dedicated server smoke test: pending
- Public release QA: pending
