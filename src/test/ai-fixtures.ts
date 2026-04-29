import { minecraftBlockIds } from "@/lib/voxel";
import type { AiStructurePlan } from "@/lib/ai";

export function createValidStructurePlan(): AiStructurePlan {
  return {
    schemaVersion: 1,
    name: "AI Test Tower",
    description: "A compact validated tower structure plan.",
    intent: "tower",
    size: { width: 9, height: 12, depth: 9 },
    palette: {
      wall: {
        name: "Stone Bricks",
        block: "stone_bricks",
        minecraftBlockId: minecraftBlockIds.stone_bricks
      },
      glass: {
        name: "Glass",
        block: "glass",
        minecraftBlockId: minecraftBlockIds.glass
      },
      floor: {
        name: "Cobblestone",
        block: "cobblestone",
        minecraftBlockId: minecraftBlockIds.cobblestone
      }
    },
    elements: [
      {
        id: "base-floor",
        type: "floor",
        blockKey: "floor",
        from: [1, 0, 1],
        to: [7, 0, 7]
      },
      {
        id: "tower-shell",
        type: "wall",
        blockKey: "wall",
        from: [1, 1, 1],
        to: [7, 9, 7]
      },
      {
        id: "front-window",
        type: "window",
        blockKey: "glass",
        from: [4, 4, 1],
        to: [4, 5, 1]
      }
    ],
    constraints: {
      maxBlocks: 1000,
      allowUnsupportedBlocks: false
    }
  };
}
