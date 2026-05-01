package com.blockforge.connector.gameplaygui;

import com.blockforge.common.gameplaygui.LoaderScreenRegistrationPlan;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoaderScreenRegistrationPlanTest {
    @Test
    void serverSafePlanDoesNotReferenceClientScreensFromCommonRegistration() {
        LoaderScreenRegistrationPlan plan = new LoaderScreenRegistrationPlan(
                "neoforge",
                false,
                true,
                true,
                List.of("MaterialCacheScreen", "BuilderStationScreen")
        );

        assertTrue(plan.dedicatedServerSafe());
        assertTrue(plan.warnings().isEmpty());
    }

    @Test
    void commonClientScreenReferenceIsUnsafeForDedicatedServer() {
        LoaderScreenRegistrationPlan plan = new LoaderScreenRegistrationPlan(
                "forge",
                true,
                false,
                false,
                List.of("BuilderStationScreen")
        );

        assertFalse(plan.dedicatedServerSafe());
        assertFalse(plan.warnings().isEmpty());
    }
}
