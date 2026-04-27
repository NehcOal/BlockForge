# GUI Search And Filters

`v1.8.0-alpha.1` adds query-based in-game Blueprint Selector controls for NeoForge, Fabric, and Forge.

## Controls

| Control | Behavior |
|---|---|
| Search | Case-insensitive contains search over blueprint id, name, source id, pack id, and tags. Whitespace is trimmed. Chinese text works with normal `contains` matching. |
| Source filter | Cycles through All, Loose, Pack, and Schematic. |
| Warning filter | Cycles through All, With warnings, and Without warnings. Warning badges are best-effort and default to zero where a loader has no warning tracking. |
| Sort | Cycles through Name A-Z, Name Z-A, Blocks low-high, Blocks high-low, Source A-Z, and Source Z-A. |
| Pagination | Uses server-side pages with page size 8. UI displays `Page X / Y` and total blueprint count. |

## Server Query Model

The client sends `BlueprintListRequestPayload` with:

- `searchText`
- `sourceFilter`
- `warningFilter`
- `sortMode`
- `page`
- `pageSize`

The server builds `BlueprintSummary` values from the authoritative registry, applies `BlueprintGuiQueryService`, and returns only the current page in `BlueprintListPayload`. Selection still uses a separate server-validated request, so the client cannot select an unavailable registry id by editing UI state.

## Source Tags

Blueprint rows show a source tag:

- `loose` for files loaded directly from `config/blockforge/blueprints/`
- `pack` for ids that include a pack prefix
- `schematic` for schematic-derived ids

The details panel also shows source id when available.

## Warning Badges

Rows append `!` when `warningCount > 0`. The details panel displays `warnings=N`. Current loader warning tracking is best-effort; missing tracking reports zero warnings rather than blocking GUI usage.

## Current Limitations

- No fuzzy search or pinyin search.
- `/blockforge gui search <query>` is not implemented yet.
- `/blockforge list --source <all|loose|pack|schem>` is not implemented yet.
- Protection preflight still runs at build time on the server.
- Minecraft manual regression is pending.
