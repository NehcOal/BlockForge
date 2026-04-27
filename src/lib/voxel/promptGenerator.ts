import { blocksFromStore, setBlock } from "@/lib/voxel/utils";
import type { BlockType, VoxelBlock, VoxelModel, VoxelSize } from "@/types/blueprint";

export type PromptGenerationKind = "tower" | "cottage" | "bridge" | "dungeon" | "statue";
export type PromptGenerationScale = "small" | "medium" | "large";

export type PromptGenerationResult = {
  model: VoxelModel;
  kind: PromptGenerationKind;
  scale: PromptGenerationScale;
  materials: BlockType[];
  notes: string[];
};

type PromptRecipe = {
  kind: PromptGenerationKind;
  scale: PromptGenerationScale;
  primary: BlockType;
  accent: BlockType;
  light: BlockType;
};

export function generateVoxelModelFromPrompt(prompt: string): PromptGenerationResult {
  const recipe = readRecipe(prompt);
  const store = new Map<string, VoxelBlock>();
  const size = modelSize(recipe);

  switch (recipe.kind) {
    case "tower":
      buildTower(store, size, recipe);
      break;
    case "cottage":
      buildCottage(store, size, recipe);
      break;
    case "bridge":
      buildBridge(store, size, recipe);
      break;
    case "dungeon":
      buildDungeon(store, size, recipe);
      break;
    case "statue":
      buildStatue(store, size, recipe);
      break;
  }

  const cleanPrompt = prompt.trim();
  const title = `${capitalize(recipe.scale)} ${capitalize(recipe.kind)}`;
  return {
    model: {
      id: `generated-${recipe.kind}-${recipe.scale}`,
      name: cleanPrompt ? `Generated ${title}` : title,
      description: cleanPrompt
        ? `Local rule-generated ${recipe.kind} from prompt: ${cleanPrompt}`
        : `Local rule-generated ${recipe.kind}.`,
      size,
      blocks: blocksFromStore(store)
    },
    kind: recipe.kind,
    scale: recipe.scale,
    materials: Array.from(new Set([recipe.primary, recipe.accent, recipe.light])),
    notes: [
      "Generated locally from deterministic rules.",
      "No external AI API is used.",
      "Export formats remain compatible with existing BlockForge workflows."
    ]
  };
}

function readRecipe(prompt: string): PromptRecipe {
  const text = prompt.toLowerCase();
  return {
    kind: readKind(text),
    scale: readScale(text),
    primary: readPrimary(text),
    accent: text.includes("wood") || text.includes("木") ? "oak_log" : "cobblestone",
    light: "torch"
  };
}

function readKind(text: string): PromptGenerationKind {
  if (includesAny(text, ["bridge", "桥"])) return "bridge";
  if (includesAny(text, ["dungeon", "gate", "entrance", "地牢", "入口"])) return "dungeon";
  if (includesAny(text, ["statue", "pixel", "雕像", "像素"])) return "statue";
  if (includesAny(text, ["cottage", "house", "hut", "小屋", "房子"])) return "cottage";
  return "tower";
}

function readScale(text: string): PromptGenerationScale {
  if (includesAny(text, ["large", "huge", "castle", "big", "大型", "巨大", "城堡"])) return "large";
  if (includesAny(text, ["small", "tiny", "mini", "小型", "迷你"])) return "small";
  return "medium";
}

function readPrimary(text: string): BlockType {
  if (includesAny(text, ["gold", "金"])) return "gold_block";
  if (includesAny(text, ["wood", "木"])) return "oak_planks";
  if (includesAny(text, ["wool", "red", "红"])) return "wool_red";
  if (includesAny(text, ["blue", "蓝"])) return "wool_blue";
  if (includesAny(text, ["stone", "石"])) return "stone_bricks";
  return "stone_bricks";
}

function modelSize(recipe: PromptRecipe): VoxelSize {
  const scale = recipe.scale === "large" ? 1.35 : recipe.scale === "small" ? 0.82 : 1;
  const base: Record<PromptGenerationKind, VoxelSize> = {
    tower: { width: 13, height: 18, depth: 13 },
    cottage: { width: 14, height: 9, depth: 11 },
    bridge: { width: 19, height: 7, depth: 9 },
    dungeon: { width: 15, height: 10, depth: 10 },
    statue: { width: 11, height: 16, depth: 7 }
  };
  const selected = base[recipe.kind];
  return {
    width: Math.max(5, Math.round(selected.width * scale)),
    height: Math.max(5, Math.round(selected.height * scale)),
    depth: Math.max(5, Math.round(selected.depth * scale))
  };
}

