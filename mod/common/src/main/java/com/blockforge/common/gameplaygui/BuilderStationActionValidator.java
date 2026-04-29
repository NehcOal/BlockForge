package com.blockforge.common.gameplaygui;

public final class BuilderStationActionValidator {
    private BuilderStationActionValidator() {
    }

    public static BuilderStationActionDecision validate(BuilderStationStatusView view, BuilderStationAction action, boolean hasPermission, boolean quotaAllowed, boolean cooldownReady) {
        if (view == null) {
            return BuilderStationActionDecision.deny("Station state is not available.");
        }
        if (action == null) {
            return BuilderStationActionDecision.deny("Unknown station action.");
        }
        if (!hasPermission) {
            return BuilderStationActionDecision.deny("You do not have permission to control this station.");
        }
        if ((action == BuilderStationAction.START || action == BuilderStationAction.STEP || action == BuilderStationAction.RESUME) && !quotaAllowed) {
            return BuilderStationActionDecision.deny("Server quota denied this station action.");
        }
        if ((action == BuilderStationAction.START || action == BuilderStationAction.STEP) && !cooldownReady) {
            return BuilderStationActionDecision.deny("Station cooldown is still active.");
        }

        return switch (action) {
            case CREATE_PLAN -> view.canCreatePlan()
                    ? BuilderStationActionDecision.allow("Station can create a build plan.")
                    : BuilderStationActionDecision.deny("Bind a blueprint and anchor before creating a plan.");
            case START -> view.canStart()
                    ? BuilderStationActionDecision.allow("Station can start.")
                    : BuilderStationActionDecision.deny("Station is not ready to start.");
            case PAUSE -> view.canPause()
                    ? BuilderStationActionDecision.allow("Station can pause.")
                    : BuilderStationActionDecision.deny("Station is not running.");
            case RESUME -> view.canResume()
                    ? BuilderStationActionDecision.allow("Station can resume.")
                    : BuilderStationActionDecision.deny("Station is not paused.");
            case STEP -> view.canStep()
                    ? BuilderStationActionDecision.allow("Station can execute one batch.")
                    : BuilderStationActionDecision.deny("Station has no step-ready job.");
            case CANCEL -> view.canCancel()
                    ? BuilderStationActionDecision.allow("Station job can be cancelled.")
                    : BuilderStationActionDecision.deny("Station has no cancellable job.");
            case CLEAR -> view.status().name().equals("RUNNING")
                    ? BuilderStationActionDecision.deny("Pause or cancel the running job before clearing.")
                    : BuilderStationActionDecision.allow("Station state can be cleared.");
            case REFRESH -> BuilderStationActionDecision.allow("Station state can be refreshed.");
        };
    }
}
