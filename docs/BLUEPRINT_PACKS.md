# BlockForge Blueprint Packs

Blueprint Packs bundle multiple BlockForge Blueprint JSON files into one
shareable zip file:

```text
*.blockforgepack.zip
```

v1.4.0 adds Blueprint Pack import/export Alpha support to the Web app and pack
loading Alpha support to NeoForge, Fabric, and Forge.

## ZIP Structure

Required:

```text
blockforge-pack.json
blueprints/
  tiny_platform.blueprint.json
  small_house.blueprint.json
```

Optional:

```text
screenshots/
  cover.png
README.md
LICENSE.txt
```

The connectors read pack zip entries directly. They do not extract pack files
to disk.

## Manifest

`blockforge-pack.json` uses schema version `1`:

```json
{
  "schemaVersion": 1,
  "packId": "starter_buildings",
  "name": "Starter Buildings",
  "version": "1.0.0",
  "description": "A small pack of starter Minecraft voxel buildings.",
  "author": "BlockForge",
  "license": "MIT",
  "minecraftVersion": "1.21.1",
  "blockforgeVersion": "1.4.0-alpha.1",
  "tags": ["starter", "survival", "medieval"],
  "blueprints": [
    {
      "id": "tiny_platform",
      "name": "Tiny Platform",
      "path": "blueprints/tiny_platform.blueprint.json",
      "description": "A minimal 3x3 platform.",
      "tags": ["test", "starter"],
      "previewImage": "screenshots/tiny_platform.png"
    }
  ]
}
```

Rules:

- `schemaVersion` must be `1`.
- `packId` and blueprint `id` must use lowercase letters, numbers,
  underscores, or hyphens.
- `blueprints` must contain at least one entry and at most 256 entries.
- Blueprint paths must be relative paths inside `blueprints/`.
- Paths must not contain `../`, backslash traversal, absolute paths, or drive
  letters.
- Manifest blueprint ids must be unique.

The JSON schema lives at:

```text
schemas/blockforge-pack-v1.schema.json
```

## Registry IDs

Loose blueprint ids stay unchanged:

```text
tiny_platform
```

Pack blueprint ids are namespaced with the pack id:

```text
starter_buildings/tiny_platform
```

Loose blueprints have priority. If a pack blueprint conflicts with an already
loaded loose id, the pack blueprint is skipped and a warning is reported.

## Web Import / Export

The Web app can export the current model as a `.blockforgepack.zip` containing
Blueprint JSON v2. It can also import a pack zip, validate the manifest and
blueprint JSON files, reject unsafe paths, and display an import summary.

v1.4.0 import does not yet persist imported pack contents into a long-term
library. Library management is planned after the pack protocol settles.

## Mod Pack Directory

All three connectors scan:

```text
.minecraft/config/blockforge/packs/
```

Supported files:

```text
*.blockforgepack.zip
*.zip
```

`/blockforge reload` scans both loose blueprints and packs. Pack blueprints then
flow through the existing registry, GUI Selector, Builder Wand, Ghost Preview,
materials, build, and undo systems.

## Pack Commands

NeoForge, Fabric, and Forge expose:

```mcfunction
/blockforge packs folder
/blockforge packs reload
/blockforge packs list
/blockforge packs info <packId>
/blockforge packs blueprints <packId>
/blockforge packs validate
```

`packs reload` requires permission level `2`. The other pack commands are
available to regular players.

Existing commands accept pack blueprint ids:

```mcfunction
/blockforge info starter_buildings/tiny_platform
/blockforge dryrun starter_buildings/tiny_platform
/blockforge select starter_buildings/tiny_platform
/blockforge materials starter_buildings/tiny_platform
/blockforge build starter_buildings/tiny_platform
```

## Security Restrictions

- Reject path traversal and absolute paths.
- Reject blueprint paths outside `blueprints/`.
- Do not extract pack files to disk.
- Limit packs to 256 blueprints.
- Pack failures must not stop loose blueprint loading.
- Zip size limits are documented as a future hardening step.

## Current Limits

- No online blueprint marketplace.
- No remote pack download or server-client pack sync.
- No encrypted packs.
- No rating, comments, or permissions layer.
- Web import shows a summary only; persistent imported-pack library management
  is planned later.
- Manual Minecraft regression testing for v1.4.0 is pending.
