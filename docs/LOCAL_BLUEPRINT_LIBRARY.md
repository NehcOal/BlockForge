# Local Blueprint Library

Status: `v3.0.0-alpha.1` Alpha.

The Local Blueprint Library stores generated, imported, schematic, pack, and
preset blueprints in browser-local storage. It does not upload data.

## Storage

- IndexedDB is the intended structured storage layer.
- localStorage should only hold lightweight UI preferences.
- The current schema includes stores for history, library, and workspaces.

## Library Items

Items include:

- id
- name
- description
- source
- Blueprint v2 payload
- tags
- favorite flag
- timestamps

## Generation History

AI candidates can be saved to generation history, searched, loaded into preview,
deleted, and exported.

## Workspace

Workspace files reference library/history ids and UI state. Export format:

```text
.blockforgeworkspace.json
```

## Limitations

- Local data stays in the browser profile.
- No cloud sync.
- Browser visual QA is pending.
