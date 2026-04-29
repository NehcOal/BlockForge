package com.blockforge.common.serverplay;

public record BuildQuotaDecision(
        boolean allowed,
        String reason
) {
    public BuildQuotaDecision {
        reason = reason == null ? "" : reason;
    }
}
