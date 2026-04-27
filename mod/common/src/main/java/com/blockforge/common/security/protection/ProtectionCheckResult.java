package com.blockforge.common.security.protection;

import java.util.List;

public record ProtectionCheckResult(
        boolean allowed,
        List<String> deniedRegionIds,
        List<String> warnings,
        String reason
) {
    public ProtectionCheckResult {
        deniedRegionIds = deniedRegionIds == null ? List.of() : List.copyOf(deniedRegionIds);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
        reason = reason == null ? "" : reason;
    }

    public static ProtectionCheckResult allowed(List<String> warnings) {
        return new ProtectionCheckResult(true, List.of(), warnings, "");
    }

    public static ProtectionCheckResult denied(List<String> deniedRegionIds, List<String> warnings) {
        String reason = deniedRegionIds == null || deniedRegionIds.isEmpty()
                ? "Denied by protection region."
                : "Build denied by protection region: " + String.join(", ", deniedRegionIds);
        return new ProtectionCheckResult(false, deniedRegionIds, warnings, reason);
    }
}
