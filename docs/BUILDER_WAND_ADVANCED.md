# Builder Wand Advanced

Status: `3.5.0-alpha.1` Alpha. Minecraft manual regression is pending.

The Builder Wand now has a shared pure-Java state model used by NeoForge,
Fabric, and Forge:

- `BuilderWandMode`
- `BuilderWandState`
- `PlacementOptions`
- `BuilderAnchorRef`
- `MaterialCacheRef`

## Modes

Supported modes:

- `preview`
- `build`
- `dry_run`
- `materials`
- `undo`
- `rotate`
- `mirror`
- `offset`
- `anchor`
- `clear_preview`

Sneak + right-click cycles the current mode on all three loaders. NeoForge also
exposes initial command helpers:

```text
/blockforge wand mode
/blockforge wand mode <mode>
/blockforge wand cycle
/blockforge wand options
/blockforge wand offset <x> <y> <z>
/blockforge wand mirror <x|z|none>
/blockforge wand replace <air_only|allow_replace>
/blockforge wand anchor clear
```

Fabric and Forge currently share the same wand state and sneak-cycle behavior;
command parity is planned as v3.1 train polish.

## Build Behavior

- `build` places the selected blueprint using the existing server build flow.
- `preview` refreshes state and does not place blocks.
- `dry_run` and `materials` report selected blueprint stats without placing.
- Non-build configuration modes update or describe wand state without placing.
- Offset is applied to the clicked placement base position.
- Mirror flags are tracked in state and surfaced in command/status messages;
  full mirrored placement transformation is planned.

## Anchor Workflow

1. Place a Builder Anchor.
2. Right-click it to bind the current player wand state.
3. Use `/blockforge wand options` on NeoForge to confirm anchor status.

Current limitation: anchor binding is stored in player wand state, but full
anchor-based base-position replacement remains pending manual gameplay polish.

## Server Safety Notes

- The existing protection preflight still runs before placement.
- Existing survival material checks still run before placement.
- No Minecraft manual regression has been completed for v3.1 yet.

## v3.2 Build Planner Link

Build Planner adds a command-driven construction planning layer. In
`3.5.0-alpha.1`, NeoForge exposes `/blockforge buildplan ...` commands while
Fabric and Forge keep shared manager scaffolding pending command parity.

Builder Wand BUILD mode still performs the actual existing placement path.
BuildPlan step execution is currently simulated and does not place blocks.
