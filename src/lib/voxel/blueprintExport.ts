import { voxelModelToBlueprintV1 } from "@/lib/voxel/blueprintProtocol";
import { voxelModelToBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";
import { createSafeResourcePath } from "@/lib/voxel/exportUtils";
import type { VoxelModel } from "@/types/blueprint";

export function voxelModelToBlueprintJson(model: VoxelModel): string {
  return JSON.stringify(voxelModelToBlueprintV1(model), null, 2);
}

export function voxelModelToBlueprintV2Json(model: VoxelModel): string {
  return JSON.stringify(voxelModelToBlueprintV2(model), null, 2);
}

export function createBlueprintJsonFileName(model: VoxelModel): string {
  return `blockforge-${createSafeResourcePath(model.name)}.v1.blueprint.json`;
}

export function createBlueprintV2JsonFileName(model: VoxelModel): string {
  return `blockforge-${createSafeResourcePath(model.name)}.v2.blueprint.json`;
}
