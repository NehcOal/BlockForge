# BlockForge v4.0.0-beta.1 Gameplay Beta QA Report

Date: 2026-04-29

## Summary

v4.0.0-beta.1 moves the gameplay line from Alpha scaffold toward a Beta
candidate by adding a pure Builder Station tick runtime, stronger diagnostics
fields, and beta testing documentation.

This report does not claim Minecraft manual regression or dedicated server
smoke testing passed.

## Automated Validation

Fill after final validation:

| Check | Result |
|---|---|
| `pnpm lint` | passed |
| `pnpm test` | passed: 59 files / 154 tests |
| `pnpm build` | passed |
| NeoForge `gradlew.bat build` | passed |
| Fabric `gradlew.bat build` | passed |
| Forge `gradlew.bat build` | passed |

## Runtime Status

| Area | Status |
|---|---|
| Builder Station common tick runtime | implemented and covered by Java tests |
| Loaded chunk / protection / material / quota / cooldown gates | pure runtime checks implemented |
| World block placement from station tick | partial, loader integration still guarded |
| Material Cache GUI | planned / partial |
| Builder Station GUI | planned / partial |
| Audit persistence | planned / partial |
| Diagnostics fields | implemented in Web/server diagnostics model |

## Manual QA

| Area | Status |
|---|---|
| NeoForge client regression | pending |
| Fabric client regression | pending |
| Forge client regression | pending |
| NeoForge dedicated server smoke | pending |
| Fabric dedicated server smoke | pending |
| Forge dedicated server smoke | pending |

## Release Recommendation

v4.0.0-beta.1 is suitable for a GitHub beta prerelease candidate from automated
validation. It should not be marked stable until manual Minecraft and dedicated
server QA are recorded.
