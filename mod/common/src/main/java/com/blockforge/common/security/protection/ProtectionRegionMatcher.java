package com.blockforge.common.security.protection;

import com.blockforge.common.security.permission.PermissionCheckResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ProtectionRegionMatcher {
    private ProtectionRegionMatcher() {
    }

    public static ProtectionCheckResult check(
            ProtectionCheckRequest request,
            PermissionCheckResult bypassPermission
    ) {
        List<String> warnings = new ArrayList<>();
        List<String> denied = new ArrayList<>();

        if (request.area() == null) {
            return ProtectionCheckResult.denied(List.of("missing_area"), warnings);
        }

        for (BlockForgeProtectionRegion region : request.regions()) {
            if (!sameDimension(region.dimensionId(), request.dimensionId())) {
                continue;
            }
            if (!intersects(request.area(), region)) {
                continue;
            }
            if (region.mode() == BlockForgeRegionMode.ALLOW) {
                continue;
            }
            if (isListed(region.allowedPlayers(), request.playerId(), request.playerName())) {
                continue;
            }
            if (bypassPermission != null && bypassPermission.allowed()
                    && region.allowedPermissions().contains(bypassPermission.node())) {
                continue;
            }
            denied.add(region.id());
        }

        if (!denied.isEmpty()) {
            return ProtectionCheckResult.denied(denied, warnings);
        }
        return ProtectionCheckResult.allowed(warnings);
    }

    public static boolean intersects(BuildArea area, BlockForgeProtectionRegion region) {
        return area.minX() <= region.maxX()
                && area.maxX() >= region.minX()
                && area.minY() <= region.maxY()
                && area.maxY() >= region.minY()
                && area.minZ() <= region.maxZ()
                && area.maxZ() >= region.minZ();
    }

    private static boolean sameDimension(String a, String b) {
        return normalize(a).equals(normalize(b));
    }

    private static boolean isListed(List<String> values, java.util.UUID playerId, String playerName) {
        String id = playerId == null ? "" : playerId.toString().toLowerCase(Locale.ROOT);
        String name = playerName == null ? "" : playerName.toLowerCase(Locale.ROOT);
        return values.stream()
                .map(ProtectionRegionMatcher::normalize)
                .anyMatch(value -> value.equals(id) || value.equals(name));
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
