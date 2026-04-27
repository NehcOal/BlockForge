export {
  blockTypes,
  blocksFromStore,
  createBlock,
  getAllPresets,
  getBlockKey,
  getPresetById,
  getUsedBlockTypes,
  setBlock,
  validateVoxelModel
} from "@/lib/voxel/utils";
export { blockStyles } from "@/lib/voxel/blockStyles";
export {
  createBlueprintJsonFileName,
  createBlueprintV2JsonFileName,
  voxelModelToBlueprintV2Json,
  voxelModelToBlueprintJson
} from "@/lib/voxel/blueprintExport";
export { voxelModelToBlueprintV1 } from "@/lib/voxel/blueprintProtocol";
export { voxelModelToBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";
export {
  BLOCKFORGE_PACK_EXTENSION,
  createPackManifestFromModels,
  createPackRegistryId,
  validatePackBlueprintPath,
  validatePackManifest,
  type BlockForgePackBlueprintEntryV1,
  type BlockForgePackManifestV1,
  type ImportedBlueprintPack,
  type PackExportOptions
} from "@/lib/voxel/blueprintPack";
export { voxelModelsToBlueprintPackZip } from "@/lib/voxel/blueprintPackExport";
export { importBlueprintPackZip } from "@/lib/voxel/blueprintPackImport";
export {
  createImportedBlueprintAsset,
  createImportedPackAsset,
  createImportedSchematicAsset
} from "@/lib/voxel/blueprintImport";
export {
  formatValidationSummary,
  validateBlueprintJson
} from "@/lib/voxel/blueprintValidation";
export { exportSpongeSchematicBlob, blueprintV2ToSpongeSchematic } from "@/lib/voxel/schematic/spongeSchematicExport";
export { importSpongeSchematicBlob, spongeSchematicToBlueprintV2 } from "@/lib/voxel/schematic/spongeSchematicImport";
export { schematicFileName } from "@/lib/voxel/schematic/schematicValidation";
export {
  generateVoxelModelFromPrompt,
  type PromptGenerationKind,
  type PromptGenerationResult,
  type PromptGenerationScale
} from "@/lib/voxel/promptGenerator";
export {
  createSafeFileName,
  createSafeResourcePath
} from "@/lib/voxel/exportUtils";
export {
  createPackMcmeta,
  getFunctionName,
  getFunctionPath,
  voxelModelToDataPackZip
} from "@/lib/voxel/datapack";
export { minecraftBlockIds } from "@/lib/voxel/minecraftBlocks";
export { voxelModelToMcFunction } from "@/lib/voxel/mcfunction";
export {
  getCameraPosition,
  getModelCenterOffset,
  toRenderPosition
} from "@/lib/voxel/rendering";
export {
  createDungeonEntrance,
  createMedievalTower,
  createPixelStatue,
  createSmallCottage,
  createStoneBridge
} from "@/lib/voxel/presets";
export type {
  BlockType,
  PresetId,
  VoxelBlock,
  VoxelModel,
  VoxelSize
} from "@/lib/voxel/types";
export type { McFunctionExportOptions } from "@/lib/voxel/mcfunction";
export type { DataPackExportOptions } from "@/lib/voxel/datapack";
export type { BlockForgeBlueprintV1 } from "@/lib/voxel/blueprintProtocol";
export type {
  BlockForgeBlockStateV2,
  BlockForgeBlueprintBlockV2,
  BlockForgeBlueprintV2
} from "@/lib/voxel/blueprintProtocolV2";
export type {
  ImportedBlueprintAsset,
  ImportedBlueprintSourceType,
  ImportedBlueprintSummary
} from "@/lib/voxel/blueprintImport";
export type {
  BlueprintValidationIssue,
  BlueprintValidationReport,
  ValidationSeverity
} from "@/lib/voxel/blueprintValidation";
export type {
  ImportedSpongeSchematic,
  SpongeSchematicExportOptions,
  SpongeSchematicV3
} from "@/lib/voxel/schematic/spongeSchematic";
