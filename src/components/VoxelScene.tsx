"use client";

import { OrbitControls } from "@react-three/drei";
import { useMemo } from "react";
import { InstancedVoxelModel } from "@/components/InstancedVoxelModel";
import { VoxelCube } from "@/components/VoxelCube";
import {
  getModelCenter,
  getRecommendedCameraPosition,
  resolveRenderMode,
  type RenderMode
} from "@/lib/voxel";
import type { VoxelModel } from "@/types/blueprint";

type VoxelSceneProps = {
  model: VoxelModel;
  renderMode: RenderMode;
};

export function VoxelScene({ model, renderMode }: VoxelSceneProps) {
  const cameraTarget = useMemo<[number, number, number]>(
    () => getModelCenter(model),
    [model]
  );
  const cameraPosition = useMemo(() => getRecommendedCameraPosition(model), [model]);
  const resolvedRenderMode = resolveRenderMode(model.blocks.length, renderMode);
  const gridSize = useMemo(
    () => Math.max(model.size.width, model.size.depth) + 12,
    [model.size.depth, model.size.width]
  );

  return (
    <>
      <color args={["#080706"]} attach="background" />
      <fog args={["#080706", 24, 72]} attach="fog" />
      <ambientLight intensity={0.5} />
      <directionalLight
        castShadow
        color="#ffd08a"
        intensity={2.1}
        position={[10, 20, 12]}
        shadow-mapSize-height={1024}
        shadow-mapSize-width={1024}
      />
      <directionalLight color="#6fb8ff" intensity={0.36} position={[-12, 10, -8]} />

      {resolvedRenderMode === "instanced" ? (
        <InstancedVoxelModel model={model} />
      ) : (
        <group>
          {model.blocks.map((block) => (
            <VoxelCube
              block={block}
              key={`${block.x}-${block.y}-${block.z}`}
              model={model}
            />
          ))}
        </group>
      )}

      <gridHelper
        args={[gridSize, gridSize, "#c4852d", "#2a2117"]}
        position={[0, 0, 0]}
      />
      <mesh position={[0, -0.03, 0]} receiveShadow rotation={[-Math.PI / 2, 0, 0]}>
        <planeGeometry args={[gridSize, gridSize]} />
        <meshStandardMaterial color="#0d0b08" roughness={0.92} />
      </mesh>

      <OrbitControls
        enableDamping
        enablePan
        enableZoom
        makeDefault
        maxDistance={Math.max(18, cameraPosition[0] * 2.4)}
        minDistance={3}
        target={cameraTarget}
      />
    </>
  );
}
