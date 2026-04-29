import type { LitematicRegion } from "@/lib/voxel/litematic/litematicTypes";

export function getLitematicRegionVolume(region: LitematicRegion): number {
  return region.size.width * region.size.height * region.size.depth;
}

export function getLitematicTotalVolume(regions: LitematicRegion[]): number {
  return regions.reduce((total, region) => total + getLitematicRegionVolume(region), 0);
}
