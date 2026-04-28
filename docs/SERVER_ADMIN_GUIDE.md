# Server Admin Guide

Status: `v2.5.0-alpha.1` Alpha documentation.

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
- Protection checks should run before material consumption.
- Manual Minecraft regression remains pending for this alpha candidate.
