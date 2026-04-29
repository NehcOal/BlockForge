package com.blockforge.common.serverplay;

public final class BuildQuotaChecker {
    private BuildQuotaChecker() {
    }

    public static BuildQuotaDecision canStart(BuildQuota quota, int requestedBlocks) {
        if (quota == null) {
            return new BuildQuotaDecision(true, "No quota configured.");
        }
        if (!quota.canStartJob()) {
            return new BuildQuotaDecision(false, "Active build job quota exceeded.");
        }
        if (!quota.canReserveBlocks(requestedBlocks)) {
            return new BuildQuotaDecision(false, "Daily block quota exceeded.");
        }
        return new BuildQuotaDecision(true, "Quota check passed.");
    }
}
