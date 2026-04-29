# BlockForge Material Network Alpha

Version: 4.1.0-beta.1

Material Network is the v3.5 alpha scaffold for connecting Builder Station jobs to explicit material sources.

v4.0 keeps the network server-safe: Material Link and Builder Station source
types are modeled, but remote chunk loading and cross-dimension links remain
blocked.

## Blocks

- Material Cache: local inventory source for survival builds.
- Material Link: lightweight node that points a station toward a Material Cache or nearby container.
- Builder Station: consumes linked material sources when station placement is implemented.
- Construction Core: project-level coordination scaffold.

## Source Priority

Default planned priority:

1. Player inventory
2. Material Cache
3. Nearby container
4. Material linked cache

The common material source model now recognizes:

- `MATERIAL_CACHE`
- `MATERIAL_LINK`
- `MATERIAL_LINKED_CACHE`
- `BUILDER_STATION`

## Current Alpha Limits

- No cross-dimension network.
- No remote chunk loading.
- Material Link is a metadata scaffold, not a pipe simulation.
- Inventory-backed Material Cache sourcing remains partial.
- Minecraft manual regression is pending.
- Dedicated server smoke test is pending.
