# BlockForge v4.2.0-beta.1 Gameplay Beta Testing

Status: beta candidate checklist. Manual Minecraft regression is pending until
run in real clients and dedicated servers.

Run the checklist separately for NeoForge, Fabric, and Forge.

## Client Flow

1. Create a new Minecraft 1.21.1 world with Java 21.
2. Confirm BlockForge items appear in creative inventory:
   - Builder Wand
   - Blueprint Table
   - Material Cache
   - Builder Anchor
   - Builder Station
   - Material Link
   - Construction Core
3. Place Blueprint Table and open the Blueprint Selector.
4. Select `tiny_platform`.
5. Place Material Cache and add materials.
6. Place Builder Anchor and bind Builder Wand.
7. Run wand DRY_RUN.
8. Run wand BUILD.
9. Run wand UNDO.
10. Create a BuildPlan.
11. Preview and step the BuildPlan.
12. Place Builder Station.
13. Bind blueprint / anchor / cache.
14. Run station start / pause / resume / cancel / step.
15. Verify quota denial does not consume materials.
16. Verify protection denial does not consume materials.
17. Export diagnostics.

## Dedicated Server Flow

1. Start a dedicated server for each loader.
2. Join with a matching client.
3. Run `/blockforge reload`.
4. Run `/blockforge status`.
5. Run `/blockforge diagnostics`.
6. Run `/blockforge station status`.
7. Run `/blockforge admin audit`.
8. Run `/blockforge quota get <player>`.
9. Confirm no client-only class is loaded by the server.

## Required Status Language

- Use `passed` only after manual execution.
- Use `pending` for tests not yet run.
- Use `blocked` when the environment cannot launch the client/server.
- Keep `partial` for features that compile and have pure tests but do not yet
  perform full in-world behavior.

