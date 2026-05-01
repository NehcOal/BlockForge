# Blueprint Gallery

The Blueprint Gallery is a local-first workspace for browsing, tagging,
favoriting, rating, exporting, and re-importing BlockForge blueprints.

## Storage

- Gallery metadata and blueprint data are intended for local browser storage.
- IndexedDB is used by the storage layer; no cloud sync is performed.
- Preview thumbnails can be saved from Preview PNG export when available.
- Missing thumbnails use a generated placeholder in the UI.

## Gallery Item

Each item stores:

- id, name, description
- source: generated, imported Blueprint, `.schem`, `.litematic`, pack, preset,
  or workspace
- Blueprint v2 payload
- tags, favorite, rating
- block count, palette count, dimensions
- created and updated timestamps

## Search And Filters

- Search by id, name, description, source, and tags.
- Filter by source, favorite state, and tags.
- Sort by updated date, name, block count, or rating.

## Gallery Bundle

Alpha export uses `.blockforgegallery.zip` as the user-facing extension. The
documented bundle layout is:

```text
blockforge-gallery.json
blueprints/
thumbnails/
README.md
```

The current pure logic validates imported bundle ids against path traversal,
skips duplicate ids with warnings, and validates every Blueprint v2 before it
can be loaded into preview or export.

## Limitations

- No cloud sync.
- No marketplace.
- No account system.
- Bundle import/export is alpha and should be manually checked before public
  sharing.
