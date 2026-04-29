package com.blockforge.connector.buildplan;

import com.blockforge.common.buildplan.BuildPlanManager;

public final class NeoForgeBuildPlanManager {
    private final BuildPlanManager delegate = new BuildPlanManager();

    public BuildPlanManager delegate() {
        return delegate;
    }
}
