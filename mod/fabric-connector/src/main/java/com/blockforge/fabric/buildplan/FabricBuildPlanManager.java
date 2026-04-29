package com.blockforge.fabric.buildplan;

import com.blockforge.common.buildplan.BuildPlanManager;

public final class FabricBuildPlanManager {
    private final BuildPlanManager delegate = new BuildPlanManager();

    public BuildPlanManager delegate() {
        return delegate;
    }
}
