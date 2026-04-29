package com.blockforge.common.serverrules;

public record ServerRuleDecision(
        boolean allowed,
        String reason,
        String suggestion
) {
    public ServerRuleDecision {
        reason = reason == null ? "" : reason;
        suggestion = suggestion == null ? "" : suggestion;
    }

    public static ServerRuleDecision allow() {
        return new ServerRuleDecision(true, "", "");
    }

    public static ServerRuleDecision deny(String reason, String suggestion) {
        return new ServerRuleDecision(false, reason, suggestion);
    }
}
