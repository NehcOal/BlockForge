package com.blockforge.common.serverplay;

public record AdminRollbackDecision(
        boolean allowed,
        String reason
) {
    public AdminRollbackDecision {
        reason = reason == null ? "" : reason;
    }
}
