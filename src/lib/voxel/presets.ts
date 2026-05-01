import { blocksFromStore, setBlock } from "@/lib/voxel/utils";
import { createStructurePresetModel, structurePresets } from "@/lib/voxel/structurePresets";
import type { PresetId, VoxelBlock, VoxelModel, VoxelSize } from "@/types/blueprint";

type PresetModel = VoxelModel & { id: PresetId };
type PresetFactory = () => PresetModel;

function buildModel(
  id: PresetId,
  name: string,
  description: string,
  size: VoxelSize,
  build: (blocks: Map<string, VoxelBlock>) => void
): PresetModel {
  const store = new Map<string, VoxelBlock>();
  build(store);

  return {
    id,
    name,
    description,
    size,
    blocks: blocksFromStore(store)
  };
}

export function createMedievalTower(): PresetModel {
  return buildModel(
    "medieval-tower",
    "Medieval Tower",
    "A stone watchtower with glass windows, torchlight, and crenellated battlements.",
    { width: 11, height: 16, depth: 11 },
    (blocks) => {
      for (let y = 0; y <= 12; y += 1) {
        for (let x = 1; x <= 9; x += 1) {
          for (let z = 1; z <= 9; z += 1) {
            const isWall = x === 1 || x === 9 || z === 1 || z === 9;
            const isFloor = y === 0 || y === 6 || y === 12;

            if (isWall || isFloor) {
              setBlock(blocks, x, y, z, isFloor ? "cobblestone" : "stone_bricks");
            }
          }
        }
      }

      for (const y of [4, 8]) {
        setBlock(blocks, 5, y, 1, "glass");
        setBlock(blocks, 5, y, 9, "glass");
        setBlock(blocks, 1, y, 5, "glass");
        setBlock(blocks, 9, y, 5, "glass");
      }

      for (const [x, z] of [
        [4, 0],
        [6, 0],
        [4, 10],
        [6, 10]
      ]) {
        setBlock(blocks, x, 3, z, "torch");
      }

      for (let x = 0; x <= 10; x += 1) {
        for (let z = 0; z <= 10; z += 1) {
          const isEdge = x === 0 || x === 10 || z === 0 || z === 10;
          const isCornerOrGap =
            (x % 2 === 0 && (z === 0 || z === 10)) ||
            (z % 2 === 0 && (x === 0 || x === 10));

          if (isEdge && isCornerOrGap) {
            setBlock(blocks, x, 13, z, "stone_bricks");
            setBlock(blocks, x, 14, z, "stone_bricks");
          }
        }
      }

      for (let x = 2; x <= 8; x += 1) {
        for (let z = 2; z <= 8; z += 1) {
          setBlock(blocks, x, 13, z, "cobblestone");
        }
      }
    }
  );
}

export function createSmallCottage(): PresetModel {
  return buildModel(
    "small-cottage",
    "Small Cottage",
    "A cozy timber cottage with log corners, plank walls, windows, and a stone base.",
    { width: 12, height: 8, depth: 10 },
    (blocks) => {
      for (let x = 0; x < 12; x += 1) {
        for (let z = 0; z < 10; z += 1) {
          setBlock(blocks, x, 0, z, "stone_bricks");
        }
      }

      for (let y = 1; y <= 4; y += 1) {
        for (let x = 0; x < 12; x += 1) {
          for (let z = 0; z < 10; z += 1) {
            const isWall = x === 0 || x === 11 || z === 0 || z === 9;
            if (!isWall) {
              continue;
            }

            const isCorner = (x === 0 || x === 11) && (z === 0 || z === 9);
            setBlock(blocks, x, y, z, isCorner ? "oak_log" : "oak_planks");
          }
        }
      }

      for (const x of [5, 6]) {
        setBlock(blocks, x, 1, 0, "door");
        setBlock(blocks, x, 2, 0, "door");
      }

      for (const [x, z] of [
        [2, 0],
        [9, 0],
        [0, 4],
        [11, 4],
        [3, 9],
        [8, 9]
      ]) {
        setBlock(blocks, x, 2, z, "glass");
        setBlock(blocks, x, 3, z, "glass");
      }

      for (let y = 5; y <= 7; y += 1) {
        const inset = y - 5;
        for (let x = inset; x < 12 - inset; x += 1) {
          for (let z = 0; z < 10; z += 1) {
            if (z === inset || z === 9 - inset) {
              setBlock(blocks, x, y, z, "oak_planks");
            }
          }
        }
      }

      for (let x = 1; x < 11; x += 1) {
        setBlock(blocks, x, 5, 4, "oak_log");
        setBlock(blocks, x, 5, 5, "oak_log");
      }
    }
  );
}

