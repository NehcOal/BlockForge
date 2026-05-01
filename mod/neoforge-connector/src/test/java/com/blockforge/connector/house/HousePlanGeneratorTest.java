package com.blockforge.connector.house;

import com.blockforge.common.house.HouseGenerationRequest;
import com.blockforge.common.house.HousePlan;
import com.blockforge.common.house.HousePlan.HouseModuleType;
import com.blockforge.common.house.HousePlan.HouseStyle;
import com.blockforge.common.house.HousePlanGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HousePlanGeneratorTest {
    @Test
    void starterCottageGeneratesValidPlan() {
        HousePlan plan = new HousePlanGenerator().generatePreset(HouseStyle.STARTER_COTTAGE);

        assertTrue(plan.housePlanId().contains("starter_cottage"));
        assertTrue(plan.footprint().width() > 0);
        assertTrue(plan.footprint().depth() > 0);
        assertFalse(plan.openings().doors().isEmpty());
        assertFalse(plan.openings().windows().isEmpty());
        assertTrue(plan.modules().stream().anyMatch(module -> module.type() == HouseModuleType.ROOF));
    }

    @Test
    void allAlphaPresetsGenerateModules() {
        HousePlanGenerator generator = new HousePlanGenerator();

        for (HouseStyle style : generator.supportedPresets()) {
            HousePlan plan = generator.generatePreset(style);

            assertTrue(plan.modules().size() >= 6, style.name());
            assertTrue(plan.dimensions().totalHeight() > 0, style.name());
            assertTrue(plan.issues().stream().noneMatch(issue -> "error".equals(issue.severity())), style.name());
        }
    }

    @Test
    void customSizeIsClampedToAlphaLimits() {
        HousePlan plan = new HousePlanGenerator().generate(new HouseGenerationRequest(
                HouseStyle.FARMHOUSE,
                64,
                64,
                8,
                null,
                null,
                null
        ));

        assertTrue(plan.footprint().width() <= 32);
        assertTrue(plan.footprint().depth() <= 32);
        assertTrue(plan.dimensions().floors() <= 4);
    }
}
