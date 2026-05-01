package com.blockforge.fabric.security;

import com.blockforge.common.security.permission.BlockForgePermissionAction;
import com.blockforge.common.security.permission.BlockForgePermissionNode;
import com.blockforge.common.security.permission.PermissionCheckRequest;
import com.blockforge.common.security.permission.PermissionCheckResult;
import net.minecraft.server.network.ServerPlayerEntity;

public class FabricPermissionService {
    public PermissionCheckResult check(ServerPlayerEntity player, BlockForgePermissionAction action) {
        BlockForgePermissionNode node = action.node();
        if (player == null) {
            return PermissionCheckResult.denied(node.node(), "This action requires a player.", true);
        }
        if (!FabricSecuritySettings.requirePermissions()) {
            return PermissionCheckResult.allowed(node.node(), true);
        }
        boolean allowed = hasPermission(player, node.node(), node.fallbackOpLevel());
        if (allowed) {
            return PermissionCheckResult.allowed(node.node(), true);
        }
        return PermissionCheckResult.denied(node.node(), "Missing permission: " + node.node(), true);
    }

    public PermissionCheckResult check(ServerPlayerEntity player, String node, int fallbackOpLevel) {
        if (player == null) {
            return PermissionCheckResult.denied(node, "This action requires a player.", true);
        }
        boolean allowed = !FabricSecuritySettings.requirePermissions() || hasPermission(player, node, fallbackOpLevel);
        return allowed
                ? PermissionCheckResult.allowed(node, true)
                : PermissionCheckResult.denied(node, "Missing permission: " + node, true);
    }

    public boolean hasPermission(ServerPlayerEntity player, String node, int fallbackOpLevel) {
        return player != null && player.hasPermissionLevel(fallbackOpLevel);
    }

    public PermissionCheckRequest request(ServerPlayerEntity player, BlockForgePermissionAction action) {
        return PermissionCheckRequest.of(player.getUuid(), player.getName().getString(), action);
    }
}
