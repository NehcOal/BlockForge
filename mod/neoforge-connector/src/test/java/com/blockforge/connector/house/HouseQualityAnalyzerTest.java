package com.blockforge.connector.house;

import com.blockforge.common.house.HousePlan.HouseStyle;
import com.blockforge.common.house.HousePlanGenerator;
import com.blockforge.common.house.HouseQualityAnalyzer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HouseQualityAnalyzerTest {
    @Test
    void qualityScoreStaysInsideRange() {
        var plan = new HousePlanGenerator().generatePreset(HouseStyle.STARTER_COTTAGE);
        var report = new HouseQualityAnalyzer().analyze(plan);

        assertTrue(report.total() >= 0);
        assertTrue(report.total() <= 100);
        assertTrue(report.entrance() >= 80);
        assertTrue(report.roof() >= 80);
    }

    @Test
    void multiFloorHouseRequiresAccessPlan() {
        var plan = new HousePlanGenerator().generatePreset(HouseStyle.WATCHTOWER_HOUSE);
        var report = new HouseQualityAnalyzer().analyze(plan);

        assertTrue(report.interior() > 0);
        assertTrue(report.total() >= 70);
    }
}
