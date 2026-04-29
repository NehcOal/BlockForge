# BlockForge v4.2.0-beta.1 Gameplay Beta QA Report

Date: 2026-04-29

## Summary

v4.2.0-beta.1 continues the gameplay beta line by adding common GUI state/action
models, audit JSONL formatting, admin rollback decisions, and cooldown policy
helpers. Loader-specific screens and real server world integration remain
partial.

Additional v4.2 automated coverage:

- Material Cache pure inventory insert/extract/drop behavior.
- Station material resolver cache/owner-inventory server rules.
- Audit JSONL file writing.
- Diagnostics JSON export shape.

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
| Material Cache GUI | common menu state implemented; loader screens partial |
| Builder Station GUI | common status/action model implemented; loader screens partial |
| Audit persistence | common JSONL file writer implemented; loader command wiring needs smoke test |
| Admin rollback | common decision logic implemented; world integration partial |
| Diagnostics fields | common JSON export model implemented; loader command wiring needs smoke test |

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

v4.2.0-beta.1 is suitable for a GitHub beta prerelease candidate from automated
validation. It should not be marked stable until manual Minecraft and dedicated
server QA are recorded.
