package com.blockforge.common.house;

import com.blockforge.common.house.HousePlan.HouseConstructionOptions;
import com.blockforge.common.house.HousePlan.HouseMaterials;
import com.blockforge.common.house.HousePlan.HouseRoofType;
import com.blockforge.common.house.HousePlan.HouseStyle;

public record HouseGenerationRequest(
        HouseStyle style,
        int width,
        int depth,
        int floors,
        HouseRoofType roofType,
        HouseMaterials materials,
        HouseConstructionOptions options
) {
    public static HouseGenerationRequest preset(HouseStyle style) {
        return new HouseGenerationRequest(style, 0, 0, 0, null, null, null);
    }
}