export function createDungeonEntrance(): PresetModel {
  return buildModel(
    "dungeon-entrance",
    "Dungeon Entrance",
    "A reinforced stone doorway with an arched entrance and torch-lit side walls.",
    { width: 13, height: 9, depth: 8 },
    (blocks) => {
      for (let x = 0; x < 13; x += 1) {
        for (let z = 0; z < 8; z += 1) {
          setBlock(blocks, x, 0, z, "stone");
        }
      }

      for (let y = 1; y <= 6; y += 1) {
        for (let z = 0; z < 8; z += 1) {
          setBlock(blocks, 0, y, z, "cobblestone");
          setBlock(blocks, 12, y, z, "cobblestone");
        }

        for (let x = 1; x < 12; x += 1) {
          setBlock(blocks, x, y, 7, "stone_bricks");
        }
      }

      for (let y = 1; y <= 6; y += 1) {
        for (let x = 2; x <= 10; x += 1) {
          const inDoorGap = x >= 5 && x <= 7 && y <= 4;
          const archGap = x === 6 && y === 5;

          if (!inDoorGap && !archGap) {
            setBlock(blocks, x, y, 0, "stone_bricks");
          }
        }
      }

      for (const x of [4, 8]) {
        for (let y = 1; y <= 5; y += 1) {
          setBlock(blocks, x, y, 1, "cobblestone");
        }
      }

      for (const [x, z] of [
        [3, 1],
        [9, 1],
        [1, 4],
        [11, 4]
      ]) {
        setBlock(blocks, x, 3, z, "torch");
      }

      for (let x = 2; x <= 10; x += 1) {
        setBlock(blocks, x, 7, 0, "cobblestone");
        setBlock(blocks, x, 7, 1, "cobblestone");
      }
    }
  );
}

export function createStoneBridge(): PresetModel {
  return buildModel(
    "stone-bridge",
    "Stone Bridge",
    "A low stone bridge crossing a water channel with cobbled supports and rails.",
    { width: 15, height: 6, depth: 7 },
    (blocks) => {
      for (let x = 0; x < 15; x += 1) {
        for (let z = 0; z < 7; z += 1) {
          setBlock(blocks, x, 0, z, "water");
        }
      }

      for (let x = 0; x < 15; x += 1) {
        for (let z = 2; z <= 4; z += 1) {
          setBlock(blocks, x, 2, z, "stone_bricks");
        }

        setBlock(blocks, x, 3, 1, "cobblestone");
        setBlock(blocks, x, 3, 5, "cobblestone");
      }

      for (const x of [2, 3, 4, 10, 11, 12]) {
        for (const z of [2, 3, 4]) {
          setBlock(blocks, x, 1, z, "cobblestone");
        }
      }

      for (const x of [0, 1, 13, 14]) {
        for (const z of [2, 3, 4]) {
          setBlock(blocks, x, 1, z, "stone_bricks");
        }
      }

      for (let z = 1; z <= 5; z += 1) {
        setBlock(blocks, 0, 4, z, "cobblestone");
        setBlock(blocks, 14, 4, z, "cobblestone");
      }
    }
  );
}

export function createPixelStatue(): PresetModel {
  return buildModel(
    "pixel-statue",
    "Pixel Statue",
    "A blocky pixel-art statue with a stone base, blue legs, red body, and gold crown.",
    { width: 9, height: 14, depth: 5 },
    (blocks) => {
      for (let y = 0; y <= 1; y += 1) {
        for (let x = 1; x <= 7; x += 1) {
          for (let z = 1; z <= 3; z += 1) {
            setBlock(blocks, x, y, z, "stone");
          }
        }
      }

      for (let y = 2; y <= 5; y += 1) {
        for (const x of [3, 5]) {
          for (let z = 1; z <= 3; z += 1) {
            setBlock(blocks, x, y, z, "wool_blue");
          }
        }
      }

      for (let y = 6; y <= 9; y += 1) {
        for (let x = 2; x <= 6; x += 1) {
          for (let z = 1; z <= 3; z += 1) {
            setBlock(blocks, x, y, z, x === 4 ? "wool_white" : "wool_red");
          }
        }

        for (const x of [1, 7]) {
          for (let z = 1; z <= 3; z += 1) {
            setBlock(blocks, x, y, z, "wool_red");
          }
        }
      }

      for (let y = 10; y <= 12; y += 1) {
        for (let x = 3; x <= 5; x += 1) {
          for (let z = 1; z <= 3; z += 1) {
            setBlock(blocks, x, y, z, "wool_white");
          }
        }
      }

      for (let x = 2; x <= 6; x += 1) {
        for (let z = 1; z <= 3; z += 1) {
          setBlock(blocks, x, 13, z, "gold_block");
        }
      }
    }
  );
}

const legacyPresetFactories = {
  "medieval-tower": createMedievalTower,
  "small-cottage": createSmallCottage,
  "dungeon-entrance": createDungeonEntrance,
  "stone-bridge": createStoneBridge,
  "pixel-statue": createPixelStatue
};

const structurePresetFactories = Object.fromEntries(
  structurePresets.map((presetDefinition) => [
    presetDefinition.id,
    () => createStructurePresetModel(presetDefinition)
  ])
) as Record<(typeof structurePresets)[number]["id"], PresetFactory>;

export const presetFactories: Record<PresetId, PresetFactory> = {
  ...legacyPresetFactories,
  ...structurePresetFactories
};
