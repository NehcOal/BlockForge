# House Designer

The Web House Designer is a deterministic, rule-based panel. It does not call AI and does not require network services.

## Web Controls

- house preset
- width
- depth
- floors
- roof type
- toggles for roof, windows, porch, chimney, and hollow interior

The generated house becomes the current `VoxelModel`, so existing preview and export controls continue to work.

## In-Game Commands

NeoForge reference commands:

- `/blockforge house presets`
- `/blockforge house create <preset>`
- `/blockforge house create <preset> size <width> <depth> floors <floors>`
- `/blockforge house roof <flat|gable|hip|pyramid|tower|shed|none>`
- `/blockforge house materials <wall> <roof> <floor>`
- `/blockforge house preview`
- `/blockforge house buildplan`
- `/blockforge house build`
- `/blockforge house quality`
- `/blockforge house export blueprint`
- `/blockforge house save <name>`

Fabric and Forge register the same command surface with alpha/partial messages where loader-side preview persistence is not complete.

## Current GUI Status

No new House Planner Table block is added in v5.2. The Web panel is complete enough for alpha preview/export, while in-game usage currently routes through commands and existing Blueprint Table / Builder Wand workflows.
