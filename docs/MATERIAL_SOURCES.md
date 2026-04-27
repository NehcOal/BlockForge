# BlockForge Material Sources

## v1.3 Nearby Material Source Design

BlockForge v1.3 starts the common-core design for nearby material sourcing. The
goal is to let future loader adapters build with materials from the player
inventory, nearby containers, or a mix of both.

v1.3.0 added loader-neutral data models and planning types. v1.3.1 added the
NeoForge reference adapter. v1.3.5 extends the Alpha implementation to Fabric
and Forge so all three loaders can scan loaded nearby containers, consume from
container inventories, and refund to original sources during undo when possible.

## Why Material Sources

The v1.2 Fabric and Forge Alpha material flow only checked and consumed the
player inventory. Larger blueprints usually need more materials than a player
wants to carry directly. A material source abstraction gives all loaders the
same vocabulary for reporting where materials can come from while each loader
uses its own Minecraft inventory API.

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
- dimension id
- priority

`MaterialSourceScanResult` describes what the loader found:

- source refs
- scanned block count
- found container count
- warnings

The plan and result do not touch Minecraft world APIs. NeoForge, Fabric, and
Forge each adapt them to loader-specific scanners in v1.3.5.

## Reports, Reservations, And Transactions

`MaterialSourceReport` summarizes material availability by source. It is built
from `MaterialSourceItemEntry` rows and can report total required, available,
missing, and whether the material plan is enough.

`MaterialReservation` is a future-safe record for reserved materials before
placement. v1.3.1 does not persist reservations across ticks; NeoForge consumes
from the generated source report immediately before placement.

`ConsumedMaterialEntry` and `MaterialTransaction` now include optional source
metadata while preserving the existing player-inventory-only constructors.
Existing Fabric, Forge, and NeoForge material refund flows continue to work as
before.

## Testing Strategy

v1.3.x uses a batched testing strategy:

- Small v1.3.x versions run Web and Gradle build validation only.
- Manual Minecraft testing is batched into the v1.3.5 multiloader regression
  pass.
- Mark a loader as passed only after that loader has been run in a real
  Minecraft client.

## Current Status

- `v1.3.0`: common core only.
- `v1.3.1`: NeoForge nearby container sourcing reference implementation.
- `v1.3.5`: NeoForge / Fabric / Forge nearby container sourcing Alpha.
- NeoForge adapter: Alpha, disabled by default, backed by common config.
- Fabric adapter: Alpha, disabled by default, uses runtime `/blockforge sources`
  settings for the current server session until config file support lands.
- Forge adapter: Alpha, disabled by default, uses runtime `/blockforge sources`
  settings for the current server session until config file support lands.
- Forge manual status: focused source-aware consume/refund smoke test passed on
  2026-04-26. Player-sourced materials returned to player inventory, and
  chest-sourced materials returned to the original chest.
- NeoForge / Fabric manual status: nearby container sourcing still pending for
  the v1.3.5 multiloader regression.
- GUI material source display: minimal hint only.

## Loader Adapters

NeoForge uses its common config to keep nearby containers disabled by default.
When enabled, it scans around the build origin with radius `8` by default and
stops after `64` container sources. The scanner only checks loaded chunks in the
current dimension and queries item handler capability from block entities.

Fabric uses vanilla inventory access for nearby loaded block entities in this
Alpha. It does not load chunks, does not scan across dimensions, and keeps
nearby containers disabled by default. Use `/blockforge sources enable` plus
optional `/blockforge sources priority ...` and `/blockforge sources radius ...`
for the current server session until Fabric config file support is added.

Forge uses item handler capability on loaded block entities. It does not assume
all containers are chests, does not load chunks, and keeps nearby containers
disabled by default. Use `/blockforge sources enable` plus optional
`/blockforge sources priority ...` and `/blockforge sources radius ...` for the
current server session until Forge config file support is added.

The source priority controls reservation order:

- `PLAYER_FIRST`: use player inventory first, then nearby containers.
- `CONTAINER_FIRST`: use nearby containers first, then player inventory.
- `PLAYER_ONLY`: ignore containers even if scanning is enabled.
- `CONTAINER_ONLY`: ignore player inventory for material sourcing.

Undo records source-aware material transactions. If
`returnRefundsToOriginalSource=true`, adapters try to insert refunded items back
into the original nearby container; overflow falls back to player inventory and
then drops near the player.

## Safety Limits

- Nearby containers are disabled by default.
- Default search radius is `8`.
- Default container scan cap is `64`.
- Adapters must not scan across dimensions.
- Adapters must not load unloaded chunks just to find materials.
- Adapters must not automatically take from protected containers; this
  must integrate with permissions/protection systems first.
