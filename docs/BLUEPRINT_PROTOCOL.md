# BlockForge Blueprint Protocol

## What Is BlockForge Blueprint?

BlockForge Blueprint is a stable JSON format shared by the BlockForge web app
and the Minecraft Mod Connector.

The web app can keep evolving its internal `VoxelModel`, while the Mod Connector
reads a predictable protocol file.

## Why Not Read `VoxelModel` Directly?

`VoxelModel` is an internal app data shape. It is useful for rendering, presets,
tests, and exporters, but it can change as the web app grows.

Blueprint is the compatibility layer:

- Stable field names.
- Fixed schema version.
- Explicit Minecraft version target.
- Explicit Minecraft block id palette.
- Coordinates that are easy for a mod to place in-game.

## Type Shape

### Blueprint v1

```ts
export type BlockForgeBlueprintV1 = {
  schemaVersion: 1;
  id: string;
  name: string;
  description: string;
  minecraftVersion: "1.21.1";
  generator: "BlockForge";
  size: {
    width: number;
    height: number;
    depth: number;
  };
  origin: {
    x: number;
    y: number;
    z: number;
  };
  palette: Record<string, string>;
  blocks: Array<{
    x: number;
    y: number;
    z: number;
    block: string;
  }>;
};
```

### Blueprint v2

```ts
export type BlockForgeBlockStateV2 = {
  name: string;
  properties?: Record<string, string>;
};

export type BlockForgeBlueprintV2 = {
  schemaVersion: 2;
  id: string;
  name: string;
  description: string;
  minecraftVersion: "1.21.1";
  generator: "BlockForge";
  size: {
    width: number;
    height: number;
    depth: number;
  };
  origin: {
    x: number;
    y: number;
    z: number;
  };
  palette: Record<string, BlockForgeBlockStateV2>;
  blocks: Array<{
    x: number;
    y: number;
    z: number;
    state: string;
  }>;
};
```

## Field Reference

- `schemaVersion`: Protocol version. Currently `1` or `2`.
- `id`: Minecraft-safe resource id, for example `medieval_tower`.
- `name`: Human-readable model name.
- `description`: Human-readable model description.
- `minecraftVersion`: Target Minecraft Java version. Currently fixed to `1.21.1`.
- `generator`: Source application. Currently fixed to `BlockForge`.
- `size`: Blueprint bounds in voxel blocks.
- `origin`: Placement origin. Currently `{ "x": 0, "y": 0, "z": 0 }`.
- `palette`: v1 maps keys to Minecraft Java block ids. v2 maps keys to `{ name, properties }`.
- `blocks`: Raw voxel block coordinates and palette keys. v1 uses `block`; v2 uses `state`.

## Example JSON

```json
{
  "schemaVersion": 1,
  "id": "medieval_tower",
  "name": "Medieval Tower",
  "description": "A stone watchtower with glass windows, torchlight, and crenellated battlements.",
  "minecraftVersion": "1.21.1",
  "generator": "BlockForge",
  "size": {
    "width": 11,
    "height": 16,
    "depth": 11
  },
  "origin": {
    "x": 0,
    "y": 0,
    "z": 0
  },
  "palette": {
    "stone_bricks": "minecraft:stone_bricks",
    "glass": "minecraft:glass",
    "torch": "minecraft:torch"
  },
  "blocks": [
    {
      "x": 0,
      "y": 0,
      "z": 0,
      "block": "stone_bricks"
    }
  ]
}
```

## Coordinate Rules

- Coordinates are zero-based.
- `x` ranges from `0` to `size.width - 1`.
- `y` ranges from `0` to `size.height - 1`.
- `z` ranges from `0` to `size.depth - 1`.
- Coordinates are not centered for rendering.
- `origin` is reserved for future placement offsets and defaults to zero.

## Palette Rules

The `palette` object maps BlockForge block names to Minecraft Java block ids.

Example:

```json
{
  "oak_planks": "minecraft:oak_planks",
  "glass": "minecraft:glass"
}
```

Each `blocks[].block` value refers to a key in `palette`.

In v2, each `blocks[].state` value refers to a key in `palette`.

Example v2 palette:

```json
{
  "door_lower": {
    "name": "minecraft:oak_door",
    "properties": {
      "facing": "south",
      "half": "lower",
      "hinge": "left",
      "open": "false"
    }
  }
}
```

## Minecraft Block IDs

Blueprint v1 uses Java Edition block ids such as:

- `minecraft:stone_bricks`
- `minecraft:oak_planks`
- `minecraft:glass`
- `minecraft:gold_block`

Block states such as orientation and door half are supported by v2 through
string properties. The Connector currently applies simple properties and skips
invalid values without crashing.

## Rotation Rules

The NeoForge Connector supports basic rotation parameters:

- `0`
- `90`
- `180`
- `270`

Rotation affects x/z coordinates around the blueprint origin and horizontal
`facing` values: `north`, `east`, `south`, and `west`.

It does not currently rotate `axis`, `shape`, connection properties, or other
complex block states.

## Mod Connector

The BlockForge Connector reads this format instead of the web app's internal
`VoxelModel`. That gives the mod a stable contract while the web UI, generator,
presets, and exporters continue to evolve.

The NeoForge connector looks for exported files in:

```text
.minecraft/config/blockforge/blueprints/
```

It can then place blueprints with:

```mcfunction
/blockforge build <id>
```

For manual testing, the repository includes example Blueprint v1 files in:

```text
examples/blueprints/
```

The connector also ships these examples inside the mod jar. Install them in-game
with:

```mcfunction
/blockforge examples install
/blockforge reload
```

The JSON Schema is available at:

```text
schemas/blockforge-blueprint-v1.schema.json
schemas/blockforge-blueprint-v2.schema.json
```
