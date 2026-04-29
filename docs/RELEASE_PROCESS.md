# Release Process

## Branching Model

BlockForge uses one feature branch per major product train.

- Current recommended branch: `feature/v2.0-ai-generation`
- Do not create small feature branches such as `v2.0.1-web-polish`.
- Small features, polish, documentation updates, and focused tests are grouped
  as commits inside the active major-version branch.
- Draft PRs are used for major-version development and stay open while the
  train is active.

## Version Labels

- `alpha`: feature candidate. The feature exists, but visual QA, manual
  Minecraft regression, or release polish may still be pending.
- `rc`: regression candidate. Feature scope is frozen and validation is the
  main work.
- `stable`: official release. Use after automated checks, required manual
  checks, release notes, and assets are ready.
- `patch`: bugfix only for an already published stable release.

## Release Tags

GitHub Releases are based on tags.

Recommended tag format:

```text
v4.0.0-beta.1
v2.0.0-rc.1
v2.0.0
v2.0.1
```

Do not use patch tags for new features. Use patch tags only after a stable
release exists and the change is a bugfix for that stable line.

## Jar Assets

Upload the three connector jars for every public GitHub release:

- `blockforge-connector-neoforge-<version>.jar`
- `blockforge-connector-fabric-<version>.jar`
- `blockforge-connector-forge-<version>.jar`

Fabric may also produce a sources jar, but the runtime jar is the required
release asset.

Before uploading, verify the jar filenames match the release version and loader.

## Draft PRs

Use Draft PRs while a major-version train is still missing visual QA, Minecraft
manual regression, or live provider smoke testing.

Recommended PR title format:

```text
feat: add AI generation alpha
```

Recommended PR body sections:

- Summary
- Major changes
- Validation
- Pending QA
- Known limitations
- Release readiness

## Test Status Wording

Use explicit status words in README, release notes, and QA docs:

- `passed`: actually run and passed in this candidate.
- `pending`: required but not yet run.
- `needs manual check`: cannot be validated by automated checks yet.
- `not run`: intentionally skipped or unavailable in this candidate.

Do not write `passed` for Browser visual QA or Minecraft manual regression
unless that exact manual check was performed for the release candidate.
External AI live tests, dedicated server smoke tests, Modrinth releases, and
CurseForge releases must also stay `pending` or `not run` unless actually
performed.

## Required Automated Checks

Run before opening or updating a release PR:

```powershell
pnpm lint
pnpm test
pnpm build
```

For connector releases, also run:

```powershell
cd mod/neoforge-connector
gradlew.bat build
cd ..\fabric-connector
gradlew.bat build
cd ..\forge-connector
gradlew.bat build
```

## v2.0 Notes

`v2.0` is AI Generation Alpha. It is not stable until the train reaches a
stable tag. External AI must remain optional, server-side, and guarded by
validation before generated blueprints enter preview/export.

External AI live testing is documented in `docs/AI_LIVE_TESTING.md` and remains
pending until a real server-side API key is configured and tested manually.
# v3.0 Train Rule

- Active branch: `feature/v3.0-product-workbench`.
- Alpha means feature candidate.
- RC means regression candidate.
- Stable means formal release.
- Patch is only for bugfixes against an already published stable release.
- One major product train uses one feature branch.
- Do not create `v3.0.1` feature branches for small work.
- Draft PR is recommended until Browser visual QA, Minecraft manual regression,
  External AI live test, and dedicated server smoke test have evidence.
- GitHub Release is based on a tag.
- Release assets must include NeoForge, Fabric, and Forge jars when publishing
  connector builds.
- Pending/passed status must be honest.
