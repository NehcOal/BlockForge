package com.blockforge.connector.house;

import com.blockforge.common.house.HouseMaterialEstimator;
import com.blockforge.common.house.HousePlan.HouseStyle;
import com.blockforge.common.house.HousePlanGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HouseMaterialEstimatorTest {
    @Test
    void materialEstimateCountsGeneratedModules() {
        var plan = new HousePlanGenerator().generatePreset(HouseStyle.FARMHOUSE);
        var estimate = new HouseMaterialEstimator().estimate(plan);

        assertFalse(estimate.isEmpty());
        assertTrue(estimate.values().stream().allMatch(count -> count > 0));
        assertTrue(estimate.containsKey(plan.materials().foundationBlock()));
        assertTrue(estimate.containsKey(plan.materials().wallBlock()));
    }
}
