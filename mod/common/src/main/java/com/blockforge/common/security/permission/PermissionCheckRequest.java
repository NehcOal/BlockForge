package com.blockforge.common.security.permission;

import java.util.UUID;

public record PermissionCheckRequest(
        UUID playerId,
        String playerName,
        BlockForgePermissionAction action,
        String node,
        int fallbackOpLevel
) {
    public static PermissionCheckRequest of(UUID playerId, String playerName, BlockForgePermissionAction action) {
        return new PermissionCheckRequest(
                playerId,
                playerName == null ? "" : playerName,
                action,
                action.node().node(),
                action.node().fallbackOpLevel()
        );
    }
}
