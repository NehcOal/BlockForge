# AI Generation

Status: `v2.0.0-alpha.1` AI Generation Alpha.

BlockForge v2.0 adds an optional AI generation pipeline:

```text
Prompt
-> AI provider adapter
-> AI Structure Plan v1
-> Structure Plan validation
-> VoxelModel / Blueprint v2
-> Blueprint validation report
-> 3D preview
-> multi-format export
```

## Local Rule Generator

The Local Rule Generator remains the default fallback.

- Provider id: `local-rule`
- Runs fully in the browser/runtime.
- Does not send prompts to any server.
- Uses deterministic rules for tower, cottage, bridge, dungeon, and statue
  style requests.
- Produces a normal BlockForge `VoxelModel` and Blueprint v2 export.

## External AI Provider

The first external provider is OpenAI.

- Provider id: `openai`
- Requires a server runtime.
- Requires `OPENAI_API_KEY` configured on the server.
- The key is never read by browser client code.
- The Web UI only enables the external AI button after `/api/ai/status`
  reports that OpenAI is configured.
- `/api/ai/generate-blueprint` returns validated generation results and never
  returns the API key.

Environment:

```bash
OPENAI_API_KEY=...
OPENAI_MODEL=gpt-4.1-mini
```

`OPENAI_MODEL` is optional.

See [AI Live Testing](./AI_LIVE_TESTING.md) for the manual smoke checklist.

## AI Structure Plan v1

External AI does not return final blocks. It returns a high-level Structure Plan:

- `schemaVersion: 1`
- `name`
- `description`
- `intent`
- `size`
- `palette`
- `elements`
- `constraints`

Elements describe floors, walls, roofs, windows, doors, pillars, bridge decks,
arches, decorations, or custom bounding boxes. Coordinates must stay inside the
declared size, and every `blockKey` must exist in the palette.

Schema file:

- `schemas/blockforge-ai-structure-plan-v1.schema.json`

OpenAI Structured Outputs are used so the provider requests JSON that conforms
to this schema instead of free-form natural language.

## Validation Pipeline

Every AI result is validated before preview/export:

1. Prompt safety check.
2. Structure Plan schema and field validation.
3. Bounds, palette, and estimated block count validation.
4. Deterministic Structure Plan to `VoxelModel` conversion.
5. Existing Blueprint v2 validation report.

If validation fails, the result is shown as an error and is not loaded into the
3D preview.

## Privacy And Cost

- External AI requests may send your prompt to the selected AI provider.
- API usage may incur cost.
- API keys must be configured server-side.
- BlockForge does not persist cloud generation history.
- Local Rule Generator does not send prompts anywhere.

## Known Limitations

- AI generation is Alpha.
- Architectural quality is not guaranteed.
- Material-aware AI generation is limited.
- No multiplayer AI generation queue.
- No persistent cloud generation history.
- No external provider works without a server runtime and API key.
- External AI live test is pending until a real server-side key is configured
  and tested manually.
- Browser visual QA is pending.
- Minecraft manual regression is pending.

## Future Providers

The provider interface is intentionally small so later versions can add other
server-side providers without changing the Web preview/export pipeline.
