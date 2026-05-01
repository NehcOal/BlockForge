package com.blockforge.common.settlement.event;

public final class SettlementEventConfigDefaults {
    public static final boolean ENABLE_SETTLEMENT_EVENTS = true;
    public static final int MAX_ACTIVE_EVENTS_PER_SETTLEMENT = 3;
    public static final long EVENT_REFRESH_INTERVAL_TICKS = 24000L;
    public static final boolean ALLOW_CRITICAL_EVENTS = true;
    public static final long CRITICAL_EVENT_COOLDOWN_TICKS = 72000L;
    public static final boolean ENABLE_PROJECT_CHAINS = true;
    public static final int MAX_ACTIVE_PROJECTS_PER_SETTLEMENT = 2;
    public static final boolean ENABLE_EMERGENCY_REPAIRS = true;
    public static final long EMERGENCY_REPAIR_TIMEOUT_TICKS = 72000L;
    public static final boolean EVENT_REWARDS_ENABLED = true;
    public static final boolean EVENT_FAILURE_PENALTIES_ENABLED = true;
    public static final int MIN_STABILITY_FOR_POSITIVE_EVENTS = 60;
    public static final int STABILITY_WARNING_THRESHOLD = 30;

    private SettlementEventConfigDefaults() {
    }
}
