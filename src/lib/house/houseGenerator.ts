import { defaultHouseOptions, type HouseGenerationInput, type HouseMaterials, type HousePlan, type HouseRoofType, type HouseStyle } from "@/lib/house/housePlan";

type HousePresetDefaults = {
  name: string;
  width: number;
  depth: number;
  floors: number;
  roofType: HouseRoofType;
  roofPitch: number;
  roofOverhang: number;
  floorHeight: number;
  materials: HouseMaterials;
  options?: Partial<typeof defaultHouseOptions>;
};

export function generateHousePlan(input: HouseGenerationInput): HousePlan {
  const defaults = presetDefaults[input.style] ?? presetDefaults.starter_cottage;
  const width = clamp(input.width ?? defaults.width, 5, 32);
  const depth = clamp(input.depth ?? defaults.depth, 5, 32);
  const floors = clamp(input.floors ?? defaults.floors, 1, 4);
  const roofType = input.roofType ?? defaults.roofType;
  const options = { ...defaultHouseOptions, ...defaults.options, ...input.options };
  const materials = { ...defaults.materials, ...input.materials };
  const roofHeight = options.buildRoof && roofType !== "none" ? (roofType === "flat" ? 1 : Math.max(2, defaults.roofPitch)) : 0;
  const dimensions = {
    floors,
    floorHeight: defaults.floorHeight,
    totalHeight: 1 + floors * defaults.floorHeight + roofHeight,
    foundationHeight: 1
  };
  const openings = createOpenings(width, depth, floors, materials, options, input.style);
  const planShell = {
    housePlanId: `house-${input.style}`,
    name: defaults.name,
    style: input.style,
    footprint: { width, depth, shape: "rectangle" as const },
    dimensions,
    layout: createLayout(width, depth, floors, input.style),
    roof: {
      type: roofType,
      overhang: defaults.roofOverhang,
      pitch: defaults.roofPitch,
      mainBlock: materials.roof,
      trimBlock: materials.trim
    },
    openings,
    materials,
    options,
    modules: [],
    issues: createIssues(width, depth, floors)
  };

  return {
    ...planShell,
    modules: planHouseModules(planShell)
  };
}

