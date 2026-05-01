package com.blockforge.common.settlement.event;

public class SettlementEventScheduler {
    public boolean shouldRefresh(long lastRefreshGameTime, long currentGameTime, long refreshIntervalTicks) {
        return currentGameTime >= nextRefreshAt(lastRefreshGameTime, refreshIntervalTicks);
    }

    public long nextRefreshAt(long lastRefreshGameTime, long refreshIntervalTicks) {
        return lastRefreshGameTime + Math.max(1, refreshIntervalTicks);
    }
}
