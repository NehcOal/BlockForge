package com.blockforge.common.house;

import com.blockforge.common.house.HousePlan.HouseConstructionOptions;
import com.blockforge.common.house.HousePlan.HouseDoor;
import com.blockforge.common.house.HousePlan.HouseDimensions;
import com.blockforge.common.house.HousePlan.HouseFootprint;
import com.blockforge.common.house.HousePlan.HouseMaterials;
import com.blockforge.common.house.HousePlan.HouseModule;
import com.blockforge.common.house.HousePlan.HouseModuleType;
import com.blockforge.common.house.HousePlan.HouseOpenings;
import com.blockforge.common.house.HousePlan.HouseRoof;
import com.blockforge.common.house.HousePlan.HouseRoofType;
import com.blockforge.common.house.HousePlan.HouseWindow;

import java.util.ArrayList;
import java.util.List;

public final class HouseModulePlanner {
    public List<HouseModule> planModules(
            HouseFootprint footprint,
            HouseDimensions dimensions,
            HouseRoof roof,
            HouseOpenings openings,
            HouseMaterials materials,
            HouseConstructionOptions options
    ) {
        List<HouseModule> modules = new ArrayList<>();
        int width = footprint.width();
        int depth = footprint.depth();
        int foundationHeight = Math.max(1, dimensions.foundationHeight());
        int wallStartY = foundationHeight;
        int wallHeight = Math.max(3, dimensions.floors() * dimensions.floorHeight());

        if (options.buildFoundation()) {
            modules.add(new HouseModule("foundation", HouseModuleType.FOUNDATION, 0, 0, 0, width, foundationHeight, depth, materials.foundationBlock()));
        }
        if (options.buildInteriorFloor()) {
            modules.add(new HouseModule("floor", HouseModuleType.FLOOR, 1, wallStartY, 1, Math.max(1, width - 2), 1, Math.max(1, depth - 2), materials.floorBlock()));
        }

        modules.add(new HouseModule("north_wall", HouseModuleType.WALL, 0, wallStartY, 0, width, wallHeight, 1, materials.wallBlock()));
        modules.add(new HouseModule("south_wall", HouseModuleType.WALL, 0, wallStartY, depth - 1, width, wallHeight, 1, materials.wallBlock()));
        modules.add(new HouseModule("west_wall", HouseModuleType.WALL, 0, wallStartY, 0, 1, wallHeight, depth, materials.wallBlock()));
        modules.add(new HouseModule("east_wall", HouseModuleType.WALL, width - 1, wallStartY, 0, 1, wallHeight, depth, materials.wallBlock()));

        for (HouseDoor door : openings.doors()) {
            modules.add(new HouseModule("door_" + door.x() + "_" + door.z(), HouseModuleType.DOOR, door.x(), door.y(), door.z(), 1, 2, 1, door.blockId()));
        }
        for (HouseWindow window : openings.windows()) {
            modules.add(new HouseModule("window_" + window.x() + "_" + window.z(), HouseModuleType.WINDOW, window.x(), window.y(), window.z(), window.width(), window.height(), 1, window.blockId()));
        }

        if (dimensions.floors() > 1 && options.addStairs()) {
            modules.add(new HouseModule("stair_column", HouseModuleType.STAIR, 2, wallStartY + 1, 2, 1, dimensions.floorHeight(), 1, materials.stairBlock()));
        }
        if (options.addPorch()) {
            modules.add(new HouseModule("front_porch", HouseModuleType.PORCH, Math.max(1, width / 2 - 2), wallStartY, depth, 5, 1, 2, materials.trimBlock()));
        }
        if (options.addChimney()) {
            modules.add(new HouseModule("chimney", HouseModuleType.CHIMNEY, width - 3, wallStartY + wallHeight - 1, 2, 1, 4, 1, materials.foundationBlock()));
        }
        if (options.buildRoof() && roof.type() != HouseRoofType.NONE) {
            int roofY = wallStartY + wallHeight;
            int roofHeight = roof.type() == HouseRoofType.FLAT ? 1 : Math.max(2, roof.pitch());
            modules.add(new HouseModule("roof", HouseModuleType.ROOF, -roof.overhang(), roofY, -roof.overhang(), width + roof.overhang() * 2, roofHeight, depth + roof.overhang() * 2, roof.mainBlockId()));
        }

        return modules;
    }
}