function planHouseModules(plan: Omit<HousePlan, "modules">): HousePlan["modules"] {
  const { width, depth } = plan.footprint;
  const foundationHeight = Math.max(1, plan.dimensions.foundationHeight);
  const wallStartY = foundationHeight;
  const wallHeight = Math.max(3, plan.dimensions.floors * plan.dimensions.floorHeight);
  const modules: HousePlan["modules"] = [];

  if (plan.options.buildFoundation) {
    modules.push({ moduleId: "foundation", type: "foundation", x: 0, y: 0, z: 0, width, height: foundationHeight, depth, block: plan.materials.foundation });
  }
  if (plan.options.buildInteriorFloor) {
    modules.push({ moduleId: "floor", type: "floor", x: 1, y: wallStartY, z: 1, width: width - 2, height: 1, depth: depth - 2, block: plan.materials.floor });
  }

  modules.push(
    { moduleId: "north_wall", type: "wall", x: 0, y: wallStartY, z: 0, width, height: wallHeight, depth: 1, block: plan.materials.wall },
    { moduleId: "south_wall", type: "wall", x: 0, y: wallStartY, z: depth - 1, width, height: wallHeight, depth: 1, block: plan.materials.wall },
    { moduleId: "west_wall", type: "wall", x: 0, y: wallStartY, z: 0, width: 1, height: wallHeight, depth, block: plan.materials.wall },
    { moduleId: "east_wall", type: "wall", x: width - 1, y: wallStartY, z: 0, width: 1, height: wallHeight, depth, block: plan.materials.wall }
  );

  modules.push(...plan.openings.doors.map((door) => ({
    moduleId: `door_${door.x}_${door.z}`,
    type: "door" as const,
    x: door.x,
    y: door.y,
    z: door.z,
    width: 1,
    height: 2,
    depth: 1,
    block: door.block
  })));
  modules.push(...plan.openings.windows.map((window) => ({
    moduleId: `window_${window.x}_${window.z}`,
    type: "window" as const,
    x: window.x,
    y: window.y,
    z: window.z,
    width: window.width,
    height: window.height,
    depth: 1,
    block: window.block
  })));

  if (plan.dimensions.floors > 1 && plan.options.addStairs) {
    modules.push({ moduleId: "stair_column", type: "stair", x: 2, y: wallStartY + 1, z: 2, width: 1, height: plan.dimensions.floorHeight, depth: 1, block: plan.materials.stair });
  }
  if (plan.options.addPorch) {
    modules.push({ moduleId: "front_porch", type: "porch", x: Math.max(1, Math.floor(width / 2) - 2), y: wallStartY, z: depth, width: 5, height: 1, depth: 2, block: plan.materials.trim });
  }
  if (plan.options.addChimney) {
    modules.push({ moduleId: "chimney", type: "chimney", x: width - 3, y: wallStartY + wallHeight - 1, z: 2, width: 1, height: 4, depth: 1, block: plan.materials.foundation });
  }
  if (plan.options.buildRoof && plan.roof.type !== "none") {
    modules.push({
      moduleId: "roof",
      type: "roof",
      x: -plan.roof.overhang,
      y: wallStartY + wallHeight,
      z: -plan.roof.overhang,
      width: width + plan.roof.overhang * 2,
      height: plan.roof.type === "flat" ? 1 : Math.max(2, plan.roof.pitch),
      depth: depth + plan.roof.overhang * 2,
      block: plan.roof.mainBlock
    });
  }

  return modules;
}

function createOpenings(width: number, depth: number, floors: number, materials: HouseMaterials, options: typeof defaultHouseOptions, style: HouseStyle): HousePlan["openings"] {
  const doors = options.addDoor ? [{ x: Math.floor(width / 2), y: 1, z: depth - 1, facing: "south" as const, block: materials.door }] : [];
  const windows = options.addWindows
    ? [
        { x: 2, y: 2, z: 0, width: 1, height: 1, facing: "north" as const, block: materials.window },
        { x: width - 3, y: 2, z: 0, width: 1, height: 1, facing: "north" as const, block: materials.window },
        ...(style === "storage_house" ? [] : [
          { x: 0, y: 2, z: Math.floor(depth / 2), width: 1, height: 1, facing: "west" as const, block: materials.window },
          { x: width - 1, y: 2, z: Math.floor(depth / 2), width: 1, height: 1, facing: "east" as const, block: materials.window }
        ]),
        ...(floors > 1 ? [{ x: Math.floor(width / 2), y: 5, z: 0, width: 1, height: 1, facing: "north" as const, block: materials.window }] : [])
      ]
    : [];

  return { doors, windows };
}

function createLayout(width: number, depth: number, floors: number, style: HouseStyle): HousePlan["layout"] {
  const rooms: HousePlan["layout"]["rooms"] = [
    { roomId: "main", name: "Main Room", type: "main_room", x: 1, z: 1, width: width - 2, depth: depth - 2, floor: 0 }
  ];
  if (["farmhouse", "medieval_house"].includes(style)) {
    rooms.push({ roomId: "bedroom", name: "Bedroom", type: "bedroom", x: Math.floor(width / 2), z: 1, width: Math.max(3, Math.floor(width / 2) - 2), depth: Math.max(3, Math.floor(depth / 2)), floor: 0 });
  }
  if (style === "workshop") {
    rooms.push({ roomId: "workshop", name: "Workshop", type: "workshop", x: 1, z: 1, width: width - 2, depth: depth - 2, floor: 0 });
  }
  if (style === "storage_house") {
    rooms.push({ roomId: "storage", name: "Storage", type: "storage", x: 1, z: 1, width: width - 2, depth: depth - 2, floor: 0 });
  }
  if (floors > 1) {
    rooms.push({ roomId: "stair", name: "Stair", type: "stair", x: 2, z: 2, width: 2, depth: 2, floor: 0 });
  }
  return { rooms, connections: [] };
}

