import type { BlockType } from "@/types/blueprint";

export type AiStructurePlanIntent =
  | "tower"
  | "cottage"
  | "bridge"
  | "dungeon"
  | "statue"
  | "custom";

export type AiStructureElementType =
  | "floor"
  | "wall"
  | "roof"
  | "window"
  | "door"
  | "pillar"
  | "bridge_deck"
  | "arch"
  | "decoration"
  | "custom";

export type AiStructurePlanPaletteEntry = {
  name: string;
  block: BlockType;
  minecraftBlockId: string;
  properties?: Record<string, string>;
};

export type AiStructureElement = {
  id: string;
  type: AiStructureElementType;
  blockKey: string;
  from: [number, number, number];
  to: [number, number, number];
  hollow?: boolean;
  notes?: string;
};

export type AiStructurePlanV1 = {
  schemaVersion: 1;
  name: string;
  description: string;
  intent: AiStructurePlanIntent;
  size: {
    width: number;
    height: number;
    depth: number;
  };
  palette: Record<string, AiStructurePlanPaletteEntry>;
  elements: AiStructureElement[];
  constraints: {
    maxBlocks: number;
    allowUnsupportedBlocks: false;
  };
};

export type AiStructurePlan = AiStructurePlanV1;
