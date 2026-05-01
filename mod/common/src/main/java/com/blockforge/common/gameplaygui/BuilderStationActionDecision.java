package com.blockforge.common.gameplaygui;

public record BuilderStationActionDecision(
        boolean allowed,
        String reason
) {
    public BuilderStationActionDecision {
        reason = reason == null ? "" : reason;
    }

    public static BuilderStationActionDecision allow(String reason) {
        return new BuilderStationActionDecision(true, reason);
    }

    public static BuilderStationActionDecision deny(String reason) {
        return new BuilderStationActionDecision(false, reason);
    }
}
