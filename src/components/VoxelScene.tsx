"use client";

import { OrbitControls } from "@react-three/drei";
import { useMemo } from "react";
import { VoxelCube } from "@/components/VoxelCube";
import { getCameraPosition } from "@/lib/voxel";
import type { VoxelModel } from "@/types/blueprint";

type VoxelSceneProps = {
  model: VoxelModel;
};

export function VoxelScene({ model }: VoxelSceneProps) {
  const cameraTarget = useMemo<[number, number, number]>(
    () => [0, model.size.height / 2, 0],
    [model.size.height]
  );
  const cameraPosition = useMemo(() => getCameraPosition(model), [model]);
  const gridSize = useMemo(
    () => Math.max(model.size.width, model.size.depth) + 8,
    [model.size.depth, model.size.width]
  );

  return (
    <>
      <color args={["#080706"]} attach="background" />
      <fog args={["#080706", 22, 55]} attach="fog" />
      <ambientLight intensity={0.42} />
      <directionalLight
        castShadow
        color="#ffd08a"
        intensity={2.1}
        position={[10, 20, 12]}
        shadow-mapSize-height={1024}
        shadow-mapSize-width={1024}
      />
      <directionalLight color="#6fb8ff" intensity={0.36} position={[-12, 10, -8]} />

      <group>
        {model.blocks.map((block) => (
          <VoxelCube
            block={block}
            key={`${block.x}-${block.y}-${block.z}`}
            model={model}
          />
        ))}
      </group>

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
        maxDistance={cameraPosition[0] * 2.2}
        minDistance={5}
        target={cameraTarget}
      />
    </>
  );
}
