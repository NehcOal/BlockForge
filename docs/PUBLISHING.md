# BlockForge Publishing Guide

## Scope

This guide prepares the `1.5.0-alpha.1` multi-loader Alpha release. It does not
publish automatically to GitHub, Modrinth, or CurseForge.

NeoForge is the recommended full-experience Connector. Fabric and Forge are
Alpha connectors with command builds, GUI Selector Alpha, Builder Wand Alpha
placement, Ghost Preview Alpha outlines, Survival Material Cost Alpha, and
Material Refund Undo Alpha. v1.3.1 adds NeoForge nearby container material
sourcing Alpha; v1.3.5 extends nearby container sourcing to Fabric and Forge.
v1.4.0 adds Blueprint Pack import/export and connector-side pack loading as an
Alpha regression candidate.

## Build Before Publishing

Run the Web checks:

```powershell
pnpm lint
pnpm test
pnpm build
```

Run the loader builds:

```powershell
cd mod/neoforge-connector
gradlew.bat build
cd ..\fabric-connector
gradlew.bat build
cd ..\forge-connector
gradlew.bat build
```

Expected jars:

```text
mod/neoforge-connector/build/libs/blockforge-connector-neoforge-1.5.0-alpha.1.jar
mod/fabric-connector/build/libs/blockforge-connector-fabric-1.5.0-alpha.1.jar
mod/forge-connector/build/libs/blockforge-connector-forge-1.5.0-alpha.1.jar
```

## GitHub Alpha Release

1. Create a GitHub release for `v1.5.0-alpha.1`.
2. Use `docs/RELEASE_NOTES_TEMPLATE.md` as the release body starting point.
3. Upload the three loader jars.
4. Confirm every jar file name includes the loader name.
5. Explain loader differences in the release notes.
6. Recommend NeoForge for the most complete experience.
7. Label Fabric and Forge clearly as GUI Selector + Builder Wand + Ghost
   Preview + Survival Material Cost + Material Refund Undo Alpha.
8. Mention that nearby chest material sourcing is Alpha on all loaders.
9. Mention that Blueprint Pack import/export is Alpha and uses
   `.blockforgepack.zip`.
10. Do not claim Fabric or Forge BlockEntity NBT undo, collision-aware preview,
   full block preview support, protected-container permission checks, or GUI
   material source details.

## Modrinth Preparation

If publishing to Modrinth:

- A non-draft Modrinth version must include at least one file.
- Use game version `1.21.1`.
- Mark each file with its correct loader:
  - NeoForge jar: `neoforge`
  - Fabric jar: `fabric`
  - Forge jar: `forge`
- Mark Fabric API as required for the Fabric file.
- Use clear file display names that include the loader and version.
- State clearly that Fabric and Forge are Alpha parity connectors.
- State clearly that nearby chest material sourcing is Alpha on all three
  loaders, disabled by default, and pending manual Minecraft regression.
- State clearly that Blueprint Pack loading is Alpha and pack zips are read
  from `config/blockforge/packs/`.
- NeoForge, Fabric, and Forge can be published as separate files under one
  project version if the metadata stays clear, or as separate version entries if
  that is easier to maintain.
- It is also acceptable to publish only the GitHub Alpha first and delay
  Modrinth until Fabric and Forge parity improves.

## CurseForge Preparation

If publishing to CurseForge:

- Upload each jar as a separate project file.
- Set the correct loader metadata for each file.
- Set Minecraft version `1.21.1`.
- Mark Fabric API as a dependency for the Fabric file.
- Keep release notes explicit about current loader differences.
- New projects and new files may require moderation before appearing publicly.
- Use Alpha files for early testing; prefer Beta or Release only after broader
  in-game regression passes.
- Do not market nearby chest sourcing as stable until v1.3.5 in-game
  validation is complete.
- Do not market Blueprint Pack support as stable until v1.4.0 in-game
  validation is complete.
- Do not bundle all three loader jars into one zip for normal mod-manager
  installs.

## Current Loader Messaging

Use this wording consistently:

- NeoForge: recommended complete Connector.
- Fabric Alpha: Connector for examples, reload, list, info, dryrun, build,
  GUI selection, rotation, Builder Wand placement, Ghost Preview outline,
  survival material cost, block undo, survival material refund undo, and
  nearby container material sourcing Alpha, and Blueprint Pack loading Alpha.
- Forge Alpha: Connector for examples, reload, list, info, dryrun, build,
  GUI selection, rotation, Builder Wand placement, Ghost Preview outline,
  survival material cost, block undo, survival material refund undo, and
  nearby container material sourcing Alpha, and Blueprint Pack loading Alpha.

## CI Artifacts

GitHub Actions uploads:

- `blockforge-neoforge-jar`
- `blockforge-fabric-jar`
- `blockforge-forge-jar`

These artifacts are validation outputs. Before public release, verify the final
download file names match the expected jar names above.
## v1.5.0 Security Publishing Notes

- Release notes must mention that permission/protection support is Alpha.
- External LuckPerms, Fabric Permissions API, FTB Chunks, and Open Parties and
  Claims integrations are planned, not bundled.
- Manual Minecraft regression testing must remain pending until explicitly
  tested.
