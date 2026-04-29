# Dedicated Server

Status: Dedicated server smoke test pending.

## Install

1. Install the matching loader server for Minecraft Java `1.21.1`.
2. Place the matching BlockForge jar in the server `mods/` directory.
3. Start the server once to create config directories.
4. Add blueprints, packs, or schematics under `config/blockforge/`.

## Config Paths

- `config/blockforge/blueprints/`
- `config/blockforge/packs/`
- `config/blockforge/schematics/`
- `config/blockforge/protection-regions.json`

## Smoke Checklist

- Server starts without client-only class errors.
- `/blockforge reload` works.
- `/blockforge list` shows expected registry entries.
- Build and undo work in an allowed area.
- Protected areas deny build without consuming materials.
- Pack and schematic loading warnings are readable.
- Blueprint Table opens GUI without loading client-only screen classes on the
  server.
- Builder Anchor and Material Cache interactions do not crash the server.

Do not mark this passed until a real dedicated server test is run.
# v3.0 Dedicated Server Checklist

Dedicated server smoke test is pending.

- Install NeoForge / Fabric / Forge server for Minecraft 1.21.1.
- Use Java 21.
- Place blueprints in `config/blockforge/blueprints/`.
- Place packs in `config/blockforge/packs/`.
- Place schematics in `config/blockforge/schematics/`.
- Place Litematica alpha files in `config/blockforge/litematics/`.
- Run `/blockforge status`.
- Run `/blockforge diagnostics`.
- Export diagnostics JSON before filing a server issue.
## v3.5 Gameplay Alpha Notes

- Verify Builder Station commands with `/blockforge station status`.
- Verify admin scaffold with `/blockforge admin audit`.
- Verify quota scaffold with `/blockforge quota get <player>`.
- Keep `Minecraft manual regression` and `Dedicated server smoke test` marked
  pending until a real server is started and joined by a client.
- Do not mark Modrinth / CurseForge publishing complete unless release assets
  have actually been uploaded.
