package com.blockforge.connector.house;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.house.HouseBlueprintCompiler;
import com.blockforge.common.house.HousePlan.HouseStyle;
import com.blockforge.common.house.HousePlanGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HouseBlueprintCompilerTest {
    @Test
    void compilesHousePlanToBlueprintV2Shape() {
        var plan = new HousePlanGenerator().generatePreset(HouseStyle.MEDIEVAL_HOUSE);
        Blueprint blueprint = new HouseBlueprintCompiler().compile(plan);

        assertEquals(2, blueprint.getSchemaVersion());
        assertEquals(plan.housePlanId(), blueprint.getId());
        assertEquals(plan.footprint().width(), blueprint.getSize().width());
        assertEquals(plan.dimensions().totalHeight(), blueprint.getSize().height());
        assertEquals(plan.footprint().depth(), blueprint.getSize().depth());
        assertFalse(blueprint.getBlocks().isEmpty());
        assertFalse(blueprint.getPalette().isEmpty());
    }

    @Test
    void compilerKeepsBlocksInsideBounds() {
        var plan = new HousePlanGenerator().generatePreset(HouseStyle.WATCHTOWER_HOUSE);
        Blueprint blueprint = new HouseBlueprintCompiler().compile(plan);

        assertTrue(blueprint.getBlocks().stream().allMatch(block ->
                block.getX() >= 0
                        && block.getX() < blueprint.getSize().width()
                        && block.getY() >= 0
                        && block.getY() < blueprint.getSize().height()
                        && block.getZ() >= 0
                        && block.getZ() < blueprint.getSize().depth()
        ));
    }
}
