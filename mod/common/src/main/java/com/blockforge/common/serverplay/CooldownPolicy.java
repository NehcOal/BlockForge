package com.blockforge.common.serverplay;

public final class CooldownPolicy {
    private CooldownPolicy() {
    }

    public static BuildQuotaDecision check(BuildCooldown cooldown, long currentGameTime) {
        if (cooldown == null || cooldown.ready(currentGameTime)) {
            return new BuildQuotaDecision(true, "Cooldown check passed.");
        }
        long remainingSeconds = (long) Math.ceil(cooldown.remainingTicks(currentGameTime) / 20.0);
        return new BuildQuotaDecision(false, "Cooldown active. Try again in " + remainingSeconds + "s.");
    }
}
