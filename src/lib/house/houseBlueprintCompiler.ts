import type { HouseModule, HousePlan } from "@/lib/house/housePlan";
import { blocksFromStore, getBlockKey, setBlock, validateVoxelModel } from "@/lib/voxel";
import type { BlockType, VoxelModel } from "@/types/blueprint";

export function housePlanToVoxelModel(plan: HousePlan): VoxelModel {
  const store = new Map<string, { x: number; y: number; z: number; block: BlockType }>();

  for (const houseModule of plan.modules) {
    placeModule(store, houseModule, plan);
  }

  const model: VoxelModel = {
    id: plan.housePlanId,
    name: plan.name,
    description: `Rule-based BlockForge house plan generated from ${plan.style}.`,
    size: {
      width: plan.footprint.width,
      height: plan.dimensions.totalHeight,
      depth: plan.footprint.depth
    },
    blocks: blocksFromStore(store)
  };

  if (!validateVoxelModel(model)) {
    throw new Error(`Generated house model failed validation: ${plan.housePlanId}`);
  }

  return model;
}

function placeModule(store: Map<string, { x: number; y: number; z: number; block: BlockType }>, module: HouseModule, plan: HousePlan) {
  if (module.type === "door" || module.type === "window") {
    clearOpening(store, module);
  }

  if (module.type === "roof") {
    placeRoof(store, module, plan);
    return;
  }

  for (let x = 0; x < module.width; x++) {
    for (let y = 0; y < module.height; y++) {
      for (let z = 0; z < module.depth; z++) {
        const px = module.x + x;
        const py = module.y + y;
        const pz = module.z + z;
        if (inBounds(px, py, pz, plan)) {
          setBlock(store, px, py, pz, module.block);
        }
      }
    }
  }
}

function placeRoof(store: Map<string, { x: number; y: number; z: number; block: BlockType }>, module: HouseModule, plan: HousePlan) {
  const { width, depth } = plan.footprint;
  const baseY = module.y;

  if (plan.roof.type === "flat" || plan.roof.type === "shed") {
    for (let x = 0; x < width; x++) {
      for (let z = 0; z < depth; z++) {
        setBlock(store, x, baseY, z, module.block);
      }
    }
    return;
  }

  if (plan.roof.type === "tower" || plan.roof.type === "pyramid" || plan.roof.type === "hip") {
    for (let layer = 0; layer < module.height; layer++) {
      for (let x = layer; x < width - layer; x++) {
        for (let z = layer; z < depth - layer; z++) {
          const edge = x === layer || x === width - layer - 1 || z === layer || z === depth - layer - 1 || layer === module.height - 1;
          if (edge) {
            setBlock(store, x, baseY + layer, z, module.block);
          }
        }
      }
    }
    return;
  }

  if (plan.roof.type === "gable") {
    const ridge = Math.floor(width / 2);
    for (let z = 0; z < depth; z++) {
      for (let x = 0; x < width; x++) {
        const y = baseY + Math.min(module.height - 1, Math.max(0, Math.min(x, width - 1 - x)));
        if (Math.abs(x - ridge) <= Math.max(1, module.height)) {
          setBlock(store, x, y, z, module.block);
        }
      }
      setBlock(store, ridge, baseY + module.height - 1, z, plan.roof.trimBlock);
    }
  }
}

function clearOpening(store: Map<string, { x: number; y: number; z: number; block: BlockType }>, module: HouseModule) {
  for (let x = 0; x < Math.max(1, module.width); x++) {
    for (let y = 0; y < Math.max(1, module.height); y++) {
      for (let z = 0; z < Math.max(1, module.depth); z++) {
        store.delete(getBlockKey({ x: module.x + x, y: module.y + y, z: module.z + z }));
      }
    }
  }
}

function inBounds(x: number, y: number, z: number, plan: HousePlan) {
  return x >= 0 && y >= 0 && z >= 0 && x < plan.footprint.width && y < plan.dimensions.totalHeight && z < plan.footprint.depth;
}
