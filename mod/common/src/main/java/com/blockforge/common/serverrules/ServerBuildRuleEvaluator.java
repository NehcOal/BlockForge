package com.blockforge.common.serverrules;

public final class ServerBuildRuleEvaluator {
    private ServerBuildRuleEvaluator() {
    }

    public static ServerRuleDecision canQueueBuilderStationJob(
            ServerBuildRules rules,
            int activePlansForPlayer,
            int queuedJobs,
            boolean hasAnchor
    ) {
        ServerBuildRules resolved = rules == null ? ServerBuildRules.defaults() : rules;
        if (!resolved.builderStationEnabled()) {
            return ServerRuleDecision.deny("Builder Station is disabled by server rules.", "Ask an administrator to enable builderStationEnabled.");
        }
        if (activePlansForPlayer >= resolved.maxActivePlansPerPlayer()) {
            return ServerRuleDecision.deny("Player has too many active BlockForge plans.", "Pause, cancel, or complete an existing plan first.");
        }
        if (queuedJobs >= resolved.maxQueuedJobs()) {
            return ServerRuleDecision.deny("Builder Station queue is full.", "Wait for current jobs to complete.");
        }
        if (resolved.requireAnchorForStationJobs() && !hasAnchor) {
            return ServerRuleDecision.deny("Builder Station jobs require a Builder Anchor.", "Bind a Builder Anchor before queueing this job.");
        }
        return ServerRuleDecision.allow();
    }
}
