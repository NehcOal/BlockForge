# Gameplay Blocks

Status: `5.1.0-alpha.1` Alpha candidate. Minecraft manual regression is pending.

See also: [Gameplay GUI](./GAMEPLAY_GUI.md).

BlockForge v3.1 adds the first gameplay utility blocks for NeoForge, Fabric,
and Forge. These blocks are meant to reduce command-only workflows and make the
Connector easier to use in survival and server play.

## Blueprint Table

`blockforge_connector:blueprint_table`

- Right-click opens the existing BlockForge Blueprint Selector GUI.
- The server still owns blueprint selection and validation.
- If no blueprints are loaded, the existing GUI empty state is shown.
- Use this as the in-world entry point instead of `/blockforge gui`.

## Material Cache

`blockforge_connector:material_cache`

- Registered on NeoForge, Fabric, and Forge as an Alpha gameplay block.
- Intended role: a BlockForge-specific material source for Builder Wand builds.
- v5.1.0-alpha.1 exposes the block and material source DTOs first.
- Inventory-backed cache sourcing and refund targeting are planned follow-up
  polish inside the v3.1 train.

Current limitation: the Alpha block does not yet provide a full container menu
or consume materials directly. Builds still use existing player inventory and
nearby-container material flow.

## Builder Anchor

`blockforge_connector:builder_anchor`

- Right-click binds the player Builder Wand state to the anchor position.
- The anchor id is stored as the block coordinate string.
- Advanced placement currently applies wand offset at build time.
- Anchor-fixed placement and ghost-preview pinning are Alpha work-in-progress
  and must be manually tested before release notes claim full parity.

## Planned Blocks

- Preview Projector: planned.
- Construction Marker: planned.

## Settlement Contracts Blocks

`blockforge_connector:settlement_core`

- Alpha settlement ownership and status entry point.
- Use `/blockforge settlement create <name>` and `/blockforge settlement info`.
- Persistence and GUI are partial in v5.0.

`blockforge_connector:contract_board`

- Alpha contract listing / accept / verify / submit entry point.
- Use `/blockforge contracts list`, `info`, `accept`, `verify`, and `submit`.
- Full GUI is planned.

`blockforge_connector:reward_crate`

- Alpha reward claim block.
- v5.0 rewards are represented by common DTOs and command feedback.

`blockforge_connector:architect_desk`

- Alpha architect profile and reputation entry point.
- Use `/blockforge architect profile`, `contracts`, and `reputation`.

Items:

- `blockforge_connector:architect_ledger`
- `blockforge_connector:contract_token`
- `blockforge_connector:architect_seal`

## Settlement Events Blocks

`blockforge_connector:event_board`

- Alpha entry point for settlement event lists, generated event contracts, and
  event resolution.
- Use `/blockforge events list`, `info`, `refresh`, `resolve`, and `ignore`.

`blockforge_connector:project_map`

- Alpha entry point for project chain visibility.
- Use `/blockforge projects list`, `info`, `activate`, and `status`.

`blockforge_connector:emergency_beacon`

- Alpha entry point for emergency repair requests.
- Use `/blockforge emergency list`, `info`, `repair`, and `verify`.

`blockforge_connector:supply_depot`

- Settlement-level material depot scaffold.
- Full inventory aggregation remains planned.

Items:

- `blockforge_connector:event_notice`
- `blockforge_connector:project_charter`
- `blockforge_connector:emergency_repair_kit`
- `blockforge_connector:settlement_seal`

## Permissions And Safety

- Blueprint Table uses the existing server GUI flow.
- Builder Anchor and Material Cache are server-side interactions.
- Build preflight still happens before materials are consumed or blocks are
  placed.
- Minecraft manual regression and dedicated server smoke testing remain
  pending.
