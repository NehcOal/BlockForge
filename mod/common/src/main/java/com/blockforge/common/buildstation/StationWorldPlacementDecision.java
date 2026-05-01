package com.blockforge.common.buildstation;

public record StationWorldPlacementDecision(
        boolean allowed,
        boolean pauseJob,
        boolean failJob,
        String issueType,
        String message
) {
    public StationWorldPlacementDecision {
        issueType = issueType == null ? "" : issueType;
        message = message == null ? "" : message;
    }

    public static StationWorldPlacementDecision allow() {
        return new StationWorldPlacementDecision(true, false, false, "", "Station batch can place blocks.");
    }

    public static StationWorldPlacementDecision pause(String issueType, String message) {
        return new StationWorldPlacementDecision(false, true, false, issueType, message);
    }

    public static StationWorldPlacementDecision fail(String issueType, String message) {
        return new StationWorldPlacementDecision(false, false, true, issueType, message);
    }
}
