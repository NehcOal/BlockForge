# BlockForge Team Builds

Version: 3.5.0-alpha.1

Team Builds are a v3.5 project scaffold for shared multiplayer construction.

## Data Model

`BuildProject` tracks:

- project id
- owner
- members
- Builder Stations
- Material Caches
- Builder Anchors
- project status

## Planned Commands

- `/blockforge project create <name>`
- `/blockforge project info`
- `/blockforge project bind station`
- `/blockforge project invite <player>`

Invite and membership persistence are planned. The v3.5 alpha only includes pure common data and docs.

## Current Alpha Limits

- No cloud sync.
- No multiplayer project UI.
- No persistent project store.
- Server operator policy is documented but not enforced across all loaders yet.

