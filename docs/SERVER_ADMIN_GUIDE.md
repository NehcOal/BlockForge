# Server Admin Guide

Status: `v5.3.0-beta.1` Alpha documentation.

## v3.5 Gameplay Alpha Admin Notes

- Builder Station, Material Link, and Construction Core are Alpha blocks.
- Station commands are scaffolded first on NeoForge.
- Quota, cooldown, audit, and project membership have common data models and
  tests, but persistent enforcement is partial.
- Material Link does not load remote chunks and does not cross dimensions.
- Admin audit export and rollback are planned.
- Minecraft manual regression and dedicated server smoke testing remain pending.

## Recommended Settings

- Keep Blueprint Packs in `config/blockforge/packs/`.
- Keep loose blueprints in `config/blockforge/blueprints/`.
- Keep schematics in `config/blockforge/schematics/`.
- Keep protection regions explicit and small enough to audit.

## Diagnostics

Planned/admin-facing diagnostics should summarize:

- BlockForge version
- loader
- Minecraft version
- loaded loose blueprint count
- loaded pack count
- loaded schematic count
- protection status
- nearby material sourcing status
- permission mode
- warning count

## Safety Notes

- Nearby container sourcing is Alpha.
- Material Cache is registered in v3.1, but cache-backed inventory sourcing is
  still Alpha work-in-progress.
- Builder Anchor does not bypass protection checks; build preflight still owns
  the final allow/deny decision.
- Protection checks should run before material consumption.
- Manual Minecraft regression remains pending for this alpha candidate.
# v3.0 Admin Notes

Recommended diagnostic commands for manual regression:

- `/blockforge status`
- `/blockforge diagnostics`
- `/blockforge diagnostics export`
- `/blockforge litematics folder`
- `/blockforge litematics reload`
- `/blockforge litematics list`
- `/blockforge litematics info <id>`
- `/blockforge litematics validate`

Command behavior must be verified in real Minecraft before marking passed.
