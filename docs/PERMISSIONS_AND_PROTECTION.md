# Permissions and Protection

BlockForge v1.5.0-alpha.1 adds a loader-neutral permission and protection
layer for multiplayer safety. The goal is to prevent material loss and partial
builds when a player is not allowed to build in a target area.

## Permission Nodes

| Action | Node | Fallback level |
|---|---|---:|
| Open GUI | `blockforge.command.gui` | 0 |
| Reload blueprints | `blockforge.command.reload` | 2 |
| Reload packs | `blockforge.command.packs.reload` | 2 |
| Install examples | `blockforge.command.examples.install` | 2 |
| Scan sources | `blockforge.command.sources.scan` | 0 |
| Use containers | `blockforge.command.sources.use_containers` | 0 |
| Build command | `blockforge.build.command` | 2 |
| Builder Wand build | `blockforge.build.wand` | 0 |
| Bypass build limits | `blockforge.build.bypass_limits` | 2 |
| Bypass protection | `blockforge.build.bypass_protection` | 2 |
| Undo own builds | `blockforge.undo.self` | 0 |
| Undo others | `blockforge.undo.others` | 2 |
| Admin config | `blockforge.admin.config` | 2 |
| Admin protection | `blockforge.admin.protection` | 2 |

NeoForge, Fabric, and Forge currently use vanilla permission level fallback
unless an external permission provider is added later. `requirePermissions`
defaults to `false`, so existing single-player and OP workflows are not broken.

## Protection Regions

All loaders read:

```text
config/blockforge/protection-regions.json
```

Example:

```json
{
  "schemaVersion": 1,
  "regions": [
    {
      "id": "spawn",
      "dimensionId": "minecraft:overworld",
      "minX": -64,
      "minY": -64,
      "minZ": -64,
      "maxX": 64,
      "maxY": 320,
      "maxZ": 64,
      "mode": "DENY",
      "allowedPlayers": [],
      "deniedPlayers": [],
      "allowedPermissions": ["blockforge.build.bypass_protection"],
      "tags": ["spawn"],
      "description": "Protects spawn from BlockForge builds."
    }
  ]
}
```

Region min/max coordinates are normalized during parsing. Invalid JSON or
invalid regions produce warnings and do not crash the game.

## Build Preflight

Build command and Builder Wand builds use this order:

1. Resolve blueprint.
2. Resolve base position and rotation.
3. Compute build bounding area.
4. Check permission node.
5. Check protection regions.
6. If denied, stop immediately.
7. Check and consume materials.
8. Place blocks.
9. Record undo snapshot.

This guarantees protection denial happens before material consumption.

## Nearby Containers

Nearby material source scans and consumption skip containers inside denied
regions unless the player has bypass permission. Material refund tries the
original source first only when that container is still accessible; otherwise it
falls back to the player inventory, then drops overflow near the player.

## Commands

```mcfunction
/blockforge protection folder
/blockforge protection reload
/blockforge protection list
/blockforge protection info <region>
/blockforge protection check <blueprint>
/blockforge protection check <blueprint> at <x> <y> <z>
/blockforge permissions check <node>
```

`protection reload` requires admin/OP fallback level 2 in the current Alpha.

## Current Limitations

- No hard dependency on LuckPerms, Fabric Permissions API, FTB Chunks, or Open
  Parties and Claims yet.
- No visual region map.
- Built-in regions only affect BlockForge actions.
- Manual Minecraft regression testing is pending for v1.5.0.
