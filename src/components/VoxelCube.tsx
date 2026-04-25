"use client";

import { blockStyles, toRenderPosition } from "@/lib/voxel";
import type { VoxelBlock, VoxelModel } from "@/types/blueprint";

type VoxelCubeProps = {
  block: VoxelBlock;
  model: VoxelModel;
};

export function VoxelCube({ block, model }: VoxelCubeProps) {
  const style = blockStyles[block.block];

  return (
    <mesh castShadow position={toRenderPosition(block, model)} receiveShadow>
      <boxGeometry args={[0.96, 0.96, 0.96]} />
      <meshStandardMaterial
        color={style.color}
        metalness={style.metalness ?? 0}
        opacity={style.opacity ?? 1}
        roughness={style.roughness ?? 0.75}
        transparent={style.transparent}
      />
    </mesh>
  );
}
