# Build Planner

Status: `3.5.0-alpha.1` command-driven Alpha. Minecraft manual regression and
dedicated server smoke testing are pending.

Build Planner turns a selected blueprint placement into a deterministic plan
before anything touches the world. The plan is pure data and can be previewed,
validated, stepped, paused, resumed, cancelled, or used as a base for future
repair workflows.

## Data Model

- `BuildPlan`: player, blueprint id, base position, rotation, mirror flags,
  offset, layers, status, and creation game time.
- `BuildLayer`: a stable Y layer with sorted build steps.
- `BuildStep`: one planned block position and palette reference.
- `BuildProgress`: placed/skipped/failed/total counts plus percent.
- `BuildIssue`: validation/collision/material/protection style issue record.
- `BuildPlanOptions`: layer mode, partial-build flag, replace mode, material
  mode, repair mode, and batch limits.

The common model does not reference Minecraft classes. Loader adapters own
actual world access.

## Plan Generation

`BuildPlanFactory` accepts a blueprint, base position, rotation, mirror flags,
offset, and options. It outputs a deterministic `BuildPlan`.

- Blocks are sorted by `y`, `x`, `z`, then palette key.
- Rotation uses the existing common `BlueprintRotation`.
- `mirrorX` and `mirrorZ` transform planned coordinates in the rotated
  footprint.
- Offset is applied to the base position.
- Layers are grouped from low Y to high Y.

## Validation

`BuildPlanValidator` checks:

- duplicate planned coordinates
- out-of-world Y positions
- missing palette references

The validator does not read the Minecraft world. Loader-side preview can later
add collision, block entity, protection, and material issues.

## NeoForge Commands

NeoForge has the first command-driven Build Planner entry points:

```text
/blockforge buildplan create <id>
/blockforge buildplan create <id> at <x> <y> <z>
/blockforge buildplan preview
/blockforge buildplan start
/blockforge buildplan pause
/blockforge buildplan resume
/blockforge buildplan cancel
/blockforge buildplan step
/blockforge buildplan status
/blockforge buildplan repair
/blockforge buildplan clear
```

Fabric and Forge currently include the shared manager scaffolding. Command
parity is planned inside the v3.2 train after the NeoForge reference path is
validated.

## Execution Status

v3.5.0-alpha.1 is command-driven Alpha:

- `create` stores a per-player in-memory plan.
- `preview` reports blocks, layers, base position, and pure validation issue
  count.
- `start`, `pause`, `resume`, and `cancel` update plan status.
- `step` advances a safe simulated batch and does not place blocks yet.
- Existing direct build and Builder Wand BUILD mode remain the actual placement
  path.

Real per-tick placement, material-aware partial build, collision-aware preview,
and world-diff repair are planned follow-up work.

## Repair Mode

Common `RepairPlanFactory` can create a missing-coordinate-only repair plan from
an existing plan and a set of coordinates already present in the world.

Loader integration for reading world blocks is pending.

## Manual Testing

Do not mark passed until tested in Minecraft:

1. Install examples.
2. Reload.
3. Select `tiny_platform`.
4. Create a BuildPlan.
5. Preview it.
6. Start, pause, resume, step, status, cancel, and clear.
7. Confirm direct Builder Wand build still works.
8. Confirm undo still works for direct placement.
9. Run dedicated server smoke test.
