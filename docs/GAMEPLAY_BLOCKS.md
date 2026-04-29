# Gameplay Blocks

Status: `3.2.0-alpha.1` Alpha. Minecraft manual regression is pending.

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
- v3.2.0-alpha.1 exposes the block and material source DTOs first.
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

## Permissions And Safety

- Blueprint Table uses the existing server GUI flow.
- Builder Anchor and Material Cache are server-side interactions.
- Build preflight still happens before materials are consumed or blocks are
  placed.
- Minecraft manual regression and dedicated server smoke testing remain
  pending.
