package com.blockforge.connector.buildplan;

import com.blockforge.common.buildplan.BuildIssue;
import com.blockforge.common.buildplan.BuildLayer;
import com.blockforge.common.buildplan.BuildPlanValidator;
import com.blockforge.common.buildplan.BuildStep;
import com.blockforge.common.buildplan.BuildStepStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuildPlanValidatorTest {
    @Test
    void reportsDuplicateOutOfWorldAndMissingPalette() {
        List<BuildIssue> issues = BuildPlanValidator.validateLayers(
                List.of(new BuildLayer(400, List.of(
                        new BuildStep(1, 400, 1, "minecraft:stone", "stone", "stone", false, BuildStepStatus.PENDING),
                        new BuildStep(1, 400, 1, "", "missing", "missing", false, BuildStepStatus.PENDING)
                ), 2)),
                0,
                319
        );

        assertEquals(4, issues.size());
    }
}
