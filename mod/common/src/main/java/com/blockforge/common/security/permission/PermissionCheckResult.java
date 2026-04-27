package com.blockforge.common.security.permission;

public record PermissionCheckResult(boolean allowed, String node, String reason, boolean usedFallback) {
    public static PermissionCheckResult allowed(String node, boolean usedFallback) {
        return new PermissionCheckResult(true, node, "", usedFallback);
    }

    public static PermissionCheckResult denied(String node, String reason, boolean usedFallback) {
        return new PermissionCheckResult(false, node, reason == null ? "" : reason, usedFallback);
    }
}
