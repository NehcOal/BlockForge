# AI Live Testing

Status: External AI live test pending until manually run with a real server-side
API key.

Do not commit real API keys.

## Setup

1. Create `.env.local`.
2. Add a server-side key:

```bash
OPENAI_API_KEY=your_key_here
```

`OPENAI_MODEL` is optional.

## Start Dev Server

```powershell
pnpm dev
```

## Check Provider Status

Open:

```text
http://localhost:3000/api/ai/status
```

Expected with a key:

```json
{
  "openaiConfigured": true,
  "providers": ["local-rule", "openai"],
  "defaultProvider": "local-rule"
}
```

Expected without a key:

```json
{
  "openaiConfigured": false,
  "providers": ["local-rule", "openai"],
  "defaultProvider": "local-rule"
}
```

The response must never include the API key.

## Send Minimal Prompt

Use the Web UI or send a local POST:

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:3000/api/ai/generate-blueprint `
  -ContentType "application/json" `
  -Body '{"prompt":"small stone tower","provider":"openai","maxBlocks":1000}'
```

Expected:

- Response contains a generated result.
- `structurePlan.schemaVersion` is `1`.
- `blueprintV2.schemaVersion` is `2`.
- Validation report is valid.
- The Web preview updates only after validation passes.

## Confirm Client Key Safety

- Search built assets for the real key after `pnpm build`.
- Confirm `/api/ai/status` does not return the key.
- Confirm browser network responses do not include the key.

## Fallback Check

Remove `OPENAI_API_KEY`, restart the dev server, and confirm:

- External AI button is disabled.
- Local Rule Generator still works.
- Export still works for local results.

## Recording Results

Record status as:

- `passed`: only after the live key test actually succeeds.
- `pending`: not yet run.
- `failed`: run and failed, with command and error summary.

Never mark External AI live test as passed based on mocked tests.
# v3.0 Live Test Status

External AI live test: pending.

Manual checklist:

- Set `OPENAI_API_KEY` in `.env.local`.
- Confirm `.env.local` is ignored by git.
- Start `pnpm dev`.
- Visit `/api/ai/status` and confirm `openaiConfigured` is true.
- Send a minimal prompt through `/api/ai/generate-blueprint`.
- Confirm the browser never receives the API key.
- Confirm the result is an AI Structure Plan v1.
- Confirm Structure Plan validation runs before preview.
- Confirm validation failure does not enter preview/export.
- Confirm Local Rule Generator fallback still works with no API key.
- Record date, prompt, provider, result, warnings, and cost notes.
