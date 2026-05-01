package com.blockforge.common.serverplay;

public final class AdminRollbackPlanner {
    private AdminRollbackPlanner() {
    }

    public static AdminRollbackDecision decide(boolean snapshotAvailable, boolean hasPermission, boolean protectionAllowed, boolean enforceProtectionOnAdminRollback) {
        if (!hasPermission) {
            return new AdminRollbackDecision(false, "Missing blockforge.admin.rollback permission.");
        }
        if (!snapshotAvailable) {
            return new AdminRollbackDecision(false, "Rollback unavailable: no undo snapshot is available for this job.");
        }
        if (enforceProtectionOnAdminRollback && !protectionAllowed) {
            return new AdminRollbackDecision(false, "Rollback blocked by protection settings.");
        }
        return new AdminRollbackDecision(true, "Rollback can use the stored undo snapshot.");
    }
}
