package com.blockforge.common.security.protection;

import com.blockforge.common.security.permission.PermissionCheckResult;

import java.util.ArrayList;
import java.util.List;

public final class ProtectionPreflightService {
    private ProtectionPreflightService() {
    }

    public static ProtectionPreflightReport combine(
            PermissionCheckResult permission,
            ProtectionCheckResult protection,
            int checkedBlocks
    ) {
        List<String> warnings = new ArrayList<>();
        if (protection != null) {
            warnings.addAll(protection.warnings());
        }

        if (permission != null && !permission.allowed()) {
            return new ProtectionPreflightReport(false, permission, protection, checkedBlocks, checkedBlocks, warnings);
        }
        if (protection != null && !protection.allowed()) {
            return new ProtectionPreflightReport(false, permission, protection, checkedBlocks, checkedBlocks, warnings);
        }

        return new ProtectionPreflightReport(true, permission, protection, checkedBlocks, 0, warnings);
    }
}
