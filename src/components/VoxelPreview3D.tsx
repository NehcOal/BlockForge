"use client";

import { Canvas } from "@react-three/fiber";
import { Suspense, useMemo } from "react";
import { VoxelScene } from "@/components/VoxelScene";
import { getRecommendedCameraPosition, type RenderMode } from "@/lib/voxel";
import type { VoxelModel } from "@/types/blueprint";

type VoxelPreview3DProps = {
  emptyMessage?: string;
  model: VoxelModel;
  onCanvasReady?: (canvas: HTMLCanvasElement | null) => void;
  renderMode: RenderMode;
};

export function VoxelPreview3D({ emptyMessage, model, onCanvasReady, renderMode }: VoxelPreview3DProps) {
  const cameraPosition = useMemo(() => getRecommendedCameraPosition(model), [model]);

  if (model.blocks.length === 0) {
    return (
      <div className="flex h-[420px] items-center justify-center rounded-md border border-forge/20 bg-stone-950 text-sm text-stone-500">
        {emptyMessage ?? "No voxel blocks to preview yet."}
      </div>
    );
  }

  return (
    <div className="h-[420px] overflow-hidden bg-transparent sm:h-[520px] xl:h-[560px]">
      <Canvas
        camera={{
          fov: 45,
          near: 0.1,
          far: 1000,
          position: cameraPosition
        }}
        dpr={[1, 1.5]}
        gl={{ antialias: true, preserveDrawingBuffer: true }}
        onCreated={({ gl }) => onCanvasReady?.(gl.domElement)}
        shadows
      >
        <Suspense fallback={null}>
          <VoxelScene model={model} renderMode={renderMode} />
        </Suspense>
      </Canvas>
    </div>
  );
}
