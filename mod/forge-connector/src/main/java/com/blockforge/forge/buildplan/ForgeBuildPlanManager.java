package com.blockforge.forge.buildplan;

import com.blockforge.common.buildplan.BuildPlanManager;

public final class ForgeBuildPlanManager {
    private final BuildPlanManager delegate = new BuildPlanManager();

    public BuildPlanManager delegate() {
        return delegate;
    }
}
