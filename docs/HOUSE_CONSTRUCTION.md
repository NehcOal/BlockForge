# House Construction Core

BlockForge v5.2 adds a rule-based HousePlan layer focused on building houses, not quests or settlement events.

## HousePlan

`HousePlan` is a pure data model used by both gameplay commands and the Web House Designer. It stores:

- style, footprint, dimensions, roof, materials, openings, rooms, modules, and issues
- construction options such as foundation, roof, windows, porch, chimney, stairs, hollow interior, and survival-friendly material checks
- module-level stages such as foundation, floor, walls, roof, doors/windows, stairs, porch, chimney, and trim

The common Java model does not reference Minecraft world classes and does not mutate the world directly.

## Presets

Alpha presets:

- Starter Cottage
- Medieval House
- Farmhouse
- Workshop
- Storage House
- Watchtower House
- Market House
- Longhouse

Each preset can generate a valid HousePlan, estimate materials, score quality, and compile into a Blueprint v2-compatible common blueprint.

## Build Flow

Recommended alpha flow:

1. Create a plan with `/blockforge house create <preset>`.
2. Inspect quality with `/blockforge house quality`.
3. Compile a preview summary with `/blockforge house preview`.
4. Create a BuildPlan with `/blockforge house buildplan`.
5. Use existing BuildPlan / Builder Wand / Station paths for placement.

House stages are represented by modules now. The existing BuildPlan still executes by y-layer; explicit house-stage execution is planned.

## Limits

- Max width: 32
- Max depth: 32
- Max floors: 4
- Max alpha target blocks: 5000
- Quality scoring is heuristic, not aesthetic AI.
- Minecraft manual regression and dedicated server smoke test remain pending.
