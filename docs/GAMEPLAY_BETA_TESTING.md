# BlockForge v5.3.0-beta.1 Gameplay Beta Testing

Status: beta candidate checklist. Manual Minecraft regression is pending until
run in real clients and dedicated servers.

Run the checklist separately for NeoForge, Fabric, and Forge.

## v5.3 Main Flow Test Matrix

Status values must be one of `passed`, `failed`, `partial`, `pending`, or
`blocked`. Do not mark Minecraft or dedicated server tests as passed unless
they were actually run.

### NeoForge Client Required Test

Current status: `pending`

1. Start Minecraft `1.21.1` with NeoForge.
2. Enter a creative-mode world.
3. Open the Creative Tab.
4. Confirm Builder Wand / Blueprint Table / Material Cache / Builder Anchor are visible.
5. Place Blueprint Table.
6. Right-click Blueprint Table and open GUI Selector.
7. Run `/blockforge examples install`.
8. Run `/blockforge reload`.
9. Select `tiny_platform`.
10. Use Builder Wand preview.
11. Creative build.
12. Undo.
13. Switch to survival mode.
14. Clear inventory.
15. Confirm `materials selected` reports missing materials.
16. Give required materials.
17. Build succeeds and consumes materials.
18. Undo restores blocks and refunds materials.
19. Attempt build in a protection-denied region.
20. Confirm no materials are consumed and no blocks are placed.
21. Run diagnostics export.

### NeoForge Dedicated Server Required Test

Current status: `pending`

1. Start dedicated server.
2. Confirm no client-only class crash.
3. Join server.
4. Run `/blockforge status`.
5. Run `/blockforge diagnostics`.
6. Run `/blockforge diagnostics export`.
7. Test blueprint reload/list/info.
8. If possible, test wand build/undo.

### Fabric / Forge Smoke Test

Current status: `pending`

1. Client launch.
2. Creative Tab check.
3. Place Blueprint Table.
4. Open GUI or command flow.
5. Basic Wand build/undo.
6. Dedicated server smoke test if available; otherwise keep `pending`.

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
