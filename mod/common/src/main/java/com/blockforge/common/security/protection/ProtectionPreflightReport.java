package com.blockforge.common.security.protection;

import com.blockforge.common.security.permission.PermissionCheckResult;

import java.util.List;

public record ProtectionPreflightReport(
        boolean allowed,
        PermissionCheckResult permission,
        ProtectionCheckResult protection,
        int checkedBlocks,
        int deniedBlocks,
        List<String> warnings
) {
    public ProtectionPreflightReport {
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }

    public String reason() {
        if (permission != null && !permission.allowed()) {
            return permission.reason().isBlank()
                    ? "Missing permission: " + permission.node()
                    : permission.reason();
        }
        if (protection != null && !protection.allowed()) {
            return protection.reason();
        }
        return "";
    }
}
