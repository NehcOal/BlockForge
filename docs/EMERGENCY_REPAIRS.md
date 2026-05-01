# Emergency Repairs

Emergency Repairs connect settlement events to BuildPlan repair gameplay. The v5.1 alpha keeps this as server-safe state and verification logic; it does not destructively alter player builds.

## Emergency Beacon

`blockforge_connector:emergency_beacon` is the in-world entry point. Current commands:

- `/blockforge emergency list`
- `/blockforge emergency info <id>`
- `/blockforge emergency repair <id>`
- `/blockforge emergency verify <id>`

## Repair Request

An `EmergencyRepairRequest` records:

- settlement and event ids
- target blueprint id
- target dimension and base position
- missing/wrong block counts
- required completion percent
- expiry game time

`EmergencyRepairVerifier` checks repaired blocks, remaining issues, and timeout.

## Safety

- Emergency events do not randomly damage the world.
- Repair builds still must respect protection, quota, and material rules when loader integration performs placement.
- Dedicated server smoke test remains pending.
