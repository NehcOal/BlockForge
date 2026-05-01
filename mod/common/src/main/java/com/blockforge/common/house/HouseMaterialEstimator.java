package com.blockforge.common.house;

import com.blockforge.common.house.HousePlan.HouseModule;
import com.blockforge.common.house.HousePlan.HouseModuleType;

import java.util.Map;
import java.util.TreeMap;

public final class HouseMaterialEstimator {
    public Map<String, Integer> estimate(HousePlan plan) {
        Map<String, Integer> counts = new TreeMap<>();
        for (HouseModule module : plan.modules()) {
            int count = switch (module.type()) {
                case DOOR -> 1;
                case WINDOW -> Math.max(1, module.width() * module.height());
                case ROOF -> estimateRoof(module);
                case WALL -> estimateWall(module);
                default -> Math.max(1, module.volume());
            };
            counts.merge(module.blockKey(), count, Integer::sum);
        }
        return counts;
    }

    private static int estimateWall(HouseModule module) {
        return Math.max(1, module.width() * module.height() * module.depth());
    }

    private static int estimateRoof(HouseModule module) {
        return Math.max(1, module.width() * module.depth() + module.width() * Math.max(0, module.height() - 1));
    }
}
