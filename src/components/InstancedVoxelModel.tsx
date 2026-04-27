"use client";

import { useMemo } from "react";
import { Object3D } from "three";
import { groupBlocksByType, materialStyles, toRenderPosition } from "@/lib/voxel";
import type { VoxelModel } from "@/types/blueprint";

type InstancedVoxelModelProps = {
  model: VoxelModel;
};

const matrixHelper = new Object3D();

export function InstancedVoxelModel({ model }: InstancedVoxelModelProps) {
  const groups = useMemo(() => groupBlocksByType(model.blocks), [model.blocks]);

  return (
    <group>
      {groups.map((group) => {
        const style = materialStyles[group.blockType];
        return (
          <instancedMesh
            args={[undefined, undefined, group.blocks.length]}
            castShadow
            key={group.blockType}
            receiveShadow
            ref={(mesh) => {
              if (!mesh) return;
              group.blocks.forEach((block, index) => {
                matrixHelper.position.set(...toRenderPosition(block, model));
                matrixHelper.updateMatrix();
                mesh.setMatrixAt(index, matrixHelper.matrix);
              });
              mesh.instanceMatrix.needsUpdate = true;
            }}
          >
            <boxGeometry args={[0.96, 0.96, 0.96]} />
            <meshStandardMaterial
              color={style.color}
              emissive={style.emissive}
              emissiveIntensity={style.emissiveIntensity ?? 0}
              metalness={style.metalness}
              opacity={style.opacity}
              roughness={style.roughness}
              transparent={style.transparent}
            />
          </instancedMesh>
        );
      })}
    </group>
  );
}