function createIssues(width: number, depth: number, floors: number) {
  const issues: HousePlan["issues"] = [];
  if (width < 5 || depth < 5) {
    issues.push({ severity: "error", path: "footprint", message: "House footprint is too small.", suggestion: "Use at least 5 x 5." });
  }
  if (floors > 4) {
    issues.push({ severity: "error", path: "dimensions.floors", message: "Too many floors for alpha generation.", suggestion: "Use 4 floors or fewer." });
  }
  return issues;
}

function clamp(value: number, min: number, max: number) {
  return Math.max(min, Math.min(max, Math.round(value)));
}

const oakHouse: HouseMaterials = {
  foundation: "cobblestone",
  wall: "oak_planks",
  floor: "oak_planks",
  roof: "spruce_stairs",
  trim: "spruce_planks",
  window: "glass_pane",
  door: "door",
  stair: "oak_stairs",
  accent: "lantern"
};

const presetDefaults: Record<HouseStyle, HousePresetDefaults> = {
  starter_cottage: { name: "Starter Cottage", width: 9, depth: 7, floors: 1, roofType: "gable", roofPitch: 2, roofOverhang: 1, floorHeight: 3, materials: oakHouse },
  medieval_house: { name: "Medieval House", width: 11, depth: 9, floors: 2, roofType: "gable", roofPitch: 2, roofOverhang: 1, floorHeight: 3, materials: { ...oakHouse, wall: "spruce_planks", roof: "dark_oak_stairs", trim: "stripped_oak_log" }, options: { addChimney: true } },
  farmhouse: { name: "Farmhouse", width: 13, depth: 11, floors: 1, roofType: "gable", roofPitch: 2, roofOverhang: 1, floorHeight: 3, materials: { ...oakHouse, wall: "stripped_oak_log", accent: "barrel" }, options: { addPorch: true, addChimney: true } },
  workshop: { name: "Workshop", width: 13, depth: 9, floors: 1, roofType: "gable", roofPitch: 2, roofOverhang: 1, floorHeight: 3, materials: { ...oakHouse, foundation: "stone", wall: "cobblestone", floor: "stone", roof: "dark_oak_stairs", trim: "iron_bars", accent: "anvil" } },
  storage_house: { name: "Storage House", width: 11, depth: 9, floors: 1, roofType: "flat", roofPitch: 1, roofOverhang: 0, floorHeight: 3, materials: { ...oakHouse, wall: "stone_bricks", roof: "dark_oak_planks", accent: "chest" }, options: { addWindows: false } },
  watchtower_house: { name: "Watchtower House", width: 7, depth: 7, floors: 4, roofType: "tower", roofPitch: 3, roofOverhang: 0, floorHeight: 4, materials: { ...oakHouse, foundation: "stone_bricks", wall: "stone_bricks", floor: "stone", roof: "dark_oak_planks", trim: "iron_bars", stair: "ladder" } },
  market_house: { name: "Market House", width: 11, depth: 9, floors: 1, roofType: "shed", roofPitch: 1, roofOverhang: 1, floorHeight: 3, materials: { ...oakHouse, roof: "wool_red", trim: "fence", accent: "barrel" }, options: { addPorch: true } },
  longhouse: { name: "Longhouse", width: 17, depth: 9, floors: 1, roofType: "gable", roofPitch: 2, roofOverhang: 1, floorHeight: 3, materials: { ...oakHouse, wall: "spruce_planks", floor: "spruce_planks", roof: "spruce_stairs", trim: "spruce_log" }, options: { addChimney: true } }
};
