# BlockForge Material Sources

## v1.3 Nearby Material Source Design

BlockForge v1.3 starts the common-core design for nearby material sourcing. The
goal is to let future loader adapters build with materials from the player
inventory, nearby containers, or a mix of both.

v1.3.0 only adds loader-neutral data models and planning types. It does not
scan Minecraft worlds, read container inventories, consume items from
containers, refund items to containers, or change Builder Wand placement.

## Why Material Sources

The current Fabric and Forge Alpha material flow only checks and consumes the
player inventory. Larger blueprints usually need more materials than a player
wants to carry directly. A material source abstraction gives all loaders the
same vocabulary for reporting where materials can come from before each loader
implements its own Minecraft API integration.

## Source Types

- `PLAYER_INVENTORY`: materials carried by the player.
- `NEARBY_CONTAINER`: materials found in a nearby chest, barrel, shulker box,
  or another future container adapter.
- `MIXED`: a report or transaction that combines multiple source types.

Player inventory is immediate and already supported by the loader adapters.
Nearby containers require world scanning, permissions, loaded-chunk checks, and
container inventory mutation; those remain adapter responsibilities.

## MaterialSourceConfig

Default config:

| Field | Default | Meaning |
|---|---:|---|
| `enableNearbyContainers` | `false` | Nearby containers are off by default. |
| `searchRadius` | `8` | Search radius around the build/player center. |
| `priority` | `PLAYER_FIRST` | Prefer player inventory before containers. |
| `allowPartialFromContainers` | `true` | Allow one requirement to be fulfilled by multiple sources. |
| `returnRefundsToOriginalSource` | `true` | Future refunds should prefer the original source. |
| `maxContainersScanned` | `64` | Safety cap for scanned containers. |

The default keeps current behavior unchanged: player inventory remains the only
active source until a loader adapter explicitly enables nearby containers.

## Scan Plan And Result

`MaterialSourceScanPlan` describes where a loader should scan:

- player id
- blueprint id
- scan center position
- radius
- maximum containers
- priority

`MaterialSourceScanResult` describes what the loader found:

- source refs
- scanned block count
- found container count
- warnings

The plan and result do not touch Minecraft world APIs. Fabric, Forge, and
NeoForge will each implement their own scanner later.

## Reports, Reservations, And Transactions

`MaterialSourceReport` summarizes material availability by source. It is built
from `MaterialSourceItemEntry` rows and can report total required, available,
missing, and whether the material plan is enough.

`MaterialReservation` is a future-safe record for reserved materials before
placement. v1.3.0 does not persist or enforce reservations.

`ConsumedMaterialEntry` and `MaterialTransaction` now include optional source
metadata while preserving the existing player-inventory-only constructors.
Existing Fabric, Forge, and NeoForge material refund flows continue to work as
before.

## Testing Strategy

v1.3.x uses a batched testing strategy:

- Small v1.3.x versions run Web and Gradle build validation only.
- Manual Minecraft testing is deferred until the v1.3.5 multiloader regression
  pass.
- Do not mark nearby container sourcing as passed until real loader adapters and
  in-game tests exist.

## Current Status

- `v1.3.0`: common core only.
- NeoForge adapter: planned.
- Fabric adapter: planned.
- Forge adapter: planned.
- GUI material source display: planned.

## Safety Limits

- Nearby containers are disabled by default.
- Default search radius is `8`.
- Default container scan cap is `64`.
- Future adapters must not scan across dimensions.
- Future adapters must not load unloaded chunks just to find materials.
- Future adapters must not automatically take from protected containers; this
  must integrate with permissions/protection systems first.
