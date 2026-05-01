# Project Chains

Project Chains turn contracts into longer settlement goals. Each project is a list of ordered stages, and each stage points at a required contract template.

## Built-In Chains

- Starter Settlement: Starter Cottage -> Storage Shed -> Farm Hut
- Defensive Outpost: Watchtower -> Wall Segment -> Gatehouse
- Market District: Market Stall -> Road Connection -> Fountain
- Mining Camp: Mine Entrance -> Storage Depot -> Watchtower

## Project Map

`blockforge_connector:project_map` shows project state through alpha command feedback:

- `/blockforge projects list`
- `/blockforge projects info <projectId>`
- `/blockforge projects activate <projectId>`
- `/blockforge projects status`
- `/blockforge projects complete <projectId>`: admin scaffold

## Progress Rules

- A project starts as `AVAILABLE`.
- Activating a project checks the current stage reputation requirement in common logic.
- Completing the matching contract advances the current stage.
- Completing all stages marks the project `COMPLETED`.

## Limits

Project GUI, multiplayer membership permissions, and persistent loader state remain alpha/partial.
