package com.blockforge.connector.release;

import com.blockforge.common.buildplan.BuildPlanFactory;
import com.blockforge.common.buildplan.BuildPlanOptions;
import com.blockforge.common.house.HouseBlueprintCompiler;
import com.blockforge.common.house.HouseMaterialEstimator;
import com.blockforge.common.house.HousePlan;
import com.blockforge.common.house.HousePlanGenerator;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HouseReleaseCompatibilityTest {
    @Test
    void housePresetsCompileToBuildPlanCompatibleBlueprints() {
        HousePlanGenerator generator = new HousePlanGenerator();
        HouseBlueprintCompiler compiler = new HouseBlueprintCompiler();

        for (HousePlan.HouseStyle style : generator.supportedPresets()) {
            HousePlan plan = generator.generatePreset(style);
            var blueprint = compiler.compile(plan);
            var buildPlan = BuildPlanFactory.create(
                    blueprint,
                    UUID.fromString("00000000-0000-0000-0000-000000000001"),
                    "minecraft:overworld",
                    0,
                    64,
                    0,
                    0,
                    false,
                    false,
                    0,
                    0,
                    0,
                    BuildPlanOptions.defaults(),
                    1L
            );

            assertTrue(blueprint.getBlockCount() > 0, style.name());
            assertTrue(buildPlan.totalBlocks() > 0, style.name());
            assertFalse(new HouseMaterialEstimator().estimate(plan).isEmpty(), style.name());
        }
    }
}