function buildTower(store: Map<string, VoxelBlock>, size: VoxelSize, recipe: PromptRecipe): void {
  const min = 1;
  const maxX = size.width - 2;
  const maxZ = size.depth - 2;
  for (let y = 0; y < size.height - 3; y += 1) {
    for (let x = min; x <= maxX; x += 1) {
      for (let z = min; z <= maxZ; z += 1) {
        const wall = x === min || x === maxX || z === min || z === maxZ;
        const floor = y === 0 || y % 6 === 0;
        if (wall || floor) setBlock(store, x, y, z, floor ? recipe.accent : recipe.primary);
      }
    }
  }
  for (let y = 4; y < size.height - 4; y += 4) {
    setBlock(store, Math.floor(size.width / 2), y, min, "glass");
    setBlock(store, Math.floor(size.width / 2), y, maxZ, "glass");
    setBlock(store, min, y, Math.floor(size.depth / 2), "glass");
    setBlock(store, maxX, y, Math.floor(size.depth / 2), "glass");
  }
  for (let x = 0; x < size.width; x += 2) {
    setBlock(store, x, size.height - 2, 0, recipe.primary);
    setBlock(store, x, size.height - 2, size.depth - 1, recipe.primary);
  }
  for (let z = 0; z < size.depth; z += 2) {
    setBlock(store, 0, size.height - 2, z, recipe.primary);
    setBlock(store, size.width - 1, size.height - 2, z, recipe.primary);
  }
}

function buildCottage(store: Map<string, VoxelBlock>, size: VoxelSize, recipe: PromptRecipe): void {
  for (let x = 0; x < size.width; x += 1) {
    for (let z = 0; z < size.depth; z += 1) setBlock(store, x, 0, z, "stone_bricks");
  }
  for (let y = 1; y < size.height - 3; y += 1) {
    for (let x = 0; x < size.width; x += 1) {
      for (let z = 0; z < size.depth; z += 1) {
        const wall = x === 0 || x === size.width - 1 || z === 0 || z === size.depth - 1;
        const corner = (x === 0 || x === size.width - 1) && (z === 0 || z === size.depth - 1);
        if (wall) setBlock(store, x, y, z, corner ? "oak_log" : "oak_planks");
      }
    }
  }
  const doorX = Math.floor(size.width / 2);
  setBlock(store, doorX, 1, 0, "door");
  setBlock(store, doorX, 2, 0, "door");
  for (const x of [2, size.width - 3]) {
    setBlock(store, x, 2, 0, "glass");
    setBlock(store, x, 3, 0, "glass");
  }
  for (let y = size.height - 3; y < size.height; y += 1) {
    const inset = y - (size.height - 3);
    for (let x = inset; x < size.width - inset; x += 1) {
      setBlock(store, x, y, inset, recipe.primary);
      setBlock(store, x, y, size.depth - 1 - inset, recipe.primary);
    }
  }
}

function buildBridge(store: Map<string, VoxelBlock>, size: VoxelSize, recipe: PromptRecipe): void {
  for (let x = 0; x < size.width; x += 1) {
    for (let z = 0; z < size.depth; z += 1) setBlock(store, x, 0, z, "water");
    for (let z = 2; z < size.depth - 2; z += 1) setBlock(store, x, 3, z, recipe.primary);
    setBlock(store, x, 4, 1, recipe.accent);
    setBlock(store, x, 4, size.depth - 2, recipe.accent);
  }
  for (const x of [2, 3, size.width - 4, size.width - 3]) {
    for (let z = 2; z < size.depth - 2; z += 1) {
      setBlock(store, x, 1, z, recipe.accent);
      setBlock(store, x, 2, z, recipe.accent);
    }
  }
}

function buildDungeon(store: Map<string, VoxelBlock>, size: VoxelSize, recipe: PromptRecipe): void {
  for (let x = 0; x < size.width; x += 1) {
    for (let z = 0; z < size.depth; z += 1) setBlock(store, x, 0, z, "stone");
  }
  for (let y = 1; y < size.height - 2; y += 1) {
    for (let x = 0; x < size.width; x += 1) {
      const gap = x >= Math.floor(size.width / 2) - 1 && x <= Math.floor(size.width / 2) + 1 && y < size.height - 4;
      if (!gap) setBlock(store, x, y, 0, recipe.primary);
      setBlock(store, x, y, size.depth - 1, "stone_bricks");
    }
    setBlock(store, 0, y, Math.floor(size.depth / 2), recipe.accent);
    setBlock(store, size.width - 1, y, Math.floor(size.depth / 2), recipe.accent);
  }
  setBlock(store, 3, 3, 1, recipe.light);
  setBlock(store, size.width - 4, 3, 1, recipe.light);
}

function buildStatue(store: Map<string, VoxelBlock>, size: VoxelSize, recipe: PromptRecipe): void {
  for (let x = 1; x < size.width - 1; x += 1) {
    for (let z = 1; z < size.depth - 1; z += 1) setBlock(store, x, 0, z, "stone");
  }
  const center = Math.floor(size.width / 2);
  for (let y = 1; y < size.height - 4; y += 1) {
    for (let x = center - 2; x <= center + 2; x += 1) {
      for (let z = 2; z < size.depth - 2; z += 1) {
        setBlock(store, x, y, z, y < 5 ? "wool_blue" : recipe.primary);
      }
    }
  }
  for (let y = size.height - 4; y < size.height - 1; y += 1) {
    for (let x = center - 1; x <= center + 1; x += 1) {
      for (let z = 2; z < size.depth - 2; z += 1) setBlock(store, x, y, z, "wool_white");
    }
  }
  for (let x = center - 2; x <= center + 2; x += 1) setBlock(store, x, size.height - 1, Math.floor(size.depth / 2), "gold_block");
}

function includesAny(text: string, needles: string[]): boolean {
  return needles.some((needle) => text.includes(needle));
}

function capitalize(value: string): string {
  return value.charAt(0).toUpperCase() + value.slice(1);
}
