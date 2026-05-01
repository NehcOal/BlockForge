# House Quality

House quality is a heuristic report designed to catch basic buildability issues before a house becomes a Blueprint or BuildPlan.

## Scores

The report includes:

- total
- enclosure
- roof
- entrance
- windows
- interior
- buildability
- materials
- warnings
- suggestions

## Rules

The analyzer checks:

- foundation or floor exists
- at least one door exists
- windows exist when enabled
- roof exists when enabled
- hollow interiors are preferred
- multi-floor houses include stair/ladder planning
- dimensions stay within alpha limits
- survival-friendly mode avoids rare material blocks

## Limits

This is not an AI architecture judge. It does not evaluate beauty, biome fit, furniture quality, or real-world structural engineering. It is a deterministic safety and usability check for Minecraft-style house plans.
