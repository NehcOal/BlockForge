import { blocksFromStore, createSafeResourcePath, setBlock, validateVoxelModel } from "@/lib/voxel";
import type { AiStructureElement, AiStructurePlan } from "@/lib/ai/structurePlan";
import type { BlockType, VoxelBlock, VoxelModel } from "@/types/blueprint";

export type StructurePlanToVoxelResult = {
  model: VoxelModel;
  warnings: string[];
};

export function structurePlanToVoxel(
  plan: AiStructurePlan
): StructurePlanToVoxelResult {
  const store = new Map<string, VoxelBlock>();
  const warnings: string[] = [];

  for (const element of plan.elements) {
    const paletteEntry = plan.palette[element.blockKey];
    if (!paletteEntry) {
      warnings.push(`Skipped ${element.id}: missing palette key ${element.blockKey}.`);
      continue;
    }
    drawElement(store, element, paletteEntry.block, warnings);
  }

  const model: VoxelModel = {
    id: createSafeResourcePath(`ai-${plan.name}`),
    name: plan.name,
    description: plan.description,
    size: plan.size,
    blocks: blocksFromStore(store)
  };

  if (!validateVoxelModel(model)) {
    warnings.push("Generated VoxelModel failed BlockForge validation.");
  }

  return { model, warnings };
}

function drawElement(
  store: Map<string, VoxelBlock>,
  element: AiStructureElement,
  block: BlockType,
  warnings: string[]
): void {
  switch (element.type) {
    case "floor":
    case "bridge_deck":
      fillPlane(store, element.from, element.to, block, "y");
      return;
    case "wall":
      fillShell(store, element.from, element.to, block);
      return;
    case "roof":
      fillRoof(store, element.from, element.to, block);
      return;
    case "window":
      fillBox(store, element.from, element.to, "glass");
      return;
    case "door":
      fillBox(store, element.from, element.to, block === "door" ? "door" : "oak_planks");
      return;
    case "pillar":
    case "decoration":
      fillBox(store, element.from, element.to, block, element.hollow === true);
      return;
    case "arch":
      fillArch(store, element.from, element.to, block);
      return;
    case "custom":
      warnings.push(`Rendered custom element ${element.id} as a bounding box.`);
      fillBox(store, element.from, element.to, block, element.hollow === true);
      return;
  }
}

function fillBox(
  store: Map<string, VoxelBlock>,
  from: [number, number, number],
  to: [number, number, number],
  block: BlockType,
  hollow = false
): void {
  for (let x = from[0]; x <= to[0]; x += 1) {
    for (let y = from[1]; y <= to[1]; y += 1) {
      for (let z = from[2]; z <= to[2]; z += 1) {
        if (hollow && !isShell(x, y, z, from, to)) continue;
        setBlock(store, x, y, z, block);
      }
    }
  }
}

function fillPlane(
  store: Map<string, VoxelBlock>,
  from: [number, number, number],
  to: [number, number, number],
  block: BlockType,
  axis: "x" | "y" | "z"
): void {
  const [fx, fy, fz] = from;
  const [tx, ty, tz] = to;
  for (let x = fx; x <= tx; x += 1) {
    for (let y = fy; y <= ty; y += 1) {
      for (let z = fz; z <= tz; z += 1) {
        if ((axis === "x" && x !== fx) || (axis === "y" && y !== fy) || (axis === "z" && z !== fz)) {
          continue;
        }
        setBlock(store, x, y, z, block);
      }
    }
  }
}

function fillShell(
  store: Map<string, VoxelBlock>,
  from: [number, number, number],
  to: [number, number, number],
  block: BlockType
): void {
  for (let x = from[0]; x <= to[0]; x += 1) {
    for (let y = from[1]; y <= to[1]; y += 1) {
      for (let z = from[2]; z <= to[2]; z += 1) {
        const boundary = x === from[0] || x === to[0] || z === from[2] || z === to[2];
        if (boundary) setBlock(store, x, y, z, block);
      }
    }
  }
}

function fillRoof(
  store: Map<string, VoxelBlock>,
  from: [number, number, number],
  to: [number, number, number],
  block: BlockType
): void {
  const centerZ = (from[2] + to[2]) / 2;
  for (let x = from[0]; x <= to[0]; x += 1) {
    for (let z = from[2]; z <= to[2]; z += 1) {
      const ridge = Math.max(0, Math.round((to[2] - from[2]) / 2 - Math.abs(z - centerZ)));
      const y = Math.min(to[1], from[1] + ridge);
      setBlock(store, x, y, z, block);
    }
  }
}

function fillArch(
  store: Map<string, VoxelBlock>,
  from: [number, number, number],
  to: [number, number, number],
  block: BlockType
): void {
  const centerX = (from[0] + to[0]) / 2;
  const radius = Math.max(1, (to[0] - from[0]) / 2);
  for (let x = from[0]; x <= to[0]; x += 1) {
    const normalized = Math.abs(x - centerX) / radius;
    const archTop = to[1] - Math.round((1 - Math.sqrt(Math.max(0, 1 - normalized * normalized))) * radius);
    for (let y = from[1]; y <= to[1]; y += 1) {
      if (y >= archTop || x === from[0] || x === to[0]) {
        for (let z = from[2]; z <= to[2]; z += 1) setBlock(store, x, y, z, block);
      }
    }
  }
}

function isShell(
  x: number,
  y: number,
  z: number,
  from: [number, number, number],
  to: [number, number, number]
): boolean {
  return x === from[0] || x === to[0] || y === from[1] || y === to[1] || z === from[2] || z === to[2];
}
