import type { BlockType } from "@/types/blueprint";

export type HouseStyle =
  | "starter_cottage"
  | "medieval_house"
  | "farmhouse"
  | "watchtower_house"
  | "storage_house"
  | "workshop"
  | "market_house"
  | "longhouse";

export type HouseRoofType =
  | "flat"
  | "gable"
  | "hip"
  | "pyramid"
  | "tower"
  | "shed"
  | "none";

export type HouseFootprintShape = "rectangle" | "l_shape" | "t_shape" | "courtyard" | "custom";

export type HouseRoomType =
  | "entry"
  | "main_room"
  | "bedroom"
  | "storage"
  | "workshop"
  | "kitchen"
  | "stair"
  | "balcony"
  | "custom";

export type HouseModuleType =
  | "foundation"
  | "floor"
  | "wall"
  | "roof"
  | "door"
  | "window"
  | "stair"
  | "chimney"
  | "porch"
  | "balcony"
  | "trim"
  | "interior"
  | "decoration";

export type HouseFootprint = {
  width: number;
  depth: number;
  shape: HouseFootprintShape;
};

export type HouseDimensions = {
  floors: number;
  floorHeight: number;
  totalHeight: number;
  foundationHeight: number;
};

export type HouseRoof = {
  type: HouseRoofType;
  overhang: number;
  pitch: number;
  mainBlock: BlockType;
  trimBlock: BlockType;
};

export type HouseRoom = {
  roomId: string;
  name: string;
  type: HouseRoomType;
  x: number;
  z: number;
  width: number;
  depth: number;
  floor: number;
};

export type HouseLayout = {
  rooms: HouseRoom[];
  connections: Array<{ fromRoomId: string; toRoomId: string; type: string }>;
};

export type HouseDoor = {
  x: number;
  y: number;
  z: number;
  facing: "north" | "south" | "east" | "west";
  block: BlockType;
};

export type HouseWindow = {
  x: number;
  y: number;
  z: number;
  width: number;
  height: number;
  facing: "north" | "south" | "east" | "west";
  block: BlockType;
};

export type HouseOpenings = {
  doors: HouseDoor[];
  windows: HouseWindow[];
};

export type HouseMaterials = {
  foundation: BlockType;
  wall: BlockType;
  floor: BlockType;
  roof: BlockType;
  trim: BlockType;
  window: BlockType;
  door: BlockType;
  stair: BlockType;
  accent: BlockType;
};

export type HouseConstructionOptions = {
  buildFoundation: boolean;
  buildInteriorFloor: boolean;
  buildRoof: boolean;
  addWindows: boolean;
  addDoor: boolean;
  addChimney: boolean;
  addPorch: boolean;
  addStairs: boolean;
  hollowInterior: boolean;
  survivalFriendly: boolean;
  useSymmetry: boolean;
};

export type HouseModule = {
  moduleId: string;
  type: HouseModuleType;
  x: number;
  y: number;
  z: number;
  width: number;
  height: number;
  depth: number;
  block: BlockType;
};

export type HouseIssue = {
  severity: "info" | "warning" | "error";
  path: string;
  message: string;
  suggestion: string;
};

export type HousePlan = {
  housePlanId: string;
  name: string;
  style: HouseStyle;
  footprint: HouseFootprint;
  dimensions: HouseDimensions;
  layout: HouseLayout;
  roof: HouseRoof;
  openings: HouseOpenings;
  materials: HouseMaterials;
  options: HouseConstructionOptions;
  modules: HouseModule[];
  issues: HouseIssue[];
};

export type HouseGenerationInput = {
  style: HouseStyle;
  width?: number;
  depth?: number;
  floors?: number;
  roofType?: HouseRoofType;
  materials?: Partial<HouseMaterials>;
  options?: Partial<HouseConstructionOptions>;
};

export const houseStyles: HouseStyle[] = [
  "starter_cottage",
  "medieval_house",
  "farmhouse",
  "workshop",
  "storage_house",
  "watchtower_house",
  "market_house",
  "longhouse"
];

export const defaultHouseOptions: HouseConstructionOptions = {
  buildFoundation: true,
  buildInteriorFloor: true,
  buildRoof: true,
  addWindows: true,
  addDoor: true,
  addChimney: false,
  addPorch: false,
  addStairs: true,
  hollowInterior: true,
  survivalFriendly: true,
  useSymmetry: true
};
